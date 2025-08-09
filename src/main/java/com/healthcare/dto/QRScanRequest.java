package com.healthcare.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class QRScanRequest {
    
    @NotBlank(message = "QR data cannot be blank")
    private String qrData;
}