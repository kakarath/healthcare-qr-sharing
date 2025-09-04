package com.healthcare.model;

import java.time.LocalDateTime;

public class ConsentRequest {
    private String id;
    private String patientId;
    private String providerId;
    private String providerName;
    private String purpose;
    private ConsentStatus status;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    private String qrCodeId;

    public enum ConsentStatus {
        PENDING, APPROVED, DENIED, EXPIRED
    }

    // Constructors
    public ConsentRequest() {}

    public ConsentRequest(String patientId, String providerId, String providerName, String purpose) {
        this.id = "consent-" + System.currentTimeMillis();
        this.patientId = patientId;
        this.providerId = providerId;
        this.providerName = providerName;
        this.purpose = purpose;
        this.status = ConsentStatus.PENDING;
        this.requestTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public ConsentStatus getStatus() { return status; }
    public void setStatus(ConsentStatus status) { this.status = status; }

    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }

    public LocalDateTime getResponseTime() { return responseTime; }
    public void setResponseTime(LocalDateTime responseTime) { this.responseTime = responseTime; }

    public String getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(String qrCodeId) { this.qrCodeId = qrCodeId; }
}