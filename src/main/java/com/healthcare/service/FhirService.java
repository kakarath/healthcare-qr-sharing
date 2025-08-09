package com.healthcare.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.healthcare.model.ConsentRecord;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FhirService {
    
    private final FhirContext fhirContext = FhirContext.forR4();
    
    @Value("${healthcare.fhir.server.base-url}")
    private String fhirServerBaseUrl;
    
    public Bundle getPatientData(String patientId, List<ConsentRecord.DataType> dataTypes) {
        IGenericClient client = fhirContext.newRestfulGenericClient(fhirServerBaseUrl);
        
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);
        
        for (ConsentRecord.DataType dataType : dataTypes) {
            switch (dataType) {
                case DEMOGRAPHICS:
                    addPatientResource(bundle, client, patientId);
                    break;
                case VITALS:
                    addObservations(bundle, client, patientId, "vital-signs");
                    break;
                case MEDICATIONS:
                    addMedicationRequests(bundle, client, patientId);
                    break;
                case ALLERGIES:
                    addAllergyIntolerances(bundle, client, patientId);
                    break;
                case CONDITIONS:
                    addConditions(bundle, client, patientId);
                    break;
                case LAB_RESULTS:
                    addObservations(bundle, client, patientId, "laboratory");
                    break;
                case IMMUNIZATIONS:
                    addImmunizations(bundle, client, patientId);
                    break;
            }
        }
        
        return bundle;
    }
    
    private void addPatientResource(Bundle bundle, IGenericClient client, String patientId) {
        Patient patient = client.read().resource(Patient.class).withId(patientId).execute();
        bundle.addEntry().setResource(patient);
    }
    
    private void addObservations(Bundle bundle, IGenericClient client, String patientId, String category) {
        Bundle observations = client.search()
            .forResource(Observation.class)
            .where(Observation.PATIENT.hasId(patientId))
            .and(Observation.CATEGORY.exactly().code(category))
            .returnBundle(Bundle.class)
            .execute();
            
        observations.getEntry().forEach(bundle::addEntry);
    }
    
    private void addMedicationRequests(Bundle bundle, IGenericClient client, String patientId) {
        Bundle medications = client.search()
            .forResource(MedicationRequest.class)
            .where(MedicationRequest.PATIENT.hasId(patientId))
            .returnBundle(Bundle.class)
            .execute();
            
        medications.getEntry().forEach(bundle::addEntry);
    }
    
    private void addAllergyIntolerances(Bundle bundle, IGenericClient client, String patientId) {
        Bundle allergies = client.search()
            .forResource(AllergyIntolerance.class)
            .where(AllergyIntolerance.PATIENT.hasId(patientId))
            .returnBundle(Bundle.class)
            .execute();
            
        allergies.getEntry().forEach(bundle::addEntry);
    }
    
    private void addConditions(Bundle bundle, IGenericClient client, String patientId) {
        Bundle conditions = client.search()
            .forResource(Condition.class)
            .where(Condition.PATIENT.hasId(patientId))
            .returnBundle(Bundle.class)
            .execute();
            
        conditions.getEntry().forEach(bundle::addEntry);
    }
    
    private void addImmunizations(Bundle bundle, IGenericClient client, String patientId) {
        Bundle immunizations = client.search()
            .forResource(Immunization.class)
            .where(Immunization.PATIENT.hasId(patientId))
            .returnBundle(Bundle.class)
            .execute();
            
        immunizations.getEntry().forEach(bundle::addEntry);
    }
}