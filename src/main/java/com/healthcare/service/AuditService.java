package com.healthcare.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class AuditService {
    private final ConcurrentLinkedQueue<AuditEvent> auditLog = new ConcurrentLinkedQueue<>();
    
    public void logAccess(String userId, String resource, String action, String ipAddress) {
        auditLog.offer(new AuditEvent(userId, resource, action, ipAddress, LocalDateTime.now(), "SUCCESS"));
    }
    
    public void logFailure(String userId, String resource, String action, String ipAddress, String reason) {
        auditLog.offer(new AuditEvent(userId, resource, action, ipAddress, LocalDateTime.now(), "FAILURE: " + reason));
    }
    
    public void logDataAccess(String userId, String patientId, String dataType, String purpose) {
        auditLog.offer(new AuditEvent(userId, "PHI_ACCESS", "VIEW_" + dataType, null, LocalDateTime.now(), 
            "Patient: " + patientId + ", Purpose: " + purpose));
    }
    
    public static class AuditEvent {
        public final String userId, resource, action, ipAddress, status;
        public final LocalDateTime timestamp;
        
        public AuditEvent(String userId, String resource, String action, String ipAddress, LocalDateTime timestamp, String status) {
            this.userId = userId; this.resource = resource; this.action = action;
            this.ipAddress = ipAddress; this.timestamp = timestamp; this.status = status;
        }
    }
}