-- Insert organizations
INSERT INTO organizations (id, name, type, npi, address, phone, fhir_id) VALUES
('org-001', 'General Hospital', 'HOSPITAL', '1234567890', '123 Medical Center Dr, Healthcare City, HC 12345', '555-0100', 'org-fhir-001'),
('org-002', 'Family Care Clinic', 'CLINIC', '1234567891', '456 Health St, Wellness Town, WT 67890', '555-0200', 'org-fhir-002');

-- Insert providers
INSERT INTO providers (id, npi, first_name, last_name, email, specialty, organization_id, license_number, fhir_id) VALUES
('provider-001', '1234567892', 'Dr. Sarah', 'Johnson', 'provider@hospital.com', 'Emergency Medicine', 'org-001', 'MD123456', 'prov-fhir-001'),
('provider-002', '1234567893', 'Dr. Michael', 'Chen', 'dr.chen@familycare.com', 'Family Medicine', 'org-002', 'MD789012', 'prov-fhir-002'),
('provider-003', '1234567894', 'Dr. Emily', 'Rodriguez', 'e.rodriguez@hospital.com', 'Cardiology', 'org-001', 'MD345678', 'prov-fhir-003');

-- Insert test patients
INSERT INTO patients (id, fhir_id, first_name, last_name, email, created_at, updated_at) VALUES
('patient-001', 'fhir-patient-001', 'John', 'Doe', 'john.doe@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient-002', 'fhir-patient-002', 'Jane', 'Smith', 'jane.smith@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient-003', 'fhir-patient-003', 'Bob', 'Johnson', 'bob.johnson@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert provider-patient relationships
INSERT INTO provider_patient_relationships (id, provider_id, patient_id, relationship_type, established_date, status) VALUES
('rel-001', 'provider-001', 'patient-001', 'EMERGENCY', '2024-01-01 10:00:00', 'ACTIVE'),
('rel-002', 'provider-002', 'patient-001', 'PRIMARY_CARE', '2023-06-15 09:30:00', 'ACTIVE'),
('rel-003', 'provider-003', 'patient-002', 'SPECIALIST', '2024-01-15 14:00:00', 'ACTIVE');

-- Insert sample drug interactions
INSERT INTO drug_interactions (id, drug1_rxnorm, drug2_rxnorm, severity, description, mechanism, management) VALUES
('int-001', '11289', '1191', 'MAJOR', 'Warfarin and Aspirin interaction', 'Increased bleeding risk', 'Monitor INR closely'),
('int-002', '3616', '1191', 'MAJOR', 'Digoxin and Aspirin interaction', 'Increased digoxin levels', 'Monitor digoxin levels'),
('int-003', '6809', '887', 'MODERATE', 'Metformin and Alcohol interaction', 'Increased lactic acidosis risk', 'Limit alcohol consumption');

-- Insert sample FHIR observations
INSERT INTO fhir_observations (id, patient_id, provider_id, loinc_code, display_name, value_quantity, value_unit, observation_date, category) VALUES
('obs-001', 'patient-001', 'provider-001', '8480-6', 'Systolic Blood Pressure', 120, 'mmHg', CURRENT_TIMESTAMP, 'vital-signs'),
('obs-002', 'patient-001', 'provider-001', '8462-4', 'Diastolic Blood Pressure', 80, 'mmHg', CURRENT_TIMESTAMP, 'vital-signs'),
('obs-003', 'patient-002', 'provider-002', '29463-7', 'Body Weight', 70.5, 'kg', CURRENT_TIMESTAMP, 'vital-signs');

-- Insert sample FHIR conditions
INSERT INTO fhir_conditions (id, patient_id, provider_id, snomed_code, display_name, clinical_status, verification_status, category) VALUES
('cond-001', 'patient-001', 'provider-002', '38341003', 'Hypertension', 'active', 'confirmed', 'problem-list-item'),
('cond-002', 'patient-002', 'provider-003', '53741008', 'Coronary artery disease', 'active', 'confirmed', 'encounter-diagnosis');

-- Insert sample FHIR medications
INSERT INTO fhir_medications (id, patient_id, provider_id, rxnorm_code, medication_name, dosage, frequency, status, start_date) VALUES
('med-001', 'patient-001', 'provider-002', '197361', 'Lisinopril 10mg', '10mg', 'once daily', 'active', CURRENT_TIMESTAMP),
('med-002', 'patient-002', 'provider-003', '36567', 'Simvastatin 20mg', '20mg', 'once daily', 'active', CURRENT_TIMESTAMP);

-- Insert test consent records
INSERT INTO consent_records (id, patient_id, grantee_id, status, granted_at, expires_at, purpose) VALUES
('consent-001', 'patient-001', 'provider-001', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'Emergency care'),
('consent-002', 'patient-002', 'provider-001', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'Routine checkup'),
('consent-003', 'patient-003', 'provider-002', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'Allergy consultation');

-- Insert allowed data types for consent records
INSERT INTO consent_record_allowed_data_types (consent_record_id, allowed_data_types) VALUES
('consent-001', 'DEMOGRAPHICS'),
('consent-001', 'VITALS'),
('consent-001', 'MEDICATIONS'),
('consent-002', 'DEMOGRAPHICS'),
('consent-002', 'VITALS'),
('consent-003', 'DEMOGRAPHICS'),
('consent-003', 'ALLERGIES');