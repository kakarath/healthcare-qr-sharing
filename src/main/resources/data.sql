-- Insert test patients
INSERT INTO patients (id, fhir_id, first_name, last_name, email, created_at, updated_at) VALUES
('patient-001', 'fhir-patient-001', 'John', 'Doe', 'john.doe@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient-002', 'fhir-patient-002', 'Jane', 'Smith', 'jane.smith@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient-003', 'fhir-patient-003', 'Bob', 'Johnson', 'bob.johnson@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test consent records
INSERT INTO consent_records (id, patient_id, grantee_id, allowed_data_types, status, granted_at, expires_at, purpose) VALUES
('consent-001', 'patient-001', 'provider-001', 'DEMOGRAPHICS,VITALS,MEDICATIONS', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'Emergency care'),
('consent-002', 'patient-002', 'provider-001', 'DEMOGRAPHICS,VITALS', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'Routine checkup'),
('consent-003', 'patient-003', 'provider-002', 'DEMOGRAPHICS,ALLERGIES', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'Allergy consultation');