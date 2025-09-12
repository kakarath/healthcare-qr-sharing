# FHIR R4 Compliance Guide

## Overview
This Healthcare QR Sharing application is designed to be compliant with HL7 FHIR R4 standards for healthcare interoperability.

## FHIR Resources Supported

### 1. Patient Resource
**Endpoint**: `/api/patients/fhir/{id}`

**FHIR R4 Patient Resource Structure**:
```json
{
  "resourceType": "Patient",
  "id": "patient-001",
  "meta": {
    "versionId": "1",
    "lastUpdated": "2024-01-01T12:00:00Z",
    "profile": ["http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient"]
  },
  "identifier": [
    {
      "use": "usual",
      "type": {
        "coding": [{
          "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
          "code": "MR",
          "display": "Medical Record Number"
        }]
      },
      "system": "http://hospital.example.org",
      "value": "patient-001"
    },
    {
      "use": "official",
      "type": {
        "coding": [{
          "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
          "code": "SS",
          "display": "Social Security Number"
        }]
      },
      "system": "http://hl7.org/fhir/sid/us-ssn",
      "value": "123-45-6789"
    }
  ],
  "name": [{
    "use": "official",
    "family": "Doe",
    "given": ["John"],
    "text": "John Doe"
  }],
  "gender": "male",
  "birthDate": "1990-01-01",
  "telecom": [
    {
      "system": "phone",
      "value": "555-0123",
      "use": "home"
    },
    {
      "system": "email",
      "value": "john.doe@example.com",
      "use": "home"
    }
  ],
  "address": [{
    "use": "home",
    "type": "both",
    "text": "123 Main St, Anytown, ST 12345",
    "line": ["123 Main St"],
    "city": "Anytown",
    "state": "ST",
    "postalCode": "12345",
    "country": "US"
  }],
  "maritalStatus": {
    "coding": [{
      "system": "http://terminology.hl7.org/CodeSystem/v3-MaritalStatus",
      "code": "M",
      "display": "Married"
    }]
  },
  "communication": [{
    "language": {
      "coding": [{
        "system": "urn:ietf:bcp:47",
        "code": "en-US",
        "display": "English (United States)"
      }]
    },
    "preferred": true
  }],
  "contact": [{
    "relationship": [{
      "coding": [{
        "system": "http://terminology.hl7.org/CodeSystem/v2-0131",
        "code": "E",
        "display": "Emergency Contact"
      }]
    }],
    "name": {
      "family": "Doe",
      "given": ["Jane"]
    },
    "telecom": [{
      "system": "phone",
      "value": "555-0456",
      "use": "mobile"
    }]
  }]
}
```

### 2. Observation Resource (Vital Signs)
**Endpoint**: `/api/patients/observations/{patientId}`

**FHIR R4 Observation Bundle**:
```json
{
  "resourceType": "Bundle",
  "id": "obs-bundle-001",
  "type": "searchset",
  "timestamp": "2024-01-01T12:00:00Z",
  "total": 1,
  "entry": [{
    "resource": {
      "resourceType": "Observation",
      "id": "bp-patient-001",
      "status": "final",
      "category": [{
        "coding": [{
          "system": "http://terminology.hl7.org/CodeSystem/observation-category",
          "code": "vital-signs",
          "display": "Vital Signs"
        }]
      }],
      "code": {
        "coding": [{
          "system": "http://loinc.org",
          "code": "85354-9",
          "display": "Blood pressure panel with all children optional"
        }]
      },
      "subject": {
        "reference": "Patient/patient-001"
      },
      "effectiveDateTime": "2024-01-01T12:00:00Z",
      "component": [
        {
          "code": {
            "coding": [{
              "system": "http://loinc.org",
              "code": "8480-6",
              "display": "Systolic blood pressure"
            }]
          },
          "valueQuantity": {
            "value": 120,
            "unit": "mmHg",
            "system": "http://unitsofmeasure.org",
            "code": "mm[Hg]"
          }
        },
        {
          "code": {
            "coding": [{
              "system": "http://loinc.org",
              "code": "8462-4",
              "display": "Diastolic blood pressure"
            }]
          },
          "valueQuantity": {
            "value": 80,
            "unit": "mmHg",
            "system": "http://unitsofmeasure.org",
            "code": "mm[Hg]"
          }
        }
      ]
    }
  }]
}
```

### 3. Condition Resource
**FHIR R4 Condition Resource**:
```json
{
  "resourceType": "Condition",
  "id": "condition-001",
  "clinicalStatus": {
    "coding": [{
      "system": "http://terminology.hl7.org/CodeSystem/condition-clinical",
      "code": "active"
    }]
  },
  "verificationStatus": {
    "coding": [{
      "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
      "code": "confirmed"
    }]
  },
  "category": [{
    "coding": [{
      "system": "http://terminology.hl7.org/CodeSystem/condition-category",
      "code": "problem-list-item",
      "display": "Problem List Item"
    }]
  }],
  "code": {
    "coding": [{
      "system": "http://snomed.info/sct",
      "code": "38341003",
      "display": "Hypertension"
    }]
  },
  "subject": {
    "reference": "Patient/patient-001"
  },
  "onsetDateTime": "2023-01-01"
}
```

### 4. MedicationRequest Resource
**FHIR R4 MedicationRequest Resource**:
```json
{
  "resourceType": "MedicationRequest",
  "id": "med-001",
  "status": "active",
  "intent": "order",
  "medicationCodeableConcept": {
    "coding": [{
      "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
      "code": "197361",
      "display": "Lisinopril 10mg"
    }]
  },
  "subject": {
    "reference": "Patient/patient-001"
  },
  "authoredOn": "2024-01-01",
  "dosageInstruction": [{
    "text": "10mg once daily",
    "timing": {
      "repeat": {
        "frequency": 1,
        "period": 1,
        "periodUnit": "d"
      }
    },
    "doseAndRate": [{
      "doseQuantity": {
        "value": 10,
        "unit": "mg",
        "system": "http://unitsofmeasure.org",
        "code": "mg"
      }
    }]
  }]
}
```

## FHIR Compliance Features

### 1. Standard Terminologies
- **LOINC**: Laboratory and clinical observations
- **SNOMED CT**: Clinical terminology
- **RxNorm**: Medications
- **ICD-10**: Diagnosis codes
- **CPT**: Procedure codes

### 2. US Core Profiles
- Patient Profile: `http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient`
- Observation Profile: `http://hl7.org/fhir/us/core/StructureDefinition/us-core-vital-signs`
- Condition Profile: `http://hl7.org/fhir/us/core/StructureDefinition/us-core-condition`
- MedicationRequest Profile: `http://hl7.org/fhir/us/core/StructureDefinition/us-core-medicationrequest`

### 3. Data Elements Supported

#### Patient Demographics
- Name (official, usual, nickname)
- Gender (male, female, other, unknown)
- Birth Date
- Address (home, work, temp)
- Telecom (phone, email, fax)
- Marital Status
- Communication Language
- Emergency Contact

#### Clinical Data
- Vital Signs (Blood Pressure, Heart Rate, Temperature, Weight, Height)
- Allergies and Intolerances
- Current Medications
- Medical Conditions
- Laboratory Results
- Immunizations

#### Identifiers
- Medical Record Number (MRN)
- Social Security Number
- Insurance ID
- Driver's License
- Passport Number

### 4. FHIR Operations Supported
- **READ**: Get individual resources
- **SEARCH**: Search for resources with parameters
- **CREATE**: Add new resources
- **UPDATE**: Modify existing resources
- **BUNDLE**: Group multiple resources

### 5. Security and Privacy
- OAuth 2.0 / SMART on FHIR authentication
- Patient consent management
- Audit logging for all FHIR operations
- Data encryption at rest and in transit

## Mobile Provider Interface

### FHIR Data Editing on Mobile
When providers access patient data via QR code on mobile devices, they can:

1. **View FHIR Resources**: Complete patient data in FHIR R4 format
2. **Edit Clinical Data**: Update observations, conditions, medications
3. **Add New Entries**: Create new FHIR resources
4. **Validate Data**: Ensure FHIR compliance before saving
5. **Sync Changes**: Update central FHIR server

### Provider Mobile Workflow
1. Scan patient QR code
2. Authenticate with SMART on FHIR
3. View patient FHIR bundle
4. Edit/add clinical data
5. Validate FHIR compliance
6. Save changes to FHIR server
7. Generate updated QR code

## Testing FHIR Compliance

### Validation Tools
- **FHIR Validator**: `https://validator.fhir.org/`
- **Touchstone Testing**: `https://touchstone.aegis.net/touchstone/`
- **Inferno Testing**: `https://inferno.healthit.gov/`

### Sample API Calls
```bash
# Get FHIR Patient
curl -H "Accept: application/fhir+json" \
     http://localhost:8080/api/patients/fhir/patient-001

# Get Patient Observations
curl -H "Accept: application/fhir+json" \
     http://localhost:8080/api/patients/observations/patient-001

# Search Patients
curl -H "Accept: application/fhir+json" \
     "http://localhost:8080/api/patients/fhir?family=Doe&given=John"
```

## Next Steps for Full FHIR Compliance

1. **Implement FHIR Server**: Use HAPI FHIR or similar
2. **Add SMART on FHIR**: OAuth 2.0 authentication
3. **Terminology Services**: Connect to VSAC/UMLS
4. **Bulk Data Export**: FHIR $export operation
5. **Clinical Decision Support**: CDS Hooks integration
6. **Patient Access API**: 21st Century Cures compliance