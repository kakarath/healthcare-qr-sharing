package com.healthcare.dto;

import com.healthcare.model.ConsentRecord;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class QRPayload {
    private String sessionToken;
    private String patientId;
    private List<ConsentRecord.DataType> dataTypes;
    private LocalDateTime expiresAt;
    
    public String toJson() {
        return "{}"; // Minimal implementation
    }
}