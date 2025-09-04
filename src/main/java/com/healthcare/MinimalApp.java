package com.healthcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import com.healthcare.util.QRCodeGenerator;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@RestController
public class MinimalApp {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email";
    private static final String FHIR_ID = "fhirId";
    private static final String SUCCESS = "success";

    public static void main(String[] args) {
        SpringApplication.run(MinimalApp.class, args);
    }

    @PostMapping("/api/qr/generate")
    public Map<String, Object> generateQR() {
        Map<String, Object> response = new HashMap<>();
        String sessionId = "session-" + System.currentTimeMillis();
        
        try {
            // Encode the content into a QR code
            String qrContent = "Session ID: " + sessionId;
            String base64QrCode = QRCodeGenerator.generateQRCodeBase64(qrContent, 200, 200);

            response.put("sessionId", sessionId);
            response.put("qrCodeImage", base64QrCode);
            response.put("expiresAt", "2025-09-04T13:00:00"); // Example timestamp
            response.put(SUCCESS, true);
            response.put("message", "QR Generated Successfully!");
        } catch (Exception e) {
            response.put(SUCCESS, false);
            response.put("message", "Failed to generate QR code: " + e.getMessage());
        }

        return response;
    }

    // Other methods remain the same...
    @DeleteMapping("/api/qr/session/{sessionId}")
    public Map<String, Object> cancelSession(@PathVariable String sessionId) {
        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS, true);
        response.put("message", "Session cancelled");
        return response;
    }

    @PostMapping("/api/patients")
    public Map<String, Object> createPatient(@RequestBody Map<String, Object> patient) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", System.currentTimeMillis());
        response.put(FIRST_NAME, patient.get(FIRST_NAME));
        response.put(LAST_NAME, patient.get(LAST_NAME));
        response.put(EMAIL, patient.get(EMAIL));
        response.put(FHIR_ID, patient.get(FHIR_ID));
        response.put(SUCCESS, true);
        return response;
    }

    @GetMapping("/api/patients/{id}")
    public Map<String, Object> getPatient(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put(FIRST_NAME, "John");
        response.put(LAST_NAME, "Doe");
        response.put(EMAIL, "john.doe@example.com");
        response.put(FHIR_ID, "patient-" + id);
        return response;
    }
}
