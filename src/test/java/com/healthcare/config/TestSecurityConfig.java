package com.healthcare.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfig {
    
    @Bean
    @Primary
    public JwtDecoder testJwtDecoder() {
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) throws JwtException {
                return Jwt.withTokenValue(token)
                    .header("alg", "HS256")
                    .claim("sub", "test-patient-123")
                    .claim("scope", "patient/*.read patient/*.write")
                    .claim("iat", Instant.now())
                    .claim("exp", Instant.now().plusSeconds(3600))
                    .build();
            }
        };
    }
}