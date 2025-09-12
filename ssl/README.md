# SSL Certificate Directory

⚠️ **SECURITY WARNING**: This directory contains SSL certificates and private keys.

## 🚫 **DO NOT COMMIT TO VERSION CONTROL**

The following files should NEVER be committed to GitHub:
- `*.key` - Private keys
- `*.crt` - Certificates  
- `*.csr` - Certificate signing requests
- `*.p12` - PKCS12 keystores
- `*.pem` - PEM format files
- `keystore.*` - Java keystores

## ✅ **Safe to Commit**
- `SSL-SETUP.md` - Setup documentation
- `README.md` - This file
- `.gitkeep` - Keep directory in git

## 🔒 **Security Best Practices**

1. **Generate certificates locally** - Never share private keys
2. **Use environment variables** - Store passwords in `.env` files
3. **Rotate certificates regularly** - Set expiration reminders
4. **Backup securely** - Encrypted offline storage only

## 📋 **Production Deployment**

For production:
1. Generate certificates on production server
2. Use proper certificate authority (Let's Encrypt, DigiCert)
3. Set up automatic renewal
4. Monitor certificate expiration

## 🛡️ **If Accidentally Committed**

If you accidentally commit certificates:
1. **Immediately revoke** the certificates
2. **Generate new certificates**
3. **Remove from git history**: `git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch ssl/*.key ssl/*.p12' --prune-empty --tag-name-filter cat -- --all`
4. **Force push**: `git push origin --force --all`