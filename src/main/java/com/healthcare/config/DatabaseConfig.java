package com.healthcare.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;

@Component
public class DatabaseConfig {
    
    private final JdbcTemplate jdbcTemplate;
    
    public DatabaseConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        try {
            // Check if tables exist
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'patients'", 
                Integer.class);
            
            if (count == null || count == 0) {
                // Execute schema.sql
                ClassPathResource schemaResource = new ClassPathResource("schema.sql");
                String schemaSql = new String(FileCopyUtils.copyToByteArray(schemaResource.getInputStream()), StandardCharsets.UTF_8);
                
                // Split and execute each statement
                String[] statements = schemaSql.split(";");
                for (String statement : statements) {
                    if (!statement.trim().isEmpty()) {
                        jdbcTemplate.execute(statement.trim());
                    }
                }
                
                // Execute data.sql
                ClassPathResource dataResource = new ClassPathResource("data.sql");
                String dataSql = new String(FileCopyUtils.copyToByteArray(dataResource.getInputStream()), StandardCharsets.UTF_8);
                
                String[] dataStatements = dataSql.split(";");
                for (String statement : dataStatements) {
                    if (!statement.trim().isEmpty() && !statement.trim().startsWith("--")) {
                        jdbcTemplate.execute(statement.trim());
                    }
                }
            }
        } catch (Exception e) {
            // Log error but don't fail startup
            System.err.println("Database initialization warning: " + e.getMessage());
        }
    }
}