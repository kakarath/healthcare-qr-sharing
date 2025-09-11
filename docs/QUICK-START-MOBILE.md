# 📱 Quick Start - Mobile Testing

## 🚀 **1-Minute Mobile Setup**

### **Step 1: Build & Run**
```bash
# Set environment variables
export ENCRYPTION_KEY=$(openssl rand -base64 32)
export DEFAULT_USER_PASSWORD="testpass123"
export DEFAULT_PROVIDER_PASSWORD="providerpass123"

# Build and run
mvn clean package -DskipTests
java -jar target/healthcare-qr-sharing-3.0.0.jar
```

### **Step 2: Find Your IP Address**
```bash
# macOS/Linux
ifconfig | grep "inet " | grep -v 127.0.0.1

# Example output: 192.168.1.100
```

### **Step 3: Test on Mobile Devices**
```
Samsung Galaxy S20: http://192.168.1.100:8080
iPad 9th Gen: http://192.168.1.100:8080
```

## 📋 **Mobile Test Checklist**

### **Samsung Galaxy S20**
- [ ] Open Chrome browser
- [ ] Navigate to `http://YOUR_IP:8080`
- [ ] Test login: `user@example.com` / `testpass123`
- [ ] Generate QR code
- [ ] Test camera scanning
- [ ] Check responsive design

### **iPad 9th Gen**
- [ ] Open Safari browser
- [ ] Navigate to `http://YOUR_IP:8080`
- [ ] Test tablet layout
- [ ] Test touch interactions
- [ ] Test landscape/portrait modes

## 🔧 **Alternative: ngrok (Public URL)**

```bash
# Install ngrok
brew install ngrok

# Start app locally
java -jar target/healthcare-qr-sharing-3.0.0.jar

# In another terminal, create tunnel
ngrok http 8080

# Use the https URL on mobile devices
# Example: https://abc123.ngrok.io
```

## 🐳 **Docker Option**

```bash
# Mobile-optimized deployment
docker-compose -f docker-compose.mobile.yml up -d

# Check status
docker-compose -f docker-compose.mobile.yml ps

# View logs
docker-compose -f docker-compose.mobile.yml logs -f app
```

## 📊 **Test Endpoints**

```bash
# Health check
curl http://YOUR_IP:8080/api/health

# Login test
curl -X POST http://YOUR_IP:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"testpass123"}'

# QR generation test
curl -X POST http://YOUR_IP:8080/api/qr/generate \
  -H "Content-Type: application/json" \
  -d '{"purpose":"Mobile testing"}'
```

## 🎯 **Expected Results**

### **Health Check Response**
```json
{
  "status": "UP",
  "version": "3.0.0",
  "components": {
    "database": "UP",
    "qrGenerator": "UP"
  }
}
```

### **QR Generation Response**
```json
{
  "sessionId": "session-abc123",
  "qrCodeImage": "data:image/png;base64,iVBORw0KGgo...",
  "success": true,
  "message": "QR Generated Successfully!"
}
```

**Total Setup Time**: 2-3 minutes
**Mobile Testing Ready**: ✅