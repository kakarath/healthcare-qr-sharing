package com.healthcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories
@EntityScan
@EnableTransactionManagement
public class HealthcareQRSharingApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthcareQRSharingApplication.class, args);
    }
}