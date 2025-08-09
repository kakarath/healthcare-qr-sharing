# API Documentation

## Patient Management

### Create Patient
```
POST /api/patients
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

### Get Patient by ID
```
GET /api/patients/{id}
```

### Get Patient by Email
```
GET /api/patients/email/{email}
```

## QR Code Management

### Generate QR Code
```
POST /api/qr/generate?patientId=1&medicalData=Blood%20Type:%20O+
```

### Get QR Code Image
```
GET /api/qr/image/{qrCodeId}
Returns: PNG image
```

### Get QR Code Data
```
GET /api/qr/data/{qrCodeId}
Returns: JSON with medical data
```