-- Healthcare QR Sharing Database Schema v2.2.0
-- Section 508 compliant provider-patient relationships

-- Add providers table
CREATE TABLE providers (
    id VARCHAR(255) PRIMARY KEY,
    npi VARCHAR(10) UNIQUE NOT NULL, -- National Provider Identifier
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    specialty VARCHAR(255),
    organization_id VARCHAR(255),
    license_number VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add provider-patient relationships
CREATE TABLE provider_patient_relationships (
    id VARCHAR(255) PRIMARY KEY,
    provider_id VARCHAR(255) NOT NULL,
    patient_id VARCHAR(255) NOT NULL,
    relationship_type VARCHAR(50) NOT NULL, -- PRIMARY_CARE, SPECIALIST, EMERGENCY, etc.
    established_date TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, TERMINATED
    FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    UNIQUE(provider_id, patient_id, relationship_type)
);

-- Add organizations table
CREATE TABLE organizations (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL, -- HOSPITAL, CLINIC, PRACTICE
    npi VARCHAR(10) UNIQUE,
    address TEXT,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add foreign key to providers
ALTER TABLE providers ADD CONSTRAINT fk_provider_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations(id);

-- Create indexes
CREATE INDEX idx_providers_npi ON providers(npi);
CREATE INDEX idx_providers_email ON providers(email);
CREATE INDEX idx_provider_relationships_provider ON provider_patient_relationships(provider_id);
CREATE INDEX idx_provider_relationships_patient ON provider_patient_relationships(patient_id);
CREATE INDEX idx_organizations_npi ON organizations(npi);