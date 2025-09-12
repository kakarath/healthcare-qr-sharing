# 📱 Mobile Testing Guide - Samsung Galaxy S20 & iPad 9th Gen

## 🚀 **Quick Mobile Testing Setup**

### **Option 1: Local Network Testing (Fastest)**
```bash
# 1. Build and run locally
export ENCRYPTION_KEY=$(openssl rand -base64 32)
export DEFAULT_USER_PASSWORD="testpass123"
export DEFAULT_PROVIDER_PASSWORD="providerpass123"

mvn clean package -DskipTests
java -jar target/healthcare-qr-sharing-3.0.0.jar

# 2. Find your local IP
ifconfig | grep "inet " | grep -v 127.0.0.1

# 3. Access from mobile devices
# Samsung Galaxy S20: http://YOUR_LOCAL_IP:8080
# iPad 9th Gen: http://YOUR_LOCAL_IP:8080
```

### **Option 2: ngrok Tunnel (Public Testing)**
```bash
# 1. Install ngrok
brew install ngrok  # macOS
# or download from https://ngrok.com

# 2. Start your app locally
java -jar target/healthcare-qr-sharing-3.0.0.jar

# 3. Create secure tunnel
ngrok http 8080

# 4. Use the https URL on mobile devices
# Example: https://abc123.ngrok.io
```

### **Option 3: Cloud Deployment (Production-like)**
```bash
# Deploy to Railway (fastest cloud option)
# 1. Install Railway CLI
npm install -g @railway/cli

# 2. Login and deploy
railway login
railway init
railway up

# 3. Set environment variables
railway variables set ENCRYPTION_KEY=$(openssl rand -base64 32)
railway variables set DEFAULT_USER_PASSWORD="testpass123"
railway variables set DEFAULT_PROVIDER_PASSWORD="providerpass123"
```

## 📱 **Mobile Testing Checklist**

### **Samsung Galaxy S20 Testing**
- [ ] Open Chrome/Samsung Internet
- [ ] Navigate to app URL
- [ ] Test QR code generation
- [ ] Test camera QR scanning
- [ ] Test responsive design
- [ ] Test touch interactions
- [ ] Test form inputs

### **iPad 9th Gen Testing**
- [ ] Open Safari
- [ ] Navigate to app URL  
- [ ] Test tablet layout
- [ ] Test QR code generation
- [ ] Test camera permissions
- [ ] Test landscape/portrait modes
- [ ] Test touch gestures

## 🔧 **Mobile-Specific Configuration**

### **Enable HTTPS for Camera Access**
```yaml
# application-mobile.yml
server:
  ssl:
    enabled: true
    key-store: ssl/keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
  port: 8443

spring:
  profiles:
    active: mobile
```

### **Mobile-Optimized Frontend**
```html
<!-- Add to index.html -->
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="default">
```

## 📊 **Testing URLs**

```bash
# Health Check
curl https://your-app-url/api/health

# QR Generation Test
curl -X POST https://your-app-url/api/qr/generate \
  -H "Content-Type: application/json" \
  -d '{"purpose": "Mobile testing"}'

# Login Test  
curl -X POST https://your-app-url/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"testpass123"}'
```