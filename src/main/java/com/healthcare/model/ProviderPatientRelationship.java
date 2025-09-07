package com.healthcare.model;

import java.time.LocalDateTime;

public class ProviderPatientRelationship {
    private String id;
    private String providerId;
    private String patientId;
    private String relationshipType;
    private LocalDateTime establishedDate;
    private String status;

    public ProviderPatientRelationship() {}

    public ProviderPatientRelationship(String id, String providerId, String patientId, String relationshipType) {
        this.id = id;
        this.providerId = providerId;
        this.patientId = patientId;
        this.relationshipType = relationshipType;
        this.establishedDate = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
    public LocalDateTime getEstablishedDate() { return establishedDate; }
    public void setEstablishedDate(LocalDateTime establishedDate) { this.establishedDate = establishedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}