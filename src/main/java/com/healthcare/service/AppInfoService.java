package com.healthcare.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Service
public class AppInfoService {
    private final Map<String, Object> appInfo = new HashMap<>();
    private LocalDateTime startTime;
    private boolean isHealthy = true;

    public AppInfoService() {
        this.startTime = LocalDateTime.now();
        loadAppInfo();
    }

    private void loadAppInfo() {
        try {
            // Load from manifest
            InputStream manifestStream = getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
            if (manifestStream != null) {
                Manifest manifest = new Manifest(manifestStream);
                Attributes attributes = manifest.getMainAttributes();
                
                appInfo.put("title", attributes.getValue("Implementation-Title"));
                appInfo.put("version", attributes.getValue("Implementation-Version"));
                appInfo.put("vendor", attributes.getValue("Implementation-Vendor"));
                appInfo.put("buildDate", attributes.getValue("Build-Date"));
                appInfo.put("gitCommit", attributes.getValue("Git-Commit"));
                appInfo.put("buildNumber", attributes.getValue("Build-Number"));
            } else {
                // Fallback values
                appInfo.put("title", "Healthcare QR Data Sharing System");
                appInfo.put("version", "1.1.0");
                appInfo.put("vendor", "Healthcare QR Team");
                appInfo.put("buildDate", "2025-01-04");
                appInfo.put("gitCommit", "dev-build");
                appInfo.put("buildNumber", "local");
            }
        } catch (IOException e) {
            // Set default values on error
            appInfo.put("title", "Healthcare QR Data Sharing System");
            appInfo.put("version", "1.1.0-SNAPSHOT");
            appInfo.put("vendor", "Healthcare QR Team");
            appInfo.put("buildDate", "Unknown");
            appInfo.put("gitCommit", "Unknown");
            appInfo.put("buildNumber", "Unknown");
        }
    }

    public Map<String, Object> getAppInfo() {
        Map<String, Object> info = new HashMap<>(appInfo);
        info.put("startTime", startTime);
        info.put("uptime", getUptime());
        info.put("status", isHealthy ? "UP" : "DOWN");
        info.put("timestamp", LocalDateTime.now());
        return info;
    }

    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", isHealthy ? "UP" : "DOWN");
        health.put("timestamp", LocalDateTime.now());
        health.put("uptime", getUptime());
        health.put("version", appInfo.get("version"));
        
        // Add component health checks
        Map<String, String> components = new HashMap<>();
        components.put("database", "UP"); // Would check actual DB in real implementation
        components.put("qrGenerator", "UP");
        components.put("authService", "UP");
        components.put("consentService", "UP");
        health.put("components", components);
        
        return health;
    }

    private String getUptime() {
        long uptimeSeconds = java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds();
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void setHealthy(boolean healthy) {
        this.isHealthy = healthy;
    }

    public boolean isHealthy() {
        return isHealthy;
    }
}