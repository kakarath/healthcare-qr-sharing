package com.healthcare.dto;

import com.healthcare.model.ConsentRecord;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConsentRequest {
    
    @NotBlank(message = "Grantee ID cannot be blank")
    private String granteeId;
    
    @NotEmpty(message = "Data types cannot be empty")
    private List<ConsentRecord.DataType> allowedDataTypes;
    
    private LocalDateTime expiresAt;
    
    @Size(max = 1000, message = "Purpose cannot exceed 1000 characters")
    private String purpose;
}