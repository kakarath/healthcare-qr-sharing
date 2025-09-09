# Security Guidelines for Healthcare QR Sharing

## 🚫 **NEVER COMMIT TO GITHUB**

### **SSL Certificates & Keys**
- `ssl/*.key` - Private keys
- `ssl/*.p12` - PKCS12 keystores
- `ssl/*.crt` - SSL certificates
- `ssl/*.pem` - PEM format files

### **Environment Files**
- `.env` - Database passwords, encryption keys
- `.env.production` - Production secrets
- `.env.local` - Local development secrets

### **Database Files**
- `*.sql` - Database dumps with PHI
- `backups/` - Backup files
- Database connection strings with passwords

### **Log Files**
- `logs/` - May contain sensitive data
- `*.log` - Application logs with PHI

## ✅ **SAFE TO COMMIT**

### **Documentation**
- `README.md` - Project documentation
- `docs/*.md` - Technical documentation
- `ssl/SSL-SETUP.md` - Setup instructions (no keys)

### **Configuration Templates**
- `application.yml` - With environment variables
- `docker-compose.yml` - With env var references
- `.env.example` - Template without real values

### **Source Code**
- `src/` - Application source code
- `pom.xml` - Maven configuration
- `Dockerfile` - Container configuration

## 🔒 **Environment Variables**

Use environment variables for all secrets:

```bash
# Database
export DB_PASSWORD="secure_password_here"
export DB_USERNAME="healthcare_user"

# Encryption
export ENCRYPTION_KEY="base64_encoded_key_here"
export JWT_SECRET="jwt_secret_here"

# SSL
export SSL_KEYSTORE_PASSWORD="keystore_password_here"
```

## 🛡️ **Security Checklist**

### **Before Each Commit**
- [ ] Check `.gitignore` is up to date
- [ ] Verify no `.env` files staged
- [ ] Confirm no SSL keys in commit
- [ ] Review diff for sensitive data
- [ ] Use `git status` to check staged files

### **Repository Security**
- [ ] Enable branch protection
- [ ] Require pull request reviews
- [ ] Enable secret scanning
- [ ] Set up dependency alerts
- [ ] Configure security advisories

## 🚨 **If Secrets Are Accidentally Committed**

### **Immediate Actions**
1. **Revoke compromised credentials immediately**
2. **Generate new secrets**
3. **Remove from git history**
4. **Force push to overwrite history**

### **Git History Cleanup**
```bash
# Remove sensitive files from history
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch ssl/*.key ssl/*.p12 .env' \
  --prune-empty --tag-name-filter cat -- --all

# Force push to all branches
git push origin --force --all
git push origin --force --tags
```

## 📋 **Production Deployment Security**

### **SSL Certificates**
- Generate on production server
- Use proper certificate authority
- Set up automatic renewal
- Monitor expiration dates

### **Database Security**
- Use strong passwords (32+ characters)
- Enable SSL connections
- Restrict network access
- Regular security updates

### **Application Security**
- Use HTTPS only
- Enable security headers
- Regular dependency updates
- Monitor for vulnerabilities

## 🔍 **Security Monitoring**

### **GitHub Security Features**
- Enable Dependabot alerts
- Use secret scanning
- Set up code scanning
- Monitor security advisories

### **Application Monitoring**
- Audit log monitoring
- Failed login alerts
- Unusual access patterns
- Certificate expiration alerts