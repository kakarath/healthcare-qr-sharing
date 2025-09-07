package com.healthcare.service;

import com.healthcare.model.PatientData;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
public class PatientDataService {
    private final Map<String, PatientData> patientDataStore = new ConcurrentHashMap<>();

    public PatientDataService() {
        // Initialize with sample data
        PatientData sampleData = new PatientData("patient-001", "John", "Doe", "john.doe@example.com");
        sampleData.setDateOfBirth("1990-01-15");
        sampleData.setBloodType("O+");
        sampleData.setAllergies(List.of("Penicillin", "Shellfish"));
        sampleData.setMedications(List.of("Lisinopril 10mg", "Metformin 500mg"));
        sampleData.setConditions(List.of("Hypertension", "Type 2 Diabetes"));
        sampleData.setEmergencyContact("Jane Doe - 555-0123");
        patientDataStore.put("patient-001", sampleData);
    }

    public Optional<PatientData> getPatientData(String patientId) {
        return Optional.ofNullable(patientDataStore.get(patientId));
    }

    public PatientData updatePatientData(String patientId, PatientData updatedData) {
        updatedData.setPatientId(patientId);
        updatedData.setLastUpdated(LocalDateTime.now());
        patientDataStore.put(patientId, updatedData);
        return updatedData;
    }

    public PatientData createPatientData(PatientData patientData) {
        patientData.setLastUpdated(LocalDateTime.now());
        patientDataStore.put(patientData.getPatientId(), patientData);
        return patientData;
    }

    public List<PatientData> searchPatients(String query) {
        return patientDataStore.values().stream()
                .filter(patient -> 
                    patient.getFirstName().toLowerCase(java.util.Locale.ROOT).contains(query.toLowerCase(java.util.Locale.ROOT)) ||
                    patient.getLastName().toLowerCase(java.util.Locale.ROOT).contains(query.toLowerCase(java.util.Locale.ROOT)) ||
                    patient.getEmail().toLowerCase(java.util.Locale.ROOT).contains(query.toLowerCase(java.util.Locale.ROOT)))
                .toList();
    }

    public String generateQRData(String patientId) {
        Optional<PatientData> patientData = getPatientData(patientId);
        if (patientData.isPresent()) {
            PatientData data = patientData.get();
            return String.format(
                "PATIENT_DATA|%s|%s %s|DOB:%s|BLOOD:%s|ALLERGIES:%s|MEDS:%s|CONDITIONS:%s|EMERGENCY:%s|UPDATED:%s",
                data.getPatientId(),
                data.getFirstName(), data.getLastName(),
                data.getDateOfBirth(),
                data.getBloodType(),
                String.join(",", data.getAllergies() != null ? data.getAllergies() : List.of()),
                String.join(",", data.getMedications() != null ? data.getMedications() : List.of()),
                String.join(",", data.getConditions() != null ? data.getConditions() : List.of()),
                data.getEmergencyContact(),
                data.getLastUpdated()
            );
        }
        return "NO_DATA_FOUND";
    }
}