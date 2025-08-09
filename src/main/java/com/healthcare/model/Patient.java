package com.healthcare.model;

import com.healthcare.util.EncryptionConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "patients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false)
    private String fhirId;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Convert(converter = EncryptionConverter.class)
    @Column(name = "date_of_birth")
    private String encryptedDateOfBirth;
    
    @Convert(converter = EncryptionConverter.class)
    @Column(name = "ssn")
    private String encryptedSSN;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private Set<ConsentRecord> consents;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private Set<QRShareSession> qrSessions;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}