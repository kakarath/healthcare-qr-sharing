package com.healthcare.dto;

import com.healthcare.model.ConsentRecord;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;

@Data
public class QRShareRequest {
    
    @NotEmpty(message = "Data types cannot be empty")
    @Size(max = 10, message = "Maximum 10 data types allowed")
    private List<ConsentRecord.DataType> dataTypes;
    
    @Min(value = 1, message = "Minimum expiration is 1 minute")
    @Max(value = 60, message = "Maximum expiration is 60 minutes")
    private int expirationMinutes = 15;
    
    @Size(max = 500, message = "Purpose cannot exceed 500 characters")
    private String purpose;
}