package com.healthcare.service;

import com.healthcare.model.ConsentRecord;
import com.healthcare.model.Patient;
import com.healthcare.model.QRShareSession;
import com.healthcare.repository.ConsentRepository;
import com.healthcare.repository.QRShareSessionRepository;
import com.healthcare.util.EncryptionService;
import com.healthcare.util.QRCodeGenerator;
import com.healthcare.dto.QRShareRequest;
import com.healthcare.util.ConsentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecureQRCodeServiceTest {

    @Mock
    private QRShareSessionRepository sessionRepository;
    
    @Mock
    private ConsentRepository consentRepository;
    
    @Mock
    private EncryptionService encryptionService;
    
    @Mock
    private QRCodeGenerator qrCodeGenerator;
    
    @Mock
    private AuditService auditService;
    
    @Mock
    private ConsentService consentService;

    @InjectMocks
    private SecureQRCodeService qrCodeService;

    private QRShareRequest validRequest;
    private ConsentRecord validConsent;

    @BeforeEach
    void setUp() {
        validRequest = new QRShareRequest();
        validRequest.setDataTypes(List.of(ConsentRecord.DataType.DEMOGRAPHICS, ConsentRecord.DataType.VITALS));
        validRequest.setExpirationMinutes(15);
        validRequest.setPurpose("Emergency room visit");

        validConsent = ConsentRecord.builder()
            .id("consent-123")
            .allowedDataTypes(List.of(ConsentRecord.DataType.DEMOGRAPHICS, ConsentRecord.DataType.VITALS))
            .status(ConsentRecord.ConsentStatus.ACTIVE)
            .grantedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(30))
            .build();
    }

    @Test
    void generateSecureQRCode_WithValidConsent_ShouldReturnQRCodeResponse() {
        // Given
        String patientId = "patient-123";
        when(consentService.hasValidConsent(eq(patientId), any(List.class))).thenReturn(true);
        when(encryptionService.encrypt(anyString())).thenReturn("encrypted-payload");
        when(qrCodeGenerator.generateQRCode(anyString())).thenReturn(new byte[]{1, 2, 3});
        when(sessionRepository.save(any(QRShareSession.class))).thenAnswer(i -> i.getArgument(0));

        // When
        var response = qrCodeService.generateSecureQRCode(patientId, validRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getQrCodeImage());
        assertNotNull(response.getExpiresAt());
        verify(auditService).logQRGeneration(patientId, validRequest.getDataTypes());
        verify(sessionRepository).save(any(QRShareSession.class));
    }

    @Test
    void generateSecureQRCode_WithoutConsent_ShouldThrowConsentException() {
        // Given
        String patientId = "patient-123";
        when(consentService.hasValidConsent(eq(patientId), any(List.class))).thenReturn(false);

        // When & Then
        assertThrows(ConsentException.class, 
            () -> qrCodeService.generateSecureQRCode(patientId, validRequest));
        verify(sessionRepository, never()).save(any());
        verify(auditService, never()).logQRGeneration(anyString(), anyList());
    }

    @Test
    void generateSecureQRCode_WithPartialConsent_ShouldThrowConsentException() {
        // Given
        String patientId = "patient-123";
        when(consentService.hasValidConsent(eq(patientId), any(List.class))).thenReturn(false);

        // When & Then
        assertThrows(ConsentException.class, 
            () -> qrCodeService.generateSecureQRCode(patientId, validRequest));
    }
}