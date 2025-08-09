# Healthcare QR Data Sharing System

A secure, FHIR-compliant healthcare data sharing system that allows patients to share their health data via QR codes with healthcare providers.

## Features

- **Secure QR Code Generation**: Time-limited, encrypted QR codes for data sharing
- **FHIR Compliance**: Built on HL7 FHIR R4 standard
- **Granular Consent Management**: Patient-controlled data sharing permissions
- **SMART on FHIR Authentication**: Industry-standard OAuth 2.0 security
- **End-to-End Encryption**: AES-256-GCM encryption for data at rest and in transit
- **Comprehensive Audit Logging**: Full compliance with healthcare regulations
- **Responsive Web Interface**: Patient and provider portals

- Patient management
- QR code generation for medical data
- Secure data sharing
- RESTful API endpoints

## Quick Start

1. Clone the repository
2. Run `mvn spring-boot:run`
3. Access the application at `http://localhost:8080`
4. H2 console available at `http://localhost:8080/h2-console`

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL 13+
- H2 Database (for development) -- considering

### Running with Docker

1. Clone the repository:
    ```
    git clone https://github.com/kakarath/healthcare-qr-sharing.git
    cd healthcare-qr-sharing
    ```
2. create a `.env` file in the root directory with the following content:
    ```
    DB_URL=jdbc:postgresql://localhost:5432/healthcare
    DB_USERNAME=your_db_username
    DB_PASSWORD=your_db_password
    ```

   Replace `your_db_username` and `your_db_password` with your PostgreSQL credentials.
   If you want to use H2 for development, you can set:
    ```
    DB_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    DB_USERNAME=sa
    DB_PASSWORD=password
    ```
3. Build the application:
    ```
    mvn clean package -DskipTests
    ```
3. Build the Docker image:
    ```
    docker compose up -d
    ```
4. Access the application at `http://localhost:8080`
5. Generate encryption key:
    ```
    openssl rand -base64 32
    ```
6. Generate JWT secret:
    ```
    openssl rand -base64 64
    ```
7. Export the keys as environment variables:
    ```
    export ENCRYPTION_KEY="your-base64-encoded-key" 
    export JWT_SECRET="your-jwt-secret" 
    export OAUTH_ISSUER_URI="[https://your-oauth-provider.com](https://your-oauth-provider.com)" 
    export FHIR_SERVER_URL="[https://your-fhir-server.com/fhir](https://your-fhir-server.com/fhir)"
    ```
   Alternatively, you can set these in your `.env` file as well.
8. Set the generated keys in your `.env` file:
    ```
    ENCRYPTION_KEY=your_generated_encryption_key
    JWT_SECRET=your_generated_jwt_secret
    ```


## API Endpoints

### Patients
- `POST /api/patients` - Create a new patient
- `GET /api/patients/{id}` - Get patient by ID
- `GET /api/patients/email/{email}` - Get patient by email

### QR Codes
- `POST /api/qr/generate` - Generate QR code for patient data
- `GET /api/qr/image/{qrCodeId}` - Get QR code image
- `GET /api/qr/data/{qrCodeId}` - Get QR code data

## Technology Stack

- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- H2 Database
- PostgreSQL (later)
- ZXing (QR Code generation)
- Maven

## Security Features

- **Multi-Factor Authentication**: TOTP-based 2FA
- **Time-Limited Sessions**: Configurable QR code expiration
- **Zero Trust Architecture**: Every request verified
- **Data Minimization**: Only requested data types shared
- **Audit Trail**: Complete access logging for compliance

## API Documentation
/healthcare-qr-sharing/docs/API.md

### Generate QR Code
http POST /api/qr/generate Authorization: Bearer {token} Content-Type: application/json
```json
{
    "patientId": 1,
    "medicalData": "Blood Type: O+",
    "dataTypes": ["DEMOGRAPHICS", "VITALS", "MEDICATIONS"],
    "expirationMinutes": 15,
    "purpose": "Emergency department visit"
}
```
### Scan QR Code
```
http POST /api/qr/scan Authorization: Bearer {token} Content-Type: application/json
{ "qrData": "encrypted-qr-payload" }
```

## Compliance

- **HIPAA**: Business Associate Agreement required
- **GDPR**: Right to erasure implemented
- **FDA**: Software as Medical Device considerations
- **HL7 FHIR R4**: Full compliance with healthcare interoperability standards

## Development

### Running Tests
```bash
mvn test
```
## Code Quality
SonarQube for code quality analysis
- Install SonarQube locally or use a cloud instance
- Configure `sonar-project.properties` in the root directory
- Run SonarQube analysis:
```
mvn sonar:sonar
```
## Database Migrations
- Flyway for schema migrations
```
mvn flyway:migrate
```

## Contributing
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Submit a Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support (coming soon)
For support and questions:
- Create an issue on GitHub
- Email: support@healthcare-qr.com
- Documentation: [https://docs.healthcare-qr.com](https://docs.healthcare-qr.com)

#### Contact (coming soon)
- **Email**: support@healthcare-qr.com
- **GitHub**: [kakarath/healthcare-qr-sharing](https://github.com/kakarath/healthcare-qr-sharing)
- **Documentation**: [https://docs.healthcare-qr.com](https://docs.healthcare-qr.com)

#### Tenets
My approach to software development is guided by the following principles:
- **Simplicity**: Keep the architecture and codebase simple and understandable
- **Modularity**: Use microservices and modular design for flexibility
- **Testability**: Write tests for all critical components
- **Documentation**: Maintain clear and comprehensive documentation
- **User-Centric**: Designed with patient and provider needs in mind
- **Data Privacy**: Patient data is encrypted and shared only with consent
- **Open Source**: Community-driven development
- **Security**: Built with security as a top priority
- **Compliance**: Adheres to healthcare regulations
- **Interoperability**: FHIR-compliant for seamless data exchange
1. **Secure** - Multiple layers of security including encryption, authentication, and authorization
2. **Compliant** - Follows HIPAA, GDPR, and FHIR standards
3. **Scalable** - Microservices-ready architecture
4. **Maintainable** - Clean code structure with separation of concerns
5. **Testable** - Comprehensive testing framework setup

