package com.healthcare.model;

import com.healthcare.util.EncryptionConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "consent_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @Column(nullable = false)
    private String granteeId; // Who can access the data
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<DataType> allowedDataTypes;
    
    @Enumerated(EnumType.STRING)
    private ConsentStatus status;
    
    @Column(nullable = false)
    private LocalDateTime grantedAt;
    
    private LocalDateTime expiresAt;
    
    private LocalDateTime revokedAt;
    
    @Column(length = 1000)
    private String purpose;
    
    @Lob
    @Convert(converter = EncryptionConverter.class)
    private String digitalSignature;
    
    public enum ConsentStatus {
        ACTIVE, EXPIRED, REVOKED, PENDING
    }
    
    public enum DataType {
        DEMOGRAPHICS, VITALS, MEDICATIONS, ALLERGIES, CONDITIONS, 
        LAB_RESULTS, IMMUNIZATIONS, PROCEDURES, DOCUMENTS
    }
}