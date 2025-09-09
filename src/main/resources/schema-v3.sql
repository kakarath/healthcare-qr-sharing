-- Healthcare QR Sharing Database Schema v3.0.0 - FHIR Integration
DROP TABLE IF EXISTS fhir_observations CASCADE;
DROP TABLE IF EXISTS fhir_conditions CASCADE;
DROP TABLE IF EXISTS fhir_medications CASCADE;
DROP TABLE IF EXISTS clinical_decision_alerts CASCADE;
DROP TABLE IF EXISTS drug_interactions CASCADE;
DROP TABLE IF EXISTS provider_patient_relationships CASCADE;
DROP TABLE IF EXISTS providers CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;
DROP TABLE IF EXISTS qr_share_session_shared_data_types CASCADE;
DROP TABLE IF EXISTS consent_record_allowed_data_types CASCADE;
DROP TABLE IF EXISTS qr_share_sessions CASCADE;
DROP TABLE IF EXISTS consent_records CASCADE;
DROP TABLE IF EXISTS patients CASCADE;

-- Organizations (hospitals, clinics, practices)
CREATE TABLE organizations (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    npi VARCHAR(10) UNIQUE,
    address TEXT,
    phone VARCHAR(20),
    fhir_id VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Providers (doctors, nurses, specialists)
CREATE TABLE providers (
    id VARCHAR(255) PRIMARY KEY,
    npi VARCHAR(10) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    specialty VARCHAR(255),
    organization_id VARCHAR(255),
    license_number VARCHAR(255),
    fhir_id VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Patients
CREATE TABLE patients (
    id VARCHAR(255) PRIMARY KEY,
    fhir_id VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    date_of_birth TEXT,
    ssn TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Provider-Patient Relationships
CREATE TABLE provider_patient_relationships (
    id VARCHAR(255) PRIMARY KEY,
    provider_id VARCHAR(255) NOT NULL,
    patient_id VARCHAR(255) NOT NULL,
    relationship_type VARCHAR(50) NOT NULL,
    established_date TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    UNIQUE(provider_id, patient_id, relationship_type)
);

-- FHIR Observations (vitals, lab results, assessments)
CREATE TABLE fhir_observations (
    id VARCHAR(255) PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255),
    loinc_code VARCHAR(50),
    display_name VARCHAR(255),
    value_quantity DECIMAL(10,2),
    value_unit VARCHAR(50),
    observation_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'final',
    category VARCHAR(100),
    fhir_resource TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES providers(id)
);

-- FHIR Conditions (diagnoses, problems)
CREATE TABLE fhir_conditions (
    id VARCHAR(255) PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255),
    snomed_code VARCHAR(50),
    display_name VARCHAR(255),
    clinical_status VARCHAR(50),
    verification_status VARCHAR(50),
    onset_date TIMESTAMP,
    category VARCHAR(100),
    fhir_resource TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES providers(id)
);

-- FHIR Medications
CREATE TABLE fhir_medications (
    id VARCHAR(255) PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255),
    rxnorm_code VARCHAR(50),
    medication_name VARCHAR(255),
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    status VARCHAR(50),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    fhir_resource TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES providers(id)
);

-- Drug Interactions
CREATE TABLE drug_interactions (
    id VARCHAR(255) PRIMARY KEY,
    drug1_rxnorm VARCHAR(50) NOT NULL,
    drug2_rxnorm VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    description TEXT,
    mechanism TEXT,
    management TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clinical Decision Support Alerts
CREATE TABLE clinical_decision_alerts (
    id VARCHAR(255) PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255),
    alert_type VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    recommendation TEXT,
    triggered_by VARCHAR(255),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES providers(id)
);

-- Consent Records
CREATE TABLE consent_records (
    id VARCHAR(255) PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    grantee_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    granted_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    revoked_at TIMESTAMP,
    purpose TEXT,
    digital_signature TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Consent Allowed Data Types
CREATE TABLE consent_record_allowed_data_types (
    consent_record_id VARCHAR(255) NOT NULL,
    allowed_data_types VARCHAR(50) NOT NULL,
    FOREIGN KEY (consent_record_id) REFERENCES consent_records(id) ON DELETE CASCADE
);

-- QR Share Sessions
CREATE TABLE qr_share_sessions (
    id VARCHAR(255) PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    session_token VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    accessed_at TIMESTAMP,
    accessed_by VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    qr_code_data TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- QR Session Shared Data Types
CREATE TABLE qr_share_session_shared_data_types (
    qr_share_session_id VARCHAR(255) NOT NULL,
    shared_data_types VARCHAR(50) NOT NULL,
    FOREIGN KEY (qr_share_session_id) REFERENCES qr_share_sessions(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_patients_email ON patients(email);
CREATE INDEX idx_patients_fhir_id ON patients(fhir_id);
CREATE INDEX idx_providers_npi ON providers(npi);
CREATE INDEX idx_providers_email ON providers(email);
CREATE INDEX idx_provider_relationships_provider ON provider_patient_relationships(provider_id);
CREATE INDEX idx_provider_relationships_patient ON provider_patient_relationships(patient_id);
CREATE INDEX idx_fhir_observations_patient ON fhir_observations(patient_id);
CREATE INDEX idx_fhir_observations_loinc ON fhir_observations(loinc_code);
CREATE INDEX idx_fhir_conditions_patient ON fhir_conditions(patient_id);
CREATE INDEX idx_fhir_conditions_snomed ON fhir_conditions(snomed_code);
CREATE INDEX idx_fhir_medications_patient ON fhir_medications(patient_id);
CREATE INDEX idx_fhir_medications_rxnorm ON fhir_medications(rxnorm_code);
CREATE INDEX idx_drug_interactions_drugs ON drug_interactions(drug1_rxnorm, drug2_rxnorm);
CREATE INDEX idx_clinical_alerts_patient ON clinical_decision_alerts(patient_id);
CREATE INDEX idx_clinical_alerts_status ON clinical_decision_alerts(status);
CREATE INDEX idx_consent_records_patient_id ON consent_records(patient_id);
CREATE INDEX idx_qr_sessions_patient_id ON qr_share_sessions(patient_id);
CREATE INDEX idx_qr_sessions_token ON qr_share_sessions(session_token);