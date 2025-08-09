package com.healthcare.testutil;

import com.healthcare.model.ConsentRecord;
import com.healthcare.model.Patient;
import com.healthcare.model.QRShareSession;

import java.time.LocalDateTime;
import java.util.List;

public class TestDataBuilder {

    public static Patient.PatientBuilder defaultPatient() {
        return Patient.builder()
            .fhirId("Patient/test-123")
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@test.com")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now());
    }

    public static ConsentRecord.ConsentRecordBuilder defaultConsent() {
        return ConsentRecord.builder()
            .allowedDataTypes(List.of(
                ConsentRecord.DataType.DEMOGRAPHICS,
                ConsentRecord.DataType.VITALS,
                ConsentRecord.DataType.MEDICATIONS
            ))
            .status(ConsentRecord.ConsentStatus.ACTIVE)
            .grantedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(30))
            .granteeId("provider-123")
            .purpose("Healthcare treatment");
    }

    public static QRShareSession.QRShareSessionBuilder defaultQRSession() {
        return QRShareSession.builder()
            .sessionToken("test-session-token")
            .sharedDataTypes(List.of(ConsentRecord.DataType.DEMOGRAPHICS))
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(15))
            .status(QRShareSession.SessionStatus.ACTIVE);
    }
}