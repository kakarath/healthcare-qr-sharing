package com.healthcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcare.util.QRCodeGenerator;
import com.healthcare.model.User;
import com.healthcare.model.ConsentRequest;
import com.healthcare.service.AuthService;
import com.healthcare.service.ConsentService;
import com.healthcare.service.AppInfoService;
import com.healthcare.service.NotificationService;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private AuthService authService;

    @Autowired
    private ConsentService consentService;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private NotificationService notificationService;

    public static void main(String[] args) {
        SpringApplication.run(MinimalApp.class, args);
    }

    @PostMapping("/api/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<String> sessionId = authService.login(email, password);
        if (sessionId.isPresent()) {
            Optional<User> user = authService.getUserBySession(sessionId.get());
            response.put(SUCCESS, true);
            response.put("sessionId", sessionId.get());
            response.put("user", user.get());
            response.put("message", "Login successful");
        } else {
            response.put(SUCCESS, false);
            response.put("message", "Invalid credentials");
        }
        return response;
    }

    @PostMapping("/api/auth/logout")
    public Map<String, Object> logout(@RequestHeader("Authorization") String sessionId) {
        Map<String, Object> response = new HashMap<>();
        authService.logout(sessionId.replace("Bearer ", ""));
        response.put(SUCCESS, true);
        response.put("message", "Logged out successfully");
        return response;
    }

    @PostMapping("/api/consent/request")
    public Map<String, Object> requestConsent(@RequestHeader("Authorization") String sessionId, 
                                            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> provider = authService.getUserBySession(sessionId.replace("Bearer ", ""));
        
        if (provider.isPresent() && provider.get().getRole() == User.UserRole.PROVIDER) {
            String patientId = request.get("patientId");
            String purpose = request.get("purpose");
            
            ConsentRequest consentRequest = consentService.createConsentRequest(
                patientId, provider.get().getProviderId(), 
                provider.get().getFirstName() + " " + provider.get().getLastName(), purpose);
            
            response.put(SUCCESS, true);
            response.put("consentRequest", consentRequest);
            response.put("message", "Consent request sent to patient");
        } else {
            response.put(SUCCESS, false);
            response.put("message", "Unauthorized or invalid user");
        }
        return response;
    }

    @GetMapping("/api/consent/pending")
    public Map<String, Object> getPendingConsents(@RequestHeader("Authorization") String sessionId) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> patient = authService.getUserBySession(sessionId.replace("Bearer ", ""));
        
        if (patient.isPresent() && patient.get().getRole() == User.UserRole.PATIENT) {
            List<ConsentRequest> pendingRequests = consentService.getPendingRequestsForPatient(patient.get().getPatientId());
            response.put(SUCCESS, true);
            response.put("requests", pendingRequests);
        } else {
            response.put(SUCCESS, false);
            response.put("message", "Unauthorized");
        }
        return response;
    }

    @PostMapping("/api/consent/{consentId}/approve")
    public Map<String, Object> approveConsent(@RequestHeader("Authorization") String sessionId,
                                            @PathVariable String consentId) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> patient = authService.getUserBySession(sessionId.replace("Bearer ", ""));
        
        if (patient.isPresent() && patient.get().getRole() == User.UserRole.PATIENT) {
            try {
                String qrCodeId = "qr-" + System.currentTimeMillis();
                String patientData = "Patient: " + patient.get().getFirstName() + " " + patient.get().getLastName() + 
                                  ", ID: " + patient.get().getPatientId() + ", Consent: " + consentId;
                String base64QrCode = QRCodeGenerator.generateQRCodeBase64(patientData, 200, 200);
                
                ConsentRequest approvedRequest = consentService.approveConsent(consentId, qrCodeId);
                
                response.put(SUCCESS, true);
                response.put("qrCodeId", qrCodeId);
                response.put("qrCodeImage", base64QrCode);
                response.put("consentRequest", approvedRequest);
                response.put("message", "Consent approved and QR code generated");
            } catch (Exception e) {
                response.put(SUCCESS, false);
                response.put("message", "Failed to generate QR code: " + e.getMessage());
            }
        } else {
            response.put(SUCCESS, false);
            response.put("message", "Unauthorized");
        }
        return response;
    }

    @PostMapping("/api/consent/{consentId}/deny")
    public Map<String, Object> denyConsent(@RequestHeader("Authorization") String sessionId,
                                         @PathVariable String consentId) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> patient = authService.getUserBySession(sessionId.replace("Bearer ", ""));
        
        if (patient.isPresent() && patient.get().getRole() == User.UserRole.PATIENT) {
            ConsentRequest deniedRequest = consentService.denyConsent(consentId);
            response.put(SUCCESS, true);
            response.put("consentRequest", deniedRequest);
            response.put("message", "Consent denied");
        } else {
            response.put(SUCCESS, false);
            response.put("message", "Unauthorized");
        }
        return response;
    }

    @GetMapping("/api/info")
    public Map<String, Object> getAppInfo() {
        return appInfoService.getAppInfo();
    }

    @GetMapping("/api/health")
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = appInfoService.getHealthStatus();
        
        // If app is down, send notification
        if (!appInfoService.isHealthy()) {
            notificationService.sendAppDownAlert("Health check failed");
        }
        
        return health;
    }

    @GetMapping("/api/banner")
    public Map<String, Object> getBanner() {
        Map<String, Object> banner = new HashMap<>();
        Map<String, Object> appInfo = appInfoService.getAppInfo();
        
        banner.put("title", appInfo.get("title"));
        banner.put("version", appInfo.get("version"));
        banner.put("status", appInfo.get("status"));
        banner.put("uptime", appInfo.get("uptime"));
        banner.put("message", appInfoService.isHealthy() ? 
            "Healthcare QR System is running normally" : 
            "Healthcare QR System is experiencing issues");
        
        return banner;
    }

    @GetMapping("/api/version")
    public Map<String, Object> getVersion() {
        Map<String, Object> version = new HashMap<>();
        Map<String, Object> appInfo = appInfoService.getAppInfo();
        
        version.put("version", appInfo.get("version"));
        version.put("buildDate", appInfo.get("buildDate"));
        version.put("gitCommit", appInfo.get("gitCommit"));
        version.put("buildNumber", appInfo.get("buildNumber"));
        
        return version;
    }

    @PostMapping("/api/admin/maintenance")
    public Map<String, Object> scheduleMaintenance(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String details = request.get("details");
        
        notificationService.sendMaintenanceNotification(details);
        
        response.put(SUCCESS, true);
        response.put("message", "Maintenance notification sent");
        return response;
    }

    // Other methods remain the same...
    @GetMapping("/api/user/profile")
    public Map<String, Object> getUserProfile(@RequestHeader("Authorization") String sessionId) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = authService.getUserBySession(sessionId.replace("Bearer ", ""));
        
        if (user.isPresent()) {
            response.put(SUCCESS, true);
            response.put("user", user.get());
        } else {
            response.put(SUCCESS, false);
            response.put("message", "Invalid session");
        }
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
