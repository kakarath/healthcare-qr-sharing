package com.healthcare.controller;

import com.healthcare.config.TestSecurityConfig;
import com.healthcare.model.ConsentRecord;
import com.healthcare.model.Patient;
import com.healthcare.repository.ConsentRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.dto.QRShareRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
class QRControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ConsentRepository consentRepository;

    private Patient testPatient;
    private ConsentRecord testConsent;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
            .id("test-patient-123")
            .fhirId("Patient/test-patient-123")
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .build();
        
        testPatient = patientRepository.save(testPatient);

        testConsent = ConsentRecord.builder()
            .patient(testPatient)
            .allowedDataTypes(List.of(
                ConsentRecord.DataType.DEMOGRAPHICS, 
                ConsentRecord.DataType.VITALS,
                ConsentRecord.DataType.MEDICATIONS
            ))
            .status(ConsentRecord.ConsentStatus.ACTIVE)
            .grantedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(30))
            .granteeId("provider-123")
            .purpose("Healthcare treatment")
            .build();
        
        consentRepository.save(testConsent);
    }

    @Test
    void generateQRCode_WithValidRequest_ShouldReturnQRCodeResponse() throws Exception {
        // Given
        QRShareRequest request = new QRShareRequest();
        request.setDataTypes(List.of(ConsentRecord.DataType.DEMOGRAPHICS, ConsentRecord.DataType.VITALS));
        request.setExpirationMinutes(15);
        request.setPurpose("Emergency visit");

        // When & Then
        mockMvc.perform(post("/api/qr/generate")
                .with(jwt().jwt(jwt -> jwt.subject("test-patient-123").claim("scope", "patient/*.write")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.qrCodeImage").exists())
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    void generateQRCode_WithoutConsent_ShouldReturnBadRequest() throws Exception {
        // Given
        QRShareRequest request = new QRShareRequest();
        request.setDataTypes(List.of(ConsentRecord.DataType.LAB_RESULTS)); // No consent for this
        request.setExpirationMinutes(15);

        // When & Then
        mockMvc.perform(post("/api/qr/generate")
                .with(jwt().jwt(jwt -> jwt.subject("test-patient-123").claim("scope", "patient/*.write")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateQRCode_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Given
        QRShareRequest request = new QRShareRequest();
        request.setDataTypes(List.of(ConsentRecord.DataType.DEMOGRAPHICS));
        request.setExpirationMinutes(15);

        // When & Then
        mockMvc.perform(post("/api/qr/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void generateQRCode_WithInvalidDataTypes_ShouldReturnBadRequest() throws Exception {
        // Given
        QRShareRequest request = new QRShareRequest();
        request.setDataTypes(List.of()); // Empty list
        request.setExpirationMinutes(15);

        // When & Then
        mockMvc.perform(post("/api/qr/generate")
                .with(jwt().jwt(jwt -> jwt.subject("test-patient-123").claim("scope", "patient/*.write")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}