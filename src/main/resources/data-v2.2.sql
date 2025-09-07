-- Test data for provider-patient relationships v2.2.0

-- Insert organizations
INSERT INTO organizations (id, name, type, npi, address, phone) VALUES
('org-001', 'General Hospital', 'HOSPITAL', '1234567890', '123 Medical Center Dr, Healthcare City, HC 12345', '555-0100'),
('org-002', 'Family Care Clinic', 'CLINIC', '1234567891', '456 Health St, Wellness Town, WT 67890', '555-0200');

-- Insert providers
INSERT INTO providers (id, npi, first_name, last_name, email, specialty, organization_id, license_number) VALUES
('provider-001', '1234567892', 'Dr. Sarah', 'Johnson', 'provider@hospital.com', 'Emergency Medicine', 'org-001', 'MD123456'),
('provider-002', '1234567893', 'Dr. Michael', 'Chen', 'dr.chen@familycare.com', 'Family Medicine', 'org-002', 'MD789012'),
('provider-003', '1234567894', 'Dr. Emily', 'Rodriguez', 'e.rodriguez@hospital.com', 'Cardiology', 'org-001', 'MD345678');

-- Insert provider-patient relationships
INSERT INTO provider_patient_relationships (id, provider_id, patient_id, relationship_type, established_date, status) VALUES
('rel-001', 'provider-001', 'patient-001', 'EMERGENCY', '2024-01-01 10:00:00', 'ACTIVE'),
('rel-002', 'provider-002', 'patient-001', 'PRIMARY_CARE', '2023-06-15 09:30:00', 'ACTIVE'),
('rel-003', 'provider-003', 'patient-002', 'SPECIALIST', '2024-01-15 14:00:00', 'ACTIVE');