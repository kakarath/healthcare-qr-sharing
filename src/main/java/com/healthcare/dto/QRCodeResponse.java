package com.healthcare.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QRCodeResponse {
    private String sessionId;
    private byte[] qrCodeImage;
    private LocalDateTime expiresAt;
    private String purpose;
}