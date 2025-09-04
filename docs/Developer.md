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

# build the app
```
mvn clean compile -X 2>&1 | head -50
mvn clean package
mvn clean package -DskipTests
```

# building a docker image
```
docker build -t healthcare-qr-app .
```

# setup database
```bash
docker run --name postgres -e POSTGRES_USER={POSTGRES_USER} -e POSTGRES_PASSWORD={POSTGRES_PASSWORD} -e POSTGRES_DB={POSTGRES_DB} -p 5432:5432 -d postgres
```
# To run with PostgreSQL:
```
mvn spring-boot:run
```

# start docker v2
```
docker compose up -d
```

# check containers status
```
docker compose ps
```

# view logs
```
docker compose logs -f
```

# stop docker
```
docker compose down -v
```

# Connection details:
```
Database: env
Username: env
Password: env
URL: jdbc:postgresql://localhost:5432/healthcare_qr
```

# JWT Secret generation
```
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

# testing the app to see if it is working
```
curl -s http://localhost:8080/api/patients | head -10
```

# Testing patient creation with PostgreSQL database.
```
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "fhirId": "patient-456"
  }'
```

# Test API
```
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"firstName": "Test", "lastName": "User", "email": "test@example.com", "fhirId": "test-123"}'
```

# shortcuts
```
mvn clean package -DskipTests && docker compose down && docker compose up -d
mvn clean package -DskipTests && docker compose restart app
docker compose down && mvn clean package -DskipTests && docker compose up -d
pkill -f java
```

# update the pom.xml with the new version
```xml
<version>1.1.0-SNAPSHOT</version>
```

```
curl -X POST http://localhost:8080/api/test/qr \
  -H "Content-Type: application/json" \
  -d '{
    "dataTypes": ["DEMOGRAPHICS", "VITALS", "MEDICATIONS", "ALLERGIES"],
    "expirationMinutes": 60,
    "purpose": "Testing the testable tested tests for testing"
  }'
```

```
sleep 10 && curl -X POST http://localhost:8080/api/qr/generate \
  -H "Content-Type: application/json" \
  -d '{}'
```

```
sleep 5 && curl -X POST http://localhost:8080/api/qr/generate
```