package com.healthcare.service;

import com.healthcare.model.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService() {
        // Initialize with sample users using hashed passwords
        User patient1 = new User("john.doe@example.com", passwordEncoder.encode("password123"), "John", "Doe", User.UserRole.PATIENT);
        patient1.setId(1L);
        patient1.setPatientId("patient-001");
        users.put("john.doe@example.com", patient1);

        User provider1 = new User("provider@hospital.com", passwordEncoder.encode("provider123"), "Dr. Jane", "Smith", User.UserRole.PROVIDER);
        provider1.setId(2L);
        provider1.setProviderId("provider-001");
        users.put("provider@hospital.com", provider1);
    }

    public Optional<String> login(String email, String password) {
        User user = users.get(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String sessionId = java.util.UUID.randomUUID().toString();
            sessions.put(sessionId, email);
            return Optional.of(sessionId);
        }
        return Optional.empty();
    }

    public Optional<User> getUserBySession(String sessionId) {
        String email = sessions.get(sessionId);
        if (email != null) {
            return Optional.ofNullable(users.get(email));
        }
        return Optional.empty();
    }

    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    public User registerUser(User user) {
        // Check if user already exists
        if (users.containsKey(user.getEmail())) {
            return null; // User already exists
        }
        
        user.setId((long) (users.size() + 1));
        if (user.getRole() == User.UserRole.PATIENT) {
            user.setPatientId("patient-" + user.getId());
        } else {
            user.setProviderId("provider-" + user.getId());
        }
        users.put(user.getEmail(), user);
        return user;
    }

    public List<User> searchPatients(String query) {
        return users.values().stream()
                .filter(user -> user.getRole() == User.UserRole.PATIENT)
                .filter(user -> 
                    user.getFirstName().toLowerCase(java.util.Locale.ROOT).contains(query.toLowerCase(java.util.Locale.ROOT)) ||
                    user.getLastName().toLowerCase(java.util.Locale.ROOT).contains(query.toLowerCase(java.util.Locale.ROOT)) ||
                    user.getEmail().toLowerCase(java.util.Locale.ROOT).contains(query.toLowerCase(java.util.Locale.ROOT)))
                .collect(java.util.stream.Collectors.toList());
    }
}