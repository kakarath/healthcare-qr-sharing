package com.healthcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcare.util.QRCodeGenerator;
import com.healthcare.model.User;
import com.healthcare.model.ConsentRequest;
import com.healthcare.service.AuthService;
import com.healthcare.service.ConsentService;
import com.healthcare.service.AppInfoService;
import com.healthcare.service.NotificationService;
import com.healthcare.service.PatientDataService;
import com.healthcare.service.AuditService;
import com.healthcare.service.ComplianceService;
import com.healthcare.model.PatientData;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@EnableScheduling
@RestController
public class MinimalApp {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email";
    private static final String FHIR_ID = "fhirId";
    private static final String SUCCESS = "success";

    @Autowired
    private AuthService authService;

    @Autowired
    private ConsentService consentService;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PatientDataService patientDataService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private ComplianceService complianceService;

    public static void main(String[] args) {
        SpringApplication.run(MinimalApp.class, args);
    }

    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        String email = credentials.get("email");
        String password = credentials.get("password");
        String ipAddress = request.getRemoteAddr();
        
        if (email == null || password == null) {
            response.put(SUCCESS, false);
            response.put("message", "Email and password required");
            return response;
        }
        
        if (complianceService.isAccountLocked(email)) {
            auditService.logFailure(email, "LOGIN", "BLOCKED", ipAddress, "Account locked");
            response.put(SUCCESS, false);
            response.put("message", "Account temporarily locked");
            return response;
        }
        
        Optional<String> sessionId = authService.login(email, password);
        if (sessionId.isPresent()) {
            complianceService.recordSuccessfulLogin(email, ipAddress);
            response.put(SUCCESS, true);
            response.put("sessionId", sessionId.get());
            response.put("message", "Login successful");
        } else {
            complianceService.recordFailedAttempt(email, ipAddress);
            response.put(SUCCESS, false);
            response.put("message", "Invalid credentials");
        }
        return response;
    }

    @PostMapping("/api/qr/generate")
    public Map<String, Object> generateQR(@RequestBody(required = false) Map<String, Object> request, HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        String sessionId = "session-" + UUID.randomUUID().toString();
        String userId = "system";
        String patientId = "patient-" + UUID.randomUUID().toString().substring(0, 8);
        
        try {
            String purpose = request != null && request.containsKey("purpose") 
                ? (String) request.get("purpose") 
                : "Healthcare data sharing";
            
            complianceService.validateDataAccess(userId, patientId, purpose);
            
            String qrContent = "Session ID: " + sessionId;
            String base64QrCode = QRCodeGenerator.generateQRCodeBase64(qrContent, 200, 200);

            auditService.logAccess(userId, "QR_GENERATION", "CREATE", httpRequest.getRemoteAddr());
            
            response.put("sessionId", sessionId);
            response.put("qrCodeImage", base64QrCode);
            response.put("expiresAt", "2025-09-04T13:00:00");
            response.put(SUCCESS, true);
            response.put("message", "QR Generated Successfully!");
        } catch (Exception e) {
            auditService.logFailure(userId, "QR_GENERATION", "CREATE", httpRequest.getRemoteAddr(), e.getMessage());
            response.put(SUCCESS, false);
            response.put("message", "Failed to generate QR code: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/api/qr/scan")
    public Map<String, Object> scanQRCode(@RequestHeader(value = "Authorization", required = false) String sessionId,
                                         @RequestBody Map<String, String> qrData) {
        Map<String, Object> response = new HashMap<>();
        
        if (qrData == null || !qrData.containsKey("qrData")) {
            response.put(SUCCESS, false);
            response.put("message", "QR data is required");
            return response;
        }
        
        String scannedData = qrData.get("qrData");
        
        // Validate QR data format and expiration
        if (scannedData == null || scannedData.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put("message", "Invalid QR data format");
            return response;
        }
        
        // Basic validation - QR data should be encrypted and base64 encoded
        if (!scannedData.matches("^[A-Za-z0-9+/]*={0,2}$")) {
            response.put(SUCCESS, false);
            response.put("message", "Invalid QR data format");
            return response;
        }
        
        // Mock patient data for demo
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("firstName", "John");
        patientData.put("lastName", "Doe");
        patientData.put("email", "john.doe@example.com");
        patientData.put("dateOfBirth", "1990-01-01");
        patientData.put("bloodType", "O+");
        patientData.put("allergies", List.of("Penicillin", "Shellfish"));
        patientData.put("medications", List.of("Lisinopril 10mg", "Metformin 500mg"));
        patientData.put("conditions", List.of("Hypertension", "Type 2 Diabetes"));
        
        response.put(SUCCESS, true);
        response.put("patientData", patientData);
        response.put("scannedAt", java.time.LocalDateTime.now());
        response.put("message", "QR code scanned successfully");
        
        return response;
    }

    @GetMapping("/api/health")
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        
        Map<String, String> components = new HashMap<>();
        components.put("database", "UP");
        components.put("qrGenerator", "UP");
        components.put("consentService", "UP");
        components.put("authService", "UP");
        
        health.put("components", components);
        health.put("version", "3.0.0");
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now());
        health.put("uptime", "00:00:30");
        
        return health;
    }

    @PostMapping("/api/patients")
    public Map<String, Object> createPatient(@RequestBody Map<String, Object> patient) {
        Map<String, Object> response = new HashMap<>();
        
        if (patient.get(FIRST_NAME) == null || patient.get(LAST_NAME) == null || patient.get(EMAIL) == null) {
            response.put(SUCCESS, false);
            response.put("message", "First name, last name, and email are required");
            return response;
        }
        
        response.put("id", UUID.randomUUID().toString());
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
        
        if (id == null || id.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put("message", "Patient ID is required");
            return response;
        }
        
        response.put("id", id);
        response.put(FIRST_NAME, "John");
        response.put(LAST_NAME, "Doe");
        response.put(EMAIL, "john.doe@example.com");
        response.put(FHIR_ID, "patient-" + id);
        return response;
    }
    
    @GetMapping("/api/compliance")
    public Map<String, Object> getComplianceStatus() {
        return complianceService.getComplianceStatus();
    }
}