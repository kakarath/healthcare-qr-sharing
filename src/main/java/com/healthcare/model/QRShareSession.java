package com.healthcare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "qr_share_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRShareSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @Column(unique = true, nullable = false)
    private String sessionToken;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ConsentRecord.DataType> sharedDataTypes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    private LocalDateTime accessedAt;
    
    private String accessedBy;
    
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    
    @Lob
    private String qrCodeData;
    
    public enum SessionStatus {
        ACTIVE, EXPIRED, USED, CANCELLED
    }
}