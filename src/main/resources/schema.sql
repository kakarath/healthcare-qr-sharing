-- Healthcare QR Sharing Database Schema v2.2.0
-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS provider_patient_relationships CASCADE;
DROP TABLE IF EXISTS providers CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;
DROP TABLE IF EXISTS qr_share_session_shared_data_types CASCADE;
DROP TABLE IF EXISTS consent_record_allowed_data_types CASCADE;
DROP TABLE IF EXISTS qr_share_sessions CASCADE;
DROP TABLE IF EXISTS consent_records CASCADE;
DROP TABLE IF EXISTS patients CASCADE;

-- Create patients table
CREATE TABLE patients (
    id VARCHAR(255) PRIMARY KEY,
    fhir_id VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    date_of_birth TEXT, -- Encrypted
    ssn TEXT, -- Encrypted
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create consent_records table
CREATE TABLE consent_records (
    id VARCHAR(255) PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    grantee_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    granted_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    revoked_at TIMESTAMP,
    purpose TEXT,
    digital_signature TEXT, -- Encrypted
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Create consent_record_allowed_data_types table
CREATE TABLE consent_record_allowed_data_types (
    consent_record_id VARCHAR(255) NOT NULL,
    allowed_data_types VARCHAR(50) NOT NULL,
    FOREIGN KEY (consent_record_id) REFERENCES consent_records(id) ON DELETE CASCADE
);

-- Create qr_share_sessions table
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

-- Create qr_share_session_shared_data_types table
CREATE TABLE qr_share_session_shared_data_types (
    qr_share_session_id VARCHAR(255) NOT NULL,
    shared_data_types VARCHAR(50) NOT NULL,
    FOREIGN KEY (qr_share_session_id) REFERENCES qr_share_sessions(id) ON DELETE CASCADE
);

-- Create organizations table
CREATE TABLE organizations (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    npi VARCHAR(10) UNIQUE,
    address TEXT,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create providers table
CREATE TABLE providers (
    id VARCHAR(255) PRIMARY KEY,
    npi VARCHAR(10) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    specialty VARCHAR(255),
    organization_id VARCHAR(255),
    license_number VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Create provider-patient relationships
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

-- Create indexes for performance
CREATE INDEX idx_patients_email ON patients(email);
CREATE INDEX idx_patients_fhir_id ON patients(fhir_id);
CREATE INDEX idx_consent_records_patient_id ON consent_records(patient_id);
CREATE INDEX idx_consent_records_grantee_id ON consent_records(grantee_id);
CREATE INDEX idx_consent_records_status ON consent_records(status);
CREATE INDEX idx_qr_sessions_patient_id ON qr_share_sessions(patient_id);
CREATE INDEX idx_qr_sessions_token ON qr_share_sessions(session_token);
CREATE INDEX idx_qr_sessions_status ON qr_share_sessions(status);
CREATE INDEX idx_qr_sessions_expires_at ON qr_share_sessions(expires_at);
CREATE INDEX idx_providers_npi ON providers(npi);
CREATE INDEX idx_providers_email ON providers(email);
CREATE INDEX idx_provider_relationships_provider ON provider_patient_relationships(provider_id);
CREATE INDEX idx_provider_relationships_patient ON provider_patient_relationships(patient_id);
CREATE INDEX idx_organizations_npi ON organizations(npi);