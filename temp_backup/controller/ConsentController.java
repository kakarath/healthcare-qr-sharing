package com.healthcare.controller;

import com.healthcare.dto.ConsentRequest;
import com.healthcare.service.ConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/consent")
@RequiredArgsConstructor
public class ConsentController {
    
    private final ConsentService consentService;
    
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> createConsent(
            @Valid @RequestBody ConsentRequest request,
            Authentication authentication) {
        
        String patientId = authentication.getName();
        var consent = consentService.createConsent(patientId, request);
        
        return ResponseEntity.ok(consent);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getConsents(Authentication authentication) {
        String patientId = authentication.getName();
        var consents = consentService.getPatientConsents(patientId);
        
        return ResponseEntity.ok(consents);
    }
    
    @PutMapping("/{consentId}/revoke")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> revokeConsent(
            @PathVariable String consentId,
            Authentication authentication) {
        
        String patientId = authentication.getName();
        consentService.revokeConsent(patientId, consentId);
        
        return ResponseEntity.ok().build();
    }
}