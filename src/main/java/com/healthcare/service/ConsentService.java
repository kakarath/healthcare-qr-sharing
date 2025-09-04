package com.healthcare.service;

import com.healthcare.model.ConsentRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConsentService {
    private final Map<String, ConsentRequest> consentRequests = new HashMap<>();

    public ConsentRequest createConsentRequest(String patientId, String providerId, String providerName, String purpose) {
        ConsentRequest request = new ConsentRequest(patientId, providerId, providerName, purpose);
        consentRequests.put(request.getId(), request);
        return request;
    }

    public List<ConsentRequest> getPendingRequestsForPatient(String patientId) {
        return consentRequests.values().stream()
                .filter(req -> req.getPatientId().equals(patientId) && 
                              req.getStatus() == ConsentRequest.ConsentStatus.PENDING)
                .collect(Collectors.toList());
    }

    public ConsentRequest approveConsent(String consentId, String qrCodeId) {
        ConsentRequest request = consentRequests.get(consentId);
        if (request != null && request.getStatus() == ConsentRequest.ConsentStatus.PENDING) {
            request.setStatus(ConsentRequest.ConsentStatus.APPROVED);
            request.setResponseTime(LocalDateTime.now());
            request.setQrCodeId(qrCodeId);
        }
        return request;
    }

    public ConsentRequest denyConsent(String consentId) {
        ConsentRequest request = consentRequests.get(consentId);
        if (request != null && request.getStatus() == ConsentRequest.ConsentStatus.PENDING) {
            request.setStatus(ConsentRequest.ConsentStatus.DENIED);
            request.setResponseTime(LocalDateTime.now());
        }
        return request;
    }

    public ConsentRequest getConsentRequest(String consentId) {
        return consentRequests.get(consentId);
    }
}