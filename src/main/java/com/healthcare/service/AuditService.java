package com.healthcare.service;

import com.healthcare.model.ConsentRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    
    public void logQRGeneration(String patientId, List<ConsentRecord.DataType> dataTypes) {
        // Minimal audit logging implementation
    }
}