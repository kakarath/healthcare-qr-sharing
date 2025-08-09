package com.healthcare.repository;

import com.healthcare.model.QRShareSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QRShareSessionRepository extends JpaRepository<QRShareSession, String> {
    
    Optional<QRShareSession> findBySessionToken(String sessionToken);
    
    @Query("SELECT q FROM QRShareSession q WHERE q.patient.id = ?1 AND q.status = 'ACTIVE' AND q.expiresAt > ?2")
    List<QRShareSession> findActiveSessionsByPatientId(String patientId, LocalDateTime now);
    
    @Query("SELECT q FROM QRShareSession q WHERE q.expiresAt < ?1 AND q.status = 'ACTIVE'")
    List<QRShareSession> findExpiredSessions(LocalDateTime now);
}