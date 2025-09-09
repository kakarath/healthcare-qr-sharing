# Healthcare QR Sharing - API Endpoints Reference

## 🔗 **Core Application Endpoints**

### **Health Check**
```bash
curl -s http://localhost:8080/api/health
```

### **Version Info**
```bash
curl -s http://localhost:8080/api/version
```

### **HIPAA Compliance Status** ⭐ NEW
```bash
curl -s http://localhost:8080/api/compliance
```

## 🔐 **Authentication Endpoints**

### **Login**
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### **Test Account Lockout** (3 failed attempts)
```bash
for i in {1..4}; do
  curl -X POST http://localhost:8080/api/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"wrong"}'
done
```

## 📱 **QR Code Endpoints**

### **Generate QR Code** (Basic)
```bash
curl -X POST http://localhost:8080/api/qr/generate \
  -H "Content-Type: application/json" \
  -d '{}'
```

### **Generate QR Code** (Full Parameters)
```bash
curl -X POST http://localhost:8080/api/qr/generate \
  -H "Content-Type: application/json" \
  -d '{
    "dataTypes": ["DEMOGRAPHICS", "VITALS", "MEDICATIONS"],
    "expirationMinutes": 15,
    "purpose": "Emergency department visit for chest pain evaluation"
  }'
```

### **Generate QR Code** (Purpose Validation Test - Will Fail)
```bash
curl -X POST http://localhost:8080/api/qr/generate \
  -H "Content-Type: application/json" \
  -d '{
    "purpose": "test"
  }'
```

### **Scan QR Code**
```bash
curl -X POST http://localhost:8080/api/qr/scan \
  -H "Content-Type: application/json" \
  -d '{
    "qrData": "encrypted-qr-payload"
  }'
```

## 👥 **Patient Management Endpoints**

### **Create Patient**
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "fhirId": "patient-123"
  }'
```

### **Get Patient by ID**
```bash
curl -s http://localhost:8080/api/patients/patient-123
```

## 🧪 **Testing & Development**

### **Quick Health Check**
```bash
curl -s http://localhost:8080/api/health | jq .
```

### **Test All Endpoints**
```bash
# Health check
curl -s http://localhost:8080/api/health

# Compliance status
curl -s http://localhost:8080/api/compliance

# Generate QR
curl -X POST http://localhost:8080/api/qr/generate \
  -H "Content-Type: application/json" \
  -d '{"purpose": "Emergency department visit"}'

# Create patient
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"firstName": "Test", "lastName": "User", "email": "test@example.com"}'
```

## 🚀 **Application Management**

### **Start Application**
```bash
mvn spring-boot:run -Dspring-boot.run.main-class=com.healthcare.MinimalApp
```

### **Kill and Restart**
```bash
lsof -ti:8080 | xargs kill -9 && sleep 2 && mvn spring-boot:run -Dspring-boot.run.main-class=com.healthcare.MinimalApp
```

### **Build and Package**
```bash
mvn clean package -DskipTests
```

## 🌐 **Web Interface URLs**

```
Main App: http://localhost:8080/
Health Check: http://localhost:8080/api/health
Compliance Status: http://localhost:8080/api/compliance
Version Info: http://localhost:8080/api/version
```

## 📊 **Response Examples**

### **Health Check Response**
```json
{
  "components": {
    "database": "UP",
    "qrGenerator": "UP",
    "consentService": "UP",
    "authService": "UP"
  },
  "version": "3.0.0",
  "status": "UP",
  "timestamp": "2025-09-08T22:18:12",
  "uptime": "00:00:30"
}
```

### **Compliance Status Response**
```json
{
  "encryption": "AES-256-GCM",
  "authentication": "BCrypt + Session",
  "auditLogging": "Enabled",
  "backupRetention": "7 years",
  "accessControl": "Role-based",
  "hipaaCompliance": "98%"
}
```

### **QR Generation Response**
```json
{
  "sessionId": "session-abc123",
  "qrCodeImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "expiresAt": "2025-09-04T13:00:00",
  "success": true,
  "message": "QR Generated Successfully!"
}
```