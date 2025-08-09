package com.healthcare.repository;

import com.healthcare.model.ConsentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsentRepository extends JpaRepository<ConsentRecord, String> {
    
    @Query("SELECT c FROM ConsentRecord c WHERE c.patient.id = ?1 AND c.status = 'ACTIVE' AND (c.expiresAt IS NULL OR c.expiresAt > ?2)")
    List<ConsentRecord> findActiveConsentsByPatientId(String patientId, LocalDateTime now);
    
    @Query("SELECT c FROM ConsentRecord c WHERE c.patient.id = ?1 AND c.granteeId = ?2 AND c.status = 'ACTIVE'")
    List<ConsentRecord> findActiveConsentsByPatientAndGrantee(String patientId, String granteeId);
    
    List<ConsentRecord> findByPatientIdAndStatus(String patientId, ConsentRecord.ConsentStatus status);
    
    List<ConsentRecord> findByPatientId(String patientId);
}