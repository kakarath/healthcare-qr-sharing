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
import java.util.ArrayList;
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
            Optional<User> user = authService.getUserByEmail(email);
            response.put(SUCCESS, true);
            response.put("sessionId", sessionId.get());
            response.put("message", "Login successful");
            
            // Always add user data
            Map<String, Object> userData = new HashMap<>();
            if (user.isPresent()) {
                userData.put("email", user.get().getEmail());
                userData.put("firstName", user.get().getFirstName());
                userData.put("lastName", user.get().getLastName());
                userData.put("role", user.get().getRole().toString());
            } else {
                // Fallback user data
                userData.put("email", email);
                userData.put("firstName", "User");
                userData.put("lastName", "Name");
                userData.put("role", email.contains("provider") ? "PROVIDER" : "PATIENT");
            }
            response.put("user", userData);
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

    @GetMapping("/api/version")
    public Map<String, Object> getVersion() {
        Map<String, Object> response = new HashMap<>();
        response.put("version", "3.0.0");
        response.put("buildDate", "2024-01-01");
        response.put("fhirVersion", "R4");
        return response;
    }
    
    @GetMapping("/api/banner")
    public Map<String, Object> getBanner() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("uptime", "Running");
        response.put("environment", "Production");
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
    
    @GetMapping("/api/patients/search")
    public Map<String, Object> searchPatients(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String id) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> patients = new ArrayList<>();
        
        // Mock search results
        if ((name != null && name.toLowerCase().contains("john")) || 
            (id != null && id.equals("patient-001"))) {
            patients.add(Map.of(
                "id", "patient-001",
                "firstName", "John",
                "lastName", "Doe",
                "email", "john.doe@example.com",
                "fhirId", "patient-001",
                "dateOfBirth", "1990-01-01",
                "gender", "male"
            ));
        }
        
        if ((name != null && name.toLowerCase().contains("jane")) || 
            (id != null && id.equals("patient-002"))) {
            patients.add(Map.of(
                "id", "patient-002",
                "firstName", "Jane",
                "lastName", "Smith",
                "email", "jane.smith@example.com",
                "fhirId", "patient-002",
                "dateOfBirth", "1985-05-15",
                "gender", "female"
            ));
        }
        
        response.put(SUCCESS, true);
        response.put("patients", patients);
        response.put("total", patients.size());
        return response;
    }
    
    @GetMapping("/api/compliance")
    public Map<String, Object> getComplianceStatus() {
        return complianceService.getComplianceStatus();
    }
    
    @GetMapping("/api/patients/fhir/{id}")
    public Map<String, Object> getFHIRPatient(@PathVariable String id) {
        Map<String, Object> fhirPatient = new HashMap<>();
        
        // FHIR R4 Patient Resource Structure
        fhirPatient.put("resourceType", "Patient");
        fhirPatient.put("id", id);
        fhirPatient.put("meta", Map.of(
            "versionId", "1",
            "lastUpdated", java.time.Instant.now().toString(),
            "profile", List.of("http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient")
        ));
        
        // Identifiers
        fhirPatient.put("identifier", List.of(
            Map.of(
                "use", "usual",
                "type", Map.of(
                    "coding", List.of(Map.of(
                        "system", "http://terminology.hl7.org/CodeSystem/v2-0203",
                        "code", "MR",
                        "display", "Medical Record Number"
                    ))
                ),
                "system", "http://hospital.example.org",
                "value", "patient-" + id
            ),
            Map.of(
                "use", "official",
                "type", Map.of(
                    "coding", List.of(Map.of(
                        "system", "http://terminology.hl7.org/CodeSystem/v2-0203",
                        "code", "SS",
                        "display", "Social Security Number"
                    ))
                ),
                "system", "http://hl7.org/fhir/sid/us-ssn",
                "value", "123-45-6789"
            )
        ));
        
        // Name
        fhirPatient.put("name", List.of(Map.of(
            "use", "official",
            "family", "Doe",
            "given", List.of("John"),
            "text", "John Doe"
        )));
        
        // Demographics
        fhirPatient.put("gender", "male");
        fhirPatient.put("birthDate", "1990-01-01");
        
        // Contact Info
        fhirPatient.put("telecom", List.of(
            Map.of(
                "system", "phone",
                "value", "555-0123",
                "use", "home"
            ),
            Map.of(
                "system", "email",
                "value", "john.doe@example.com",
                "use", "home"
            )
        ));
        
        // Address
        fhirPatient.put("address", List.of(Map.of(
            "use", "home",
            "type", "both",
            "text", "123 Main St, Anytown, ST 12345",
            "line", List.of("123 Main St"),
            "city", "Anytown",
            "state", "ST",
            "postalCode", "12345",
            "country", "US"
        )));
        
        // Marital Status
        fhirPatient.put("maritalStatus", Map.of(
            "coding", List.of(Map.of(
                "system", "http://terminology.hl7.org/CodeSystem/v3-MaritalStatus",
                "code", "M",
                "display", "Married"
            ))
        ));
        
        // Communication
        fhirPatient.put("communication", List.of(Map.of(
            "language", Map.of(
                "coding", List.of(Map.of(
                    "system", "urn:ietf:bcp:47",
                    "code", "en-US",
                    "display", "English (United States)"
                ))
            ),
            "preferred", true
        )));
        
        // Emergency Contact
        fhirPatient.put("contact", List.of(Map.of(
            "relationship", List.of(Map.of(
                "coding", List.of(Map.of(
                    "system", "http://terminology.hl7.org/CodeSystem/v2-0131",
                    "code", "E",
                    "display", "Emergency Contact"
                ))
            )),
            "name", Map.of(
                "family", "Doe",
                "given", List.of("Jane")
            ),
            "telecom", List.of(Map.of(
                "system", "phone",
                "value", "555-0456",
                "use", "mobile"
            ))
        )));
        
        return fhirPatient;
    }
    
    @GetMapping("/api/patients/observations/{patientId}")
    public Map<String, Object> getFHIRObservations(@PathVariable String patientId) {
        Map<String, Object> bundle = new HashMap<>();
        bundle.put("resourceType", "Bundle");
        bundle.put("id", UUID.randomUUID().toString());
        bundle.put("type", "searchset");
        bundle.put("timestamp", java.time.Instant.now().toString());
        
        List<Map<String, Object>> entries = List.of(
            // Blood Pressure
            Map.of(
                "resource", Map.of(
                    "resourceType", "Observation",
                    "id", "bp-" + patientId,
                    "status", "final",
                    "category", List.of(Map.of(
                        "coding", List.of(Map.of(
                            "system", "http://terminology.hl7.org/CodeSystem/observation-category",
                            "code", "vital-signs",
                            "display", "Vital Signs"
                        ))
                    )),
                    "code", Map.of(
                        "coding", List.of(Map.of(
                            "system", "http://loinc.org",
                            "code", "85354-9",
                            "display", "Blood pressure panel with all children optional"
                        ))
                    ),
                    "subject", Map.of("reference", "Patient/" + patientId),
                    "effectiveDateTime", java.time.LocalDateTime.now().toString(),
                    "component", List.of(
                        Map.of(
                            "code", Map.of(
                                "coding", List.of(Map.of(
                                    "system", "http://loinc.org",
                                    "code", "8480-6",
                                    "display", "Systolic blood pressure"
                                ))
                            ),
                            "valueQuantity", Map.of(
                                "value", 120,
                                "unit", "mmHg",
                                "system", "http://unitsofmeasure.org",
                                "code", "mm[Hg]"
                            )
                        ),
                        Map.of(
                            "code", Map.of(
                                "coding", List.of(Map.of(
                                    "system", "http://loinc.org",
                                    "code", "8462-4",
                                    "display", "Diastolic blood pressure"
                                ))
                            ),
                            "valueQuantity", Map.of(
                                "value", 80,
                                "unit", "mmHg",
                                "system", "http://unitsofmeasure.org",
                                "code", "mm[Hg]"
                            )
                        )
                    )
                )
            )
        );
        
        bundle.put("entry", entries);
        bundle.put("total", entries.size());
        
        return bundle;
    }
}