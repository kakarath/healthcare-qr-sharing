package com.healthcare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QRScanService {
    
    public Object processQRScan(String qrData, String providerId) {
        // Minimal implementation
        return "Health data processed";
    }
}