package com.healthcare.service;

import com.healthcare.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

@Service
public class AuthService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService() {
        // Initialize with sample users - BCrypt hashed passwords
        User patient1 = new User("john.doe@example.com", "[PROTECTED]", "John", "Doe", User.UserRole.PATIENT);
        patient1.setId(1L);
        patient1.setPatientId("patient-001");
        users.put("john.doe@example.com", patient1);

        User provider1 = new User("provider@hospital.com", "[PROTECTED]", "Dr. Sarah", "Johnson", User.UserRole.PROVIDER);
        provider1.setId(2L);
        provider1.setProviderId("provider-001");
        users.put("provider@hospital.com", provider1);
        
        // Store hashed passwords separately for security
        storeHashedPassword("john.doe@example.com", "password123");
        storeHashedPassword("provider@hospital.com", "provider123");
    }
    
    private final Map<String, String> hashedPasswords = new ConcurrentHashMap<>();
    
    private void storeHashedPassword(String email, String plainPassword) {
        hashedPasswords.put(email, passwordEncoder.encode(plainPassword));
    }

    public Optional<String> login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Optional.empty();
        }
        
        User user = users.get(email.toLowerCase(Locale.ROOT));
        String hashedPassword = hashedPasswords.get(email.toLowerCase(Locale.ROOT));
        
        if (user != null && hashedPassword != null && passwordEncoder.matches(password, hashedPassword)) {
            String sessionId = UUID.randomUUID().toString();
            sessions.put(sessionId, email.toLowerCase(Locale.ROOT));
            return Optional.of(sessionId);
        }
        return Optional.empty();
    }

    public Optional<User> getUserBySession(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }
        
        String email = sessions.get(sessionId);
        if (email != null) {
            return Optional.ofNullable(users.get(email));
        }
        return Optional.empty();
    }

    public void logout(String sessionId) {
        if (sessionId != null) {
            sessions.remove(sessionId);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(email.toLowerCase(Locale.ROOT)));
    }

    public User registerUser(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User and email cannot be null");
        }
        
        String email = user.getEmail().toLowerCase(Locale.ROOT);
        
        // Check if user already exists
        if (users.containsKey(email)) {
            throw new IllegalStateException("User already exists");
        }
        
        user.setId((long) (users.size() + 1));
        if (user.getRole() == User.UserRole.PATIENT) {
            user.setPatientId("patient-" + String.format("%03d", user.getId()));
        } else {
            user.setProviderId("provider-" + String.format("%03d", user.getId()));
        }
        
        users.put(email, user);
        return user;
    }

    public List<User> searchPatients(String query) {
        if (query == null) {
            return List.of();
        }
        
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        return users.values().stream()
                .filter(user -> user.getRole() == User.UserRole.PATIENT)
                .filter(user -> 
                    user.getFirstName().toLowerCase(Locale.ROOT).contains(lowerQuery) ||
                    user.getLastName().toLowerCase(Locale.ROOT).contains(lowerQuery) ||
                    user.getEmail().toLowerCase(Locale.ROOT).contains(lowerQuery))
                .collect(java.util.stream.Collectors.toList());
    }
}