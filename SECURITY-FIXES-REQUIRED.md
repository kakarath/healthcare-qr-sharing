# 🚨 CRITICAL SECURITY FIXES REQUIRED

## **IMMEDIATE ACTION ITEMS**

### **1. Remove Hardcoded Credentials**

**File**: `src/main/java/com/healthcare/service/AuthService.java`
```java
// REMOVE THESE LINES (31-33):
users.put("user@example.com", "password123");
users.put("provider@example.com", "provider123");

// REPLACE WITH:
@Value("${app.default.user.email:user@example.com}")
private String defaultUserEmail;

@Value("${app.default.user.password}")
private String defaultUserPassword;

@Value("${app.default.provider.password}")
private String defaultProviderPassword;
```

### **2. Fix Encryption Service**

**File**: `src/main/java/com/healthcare/util/EncryptionService.java`
```java
// REMOVE DEFAULT KEY (lines 82-86):
// Replace with:
@PostConstruct
private void validateConfiguration() {
    if (encryptionKey == null || encryptionKey.trim().isEmpty()) {
        throw new IllegalStateException("ENCRYPTION_KEY environment variable must be set");
    }
}
```

### **3. Enable Security Configuration**

**File**: `src/main/resources/application-simple.yml`
```yaml
# REMOVE:
security:
  enabled: false

# REPLACE WITH:
security:
  enabled: true
```

### **4. Fix SQL Injection**

**File**: `src/main/java/com/healthcare/config/DatabaseConfig.java`
```java
// REPLACE line 48-49:
try (Statement statement = connection.createStatement()) {
    statement.execute(sqlContent);
}

// WITH:
try (PreparedStatement statement = connection.prepareStatement(sqlContent)) {
    statement.execute();
}
```

### **5. Add XSS Protection**

**File**: `src/main/resources/static/js/app.js`
```javascript
// REPLACE line 49-50:
document.getElementById('result').innerHTML = response.message;

// WITH:
document.getElementById('result').textContent = response.message;
```

## **ENVIRONMENT VARIABLES TO SET**

```bash
# Required environment variables
export ENCRYPTION_KEY=$(openssl rand -base64 32)
export JWT_SECRET=$(openssl rand -base64 64)
export DEFAULT_USER_PASSWORD=$(openssl rand -base64 16)
export DEFAULT_PROVIDER_PASSWORD=$(openssl rand -base64 16)
export DB_PASSWORD=$(openssl rand -base64 16)
```

## **VERIFICATION COMMANDS**

```bash
# 1. Check for hardcoded credentials
grep -r "password123\|provider123" src/

# 2. Verify security is enabled
grep -r "enabled: false" src/main/resources/

# 3. Test authentication
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"invalid","password":"invalid"}'

# Should return 401 Unauthorized
```

## **CRITICAL PRIORITY ORDER**

1. **Remove hardcoded credentials** (IMMEDIATE)
2. **Enable security configuration** (IMMEDIATE)  
3. **Fix SQL injection** (HIGH)
4. **Add XSS protection** (HIGH)
5. **Set environment variables** (HIGH)

**DO NOT DEPLOY TO PRODUCTION UNTIL ALL FIXES ARE COMPLETE**