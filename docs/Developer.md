Later switch to Postgres DB

```
export DB_URL=jdbc:postgresql://localhost:5432/healthcare_qr
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```
Test the QR generation endpoint:

curl -X POST http://localhost:8080/api/qr/generate \
  -H "Content-Type: application/json" \
  -d '{
    "dataTypes": ["DEMOGRAPHICS", "VITALS"],
    "expirationMinutes": 15,
    "purpose": "Test QR generation"
  }'

Test patient creation:

curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "fhirId": "patient-123"
  }'

# Run all tests
mvn clean test

# Run unit tests only
mvn -Dtest="*Test" -DfailIfNoTests=false -Dsurefire.failIfNoSpecifiedTests=false test

# Run integration tests only  
mvn -Dtest="*IT" -DfailIfNoTests=false -Dsurefire.failIfNoSpecifiedTests=false test

# Run specific test class
mvn -Dtest="SecureQRCodeServiceTest" test

# Run tests with coverage
mvn clean test jacoco:report
