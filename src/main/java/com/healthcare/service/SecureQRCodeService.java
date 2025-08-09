package com.healthcare.service;

import com.healthcare.model.ConsentRecord;
import com.healthcare.model.Patient;
import com.healthcare.model.QRShareSession;
import com.healthcare.repository.ConsentRepository;
import com.healthcare.repository.QRShareSessionRepository;
import com.healthcare.util.EncryptionService;
import com.healthcare.util.QRCodeGenerator;
import com.healthcare.util.ConsentException;
import com.healthcare.dto.QRShareRequest;
import com.healthcare.dto.QRCodeResponse;
import com.healthcare.dto.QRPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecureQRCodeService {
    
    private final QRShareSessionRepository sessionRepository;
    private final ConsentRepository consentRepository;
    private final EncryptionService encryptionService;
    private final ConsentService consentService;
    private final QRCodeGenerator qrCodeGenerator;
    private final AuditService auditService;
    
    @Transactional
    public QRCodeResponse generateSecureQRCode(String patientId, QRShareRequest request) {
        log.info("Generating secure QR code for patient: {}", patientId);
        
        // Skip consent validation for development
        // validateConsent(patientId, request);
        
        // Validate consent
        if (!consentService.hasValidConsent(patientId, request.getDataTypes())) {
            throw new ConsentException("Patient consent required for requested data types");
        }
        
        // Create QR session
        QRShareSession session = QRShareSession.builder()
            .id(UUID.randomUUID().toString())
            .patient(Patient.builder().id(patientId).build())
            .sessionToken(generateSecureToken())
            .sharedDataTypes(request.getDataTypes())
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(request.getExpirationMinutes()))
            .status(QRShareSession.SessionStatus.ACTIVE)
            .build();
        
        // Create encrypted payload
        QRPayload payload = QRPayload.builder()
            .sessionToken(session.getSessionToken())
            .patientId(patientId)
            .dataTypes(request.getDataTypes())
            .expiresAt(session.getExpiresAt())
            .build();
        
        String encryptedPayload = encryptionService.encrypt(payload.toJson());
        byte[] qrCodeBytes = qrCodeGenerator.generateQRCode(encryptedPayload);
        
        session.setQrCodeData(encryptedPayload);
        sessionRepository.save(session);
        
        // Audit log
        auditService.logQRGeneration(patientId, request.getDataTypes());
        
        return QRCodeResponse.builder()
            .sessionId(session.getId())
            .qrCodeImage(qrCodeBytes)
            .expiresAt(session.getExpiresAt())
            .build();
    }
    
    private void validateConsent(String patientId, QRShareRequest request) {
        var activeConsents = consentRepository.findActiveConsentsByPatientId(patientId, LocalDateTime.now());
        
        if (activeConsents.isEmpty()) {
            throw new ConsentException("No active consent found for patient");
        }
        
        // Validate each requested data type has consent
        request.getDataTypes().forEach(dataType -> {
            boolean hasConsent = activeConsents.stream()
                .anyMatch(consent -> consent.getAllowedDataTypes().contains(dataType));
            
            if (!hasConsent) {
                throw new ConsentException("No consent found for data type: " + dataType);
            }
        });
    }
    
    private String generateSecureToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }
    
    public Object getSessionStatus(String sessionId) {
        return "Session status";
    }
    
    public void cancelSession(String sessionId) {
        // Cancel session implementation
    }
}