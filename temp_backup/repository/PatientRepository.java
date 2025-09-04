package com.healthcare.repository;

import com.healthcare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    
    Optional<Patient> findByEmail(String email);
    
    Optional<Patient> findByFhirId(String fhirId);
    
    @Query("SELECT p FROM Patient p WHERE p.email = ?1 AND p.createdAt > CURRENT_DATE - 1")
    Optional<Patient> findRecentPatientByEmail(String email);
}