package com.healthcare.service;

import ca.uhn.fhir.context.FhirContext;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.healthcare.model.ConsentRecord;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class FhirServiceTest {

    private FhirService fhirService;
    private WireMockServer wireMockServer;
    private FhirContext fhirContext;

    @BeforeEach
    void setUp() {
        fhirContext = FhirContext.forR4();
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
        
        fhirService = new FhirService();
        ReflectionTestUtils.setField(fhirService, "fhirServerBaseUrl", "http://localhost:9999/fhir");
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getPatientData_WithDemographics_ShouldReturnPatientResource() {
        // Given
        String patientId = "patient-123";
        Patient mockPatient = new Patient();
        mockPatient.setId(patientId);
        mockPatient.addName().setFamily("Doe").addGiven("John");

        String patientJson = fhirContext.newJsonParser().encodeResourceToString(mockPatient);

        wireMockServer.stubFor(get(urlPathEqualTo("/fhir/Patient/" + patientId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/fhir+json")
                .withBody(patientJson)));

        // When
        Bundle result = fhirService.getPatientData(patientId, List.of(ConsentRecord.DataType.DEMOGRAPHICS));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getEntry().size());
        assertTrue(result.getEntry().get(0).getResource() instanceof Patient);
        Patient returnedPatient = (Patient) result.getEntry().get(0).getResource();
        assertEquals("Doe", returnedPatient.getName().get(0).getFamily());
    }

    @Test
    void getPatientData_WithVitals_ShouldReturnObservations() {
        // Given
        String patientId = "patient-123";
        Bundle mockObservationsBundle = new Bundle();
        
        Observation vitalSign = new Observation();
        vitalSign.setId("obs-123");
        vitalSign.getCode().setText("Blood Pressure");
        vitalSign.setSubject(new Reference("Patient/" + patientId));
        
        mockObservationsBundle.addEntry().setResource(vitalSign);
        String bundleJson = fhirContext.newJsonParser().encodeResourceToString(mockObservationsBundle);

        wireMockServer.stubFor(get(urlPathMatching("/fhir/Observation"))
            .withQueryParam("patient", equalTo(patientId))
            .withQueryParam("category", equalTo("vital-signs"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/fhir+json")
                .withBody(bundleJson)));

        // When
        Bundle result = fhirService.getPatientData(patientId, List.of(ConsentRecord.DataType.VITALS));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getEntry().size());
        assertTrue(result.getEntry().get(0).getResource() instanceof Observation);
        Observation returnedObs = (Observation) result.getEntry().get(0).getResource();
        assertEquals("Blood Pressure", returnedObs.getCode().getText());
    }

    @Test
    void getPatientData_FhirServerError_ShouldHandleGracefully() {
        // Given
        String patientId = "patient-123";
        
        wireMockServer.stubFor(get(urlPathEqualTo("/fhir/Patient/" + patientId))
            .willReturn(aResponse().withStatus(500)));

        // When & Then
        assertThrows(Exception.class, 
            () -> fhirService.getPatientData(patientId, List.of(ConsentRecord.DataType.DEMOGRAPHICS)));
    }
}