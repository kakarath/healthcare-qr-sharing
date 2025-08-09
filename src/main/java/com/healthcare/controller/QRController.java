package com.healthcare.controller;

import com.healthcare.dto.QRShareRequest;
import com.healthcare.dto.QRCodeResponse;
import com.healthcare.dto.QRScanRequest;
import com.healthcare.service.SecureQRCodeService;
import com.healthcare.service.QRScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://healthcare.com", "http://localhost:3000"})
public class QRController {
    
    private final SecureQRCodeService qrCodeService;
    private final QRScanService qrScanService;
    
    @PostMapping("/generate")
    public ResponseEntity<QRCodeResponse> generateQRCode(
            @Valid @RequestBody QRShareRequest request) {
        
        String patientId = "test-patient";
        QRCodeResponse response = qrCodeService.generateSecureQRCode(patientId, request);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/scan")
    public ResponseEntity<Object> scanQRCode(
            @Valid @RequestBody QRScanRequest request) {
        
        String providerId = "test-provider";
        var healthData = qrScanService.processQRScan(request.getQrData(), providerId);
        
        return ResponseEntity.ok(healthData);
    }
    
    @GetMapping("/session/{sessionId}/status")
    public ResponseEntity<Object> getSessionStatus(@PathVariable String sessionId) {
        var status = qrCodeService.getSessionStatus(sessionId);
        return ResponseEntity.ok(status);
    }
    
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> cancelSession(@PathVariable String sessionId) {
        qrCodeService.cancelSession(sessionId);
        return ResponseEntity.ok().build();
    }
}