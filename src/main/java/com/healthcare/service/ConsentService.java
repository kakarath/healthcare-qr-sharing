package com.healthcare.service;

import com.healthcare.dto.ConsentRequest;
import com.healthcare.model.ConsentRecord;
import com.healthcare.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsentService {
    
    private final ConsentRepository consentRepository;
    
    public ConsentRecord createConsent(String patientId, ConsentRequest request) {
        ConsentRecord consent = ConsentRecord.builder()
                .granteeId(request.getGranteeId())
                .allowedDataTypes(request.getAllowedDataTypes())
                .status(ConsentRecord.ConsentStatus.ACTIVE)
                .grantedAt(LocalDateTime.now())
                .expiresAt(request.getExpiresAt())
                .purpose(request.getPurpose())
                .build();
        
        return consentRepository.save(consent);
    }
    
    public List<ConsentRecord> getPatientConsents(String patientId) {
        return consentRepository.findByPatientId(patientId);
    }
    
    public void revokeConsent(String patientId, String consentId) {
        ConsentRecord consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new RuntimeException("Consent not found"));
        
        consent.setStatus(ConsentRecord.ConsentStatus.REVOKED);
        consent.setRevokedAt(LocalDateTime.now());
        consentRepository.save(consent);
    }
    
    public boolean hasValidConsent(String patientId, List<ConsentRecord.DataType> dataTypes) {
        var activeConsents = consentRepository.findActiveConsentsByPatientId(patientId, LocalDateTime.now());
        
        if (activeConsents.isEmpty()) {
            return false;
        }
        
        return dataTypes.stream().allMatch(dataType -> 
            activeConsents.stream().anyMatch(consent -> 
                consent.getAllowedDataTypes().contains(dataType)
            )
        );
    }
}