package com.healthcare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {
    
    @Value("${app.backup.enabled:true}")
    private boolean backupEnabled;
    
    @Value("${app.backup.retention.days:2555}") // 7 years HIPAA requirement
    private int retentionDays;
    
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void performBackup() {
        if (!backupEnabled) return;
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupName = "healthcare_backup_" + timestamp;
        
        // PostgreSQL backup command
        String[] backupCmd = {
            "pg_dump", 
            "-h", "localhost",
            "-p", "5432",
            "-U", System.getenv("DB_USERNAME"),
            "-d", "healthcare_qr",
            "-f", "/backups/" + backupName + ".sql",
            "--no-password"
        };
        
        try {
            ProcessBuilder pb = new ProcessBuilder(backupCmd);
            pb.environment().put("PGPASSWORD", System.getenv("DB_PASSWORD"));
            Process process = pb.start();
            
            if (process.waitFor() == 0) {
                System.out.println("✅ Backup completed: " + backupName);
                cleanOldBackups();
            } else {
                System.err.println("❌ Backup failed for: " + backupName);
            }
        } catch (Exception e) {
            System.err.println("❌ Backup error: " + e.getMessage());
        }
    }
    
    private void cleanOldBackups() {
        File backupDir = new File("/backups");
        if (!backupDir.exists()) return;
        
        long cutoffTime = System.currentTimeMillis() - (retentionDays * 24L * 60 * 60 * 1000);
        
        File[] files = backupDir.listFiles((dir, name) -> name.startsWith("healthcare_backup_"));
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        System.out.println("🗑️ Deleted old backup: " + file.getName());
                    }
                }
            }
        }
    }
}