package com.healthcare.service;

import com.healthcare.model.User;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
public class AuthService {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> sessions = new HashMap<>();

    public AuthService() {
        // Initialize with sample users
        User patient1 = new User("patient@example.com", "password", "John", "Doe", User.UserRole.PATIENT);
        patient1.setId(1L);
        patient1.setPatientId("patient-1");
        users.put("patient@example.com", patient1);

        User provider1 = new User("provider@example.com", "password", "Dr. Jane", "Smith", User.UserRole.PROVIDER);
        provider1.setId(2L);
        provider1.setProviderId("provider-1");
        users.put("provider@example.com", provider1);
    }

    public Optional<String> login(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            String sessionId = "session-" + System.currentTimeMillis();
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
                    user.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }
}