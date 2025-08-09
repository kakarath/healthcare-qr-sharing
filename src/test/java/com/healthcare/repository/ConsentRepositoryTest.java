package com.healthcare.repository;

import com.healthcare.model.ConsentRecord;
import com.healthcare.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ConsentRepositoryTest {

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
            .fhirId("Patient/test-123")
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .build();
        
        testPatient = patientRepository.save(testPatient);
    }

    @Test
    void findActiveConsentsByPatientId_WithActiveConsents_ShouldReturnConsents() {
        // Given
        ConsentRecord activeConsent = ConsentRecord.builder()
            .patient(testPatient)
            .status(ConsentRecord.ConsentStatus.ACTIVE)
            .allowedDataTypes(List.of(ConsentRecord.DataType.DEMOGRAPHICS))
            .grantedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(30))
            .granteeId("provider-123")
            .build();
        
        ConsentRecord expiredConsent = ConsentRecord.builder()
            .patient(testPatient)
            .status(ConsentRecord.ConsentStatus.ACTIVE)
            .allowedDataTypes(List.of(ConsentRecord.DataType.VITALS))
            .grantedAt(LocalDateTime.now().minusDays(60))
            .expiresAt(LocalDateTime.now().minusDays(30))
            .granteeId("provider-456")
            .build();
        
        consentRepository.save(activeConsent);
        consentRepository.save(expiredConsent);

        // When
        List<ConsentRecord> activeConsents = consentRepository
            .findActiveConsentsByPatientId(testPatient.getId(), LocalDateTime.now());

        // Then
        assertEquals(1, activeConsents.size());
        assertEquals("provider-123", activeConsents.get(0).getGranteeId());
    }

    @Test
    void findActiveConsentsByPatientAndGrantee_ShouldReturnMatchingConsents() {
        // Given
        String granteeId = "provider-123";
        ConsentRecord consent1 = ConsentRecord.builder()
            .patient(testPatient)
            .status(ConsentRecord.ConsentStatus.ACTIVE)
            .allowedDataTypes(List.of(ConsentRecord.DataType.DEMOGRAPHICS))
            .grantedAt(LocalDateTime.now())
            .granteeId(granteeId)
            .build();
        
        ConsentRecord consent2 = ConsentRecord.builder()
            .patient(testPatient)
            .status(ConsentRecord.ConsentStatus.ACTIVE)
            .allowedDataTypes(List.of(ConsentRecord.DataType.VITALS))
            .grantedAt(LocalDateTime.now())
            .granteeId("different-provider")
            .build();
        
        consentRepository.save(consent1);
        consentRepository.save(consent2);

        // When
        List<ConsentRecord> results = consentRepository
            .findActiveConsentsByPatientAndGrantee(testPatient.getId(), granteeId);

        // Then
        assertEquals(1, results.size());
        assertEquals(granteeId, results.get(0).getGranteeId());
        assertEquals(ConsentRecord.DataType.DEMOGRAPHICS, results.get(0).getAllowedDataTypes().get(0));
    }
}