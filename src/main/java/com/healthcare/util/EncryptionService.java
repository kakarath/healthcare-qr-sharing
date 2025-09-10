package com.healthcare.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Component
public class EncryptionService {
    
    @Value("${healthcare.security.encryption.key:}")
    private String encryptionKeyBase64;
    
    @Value("${healthcare.security.encryption.algorithm:AES/GCM/NoPadding}")
    private String algorithm;
    
    private static final int GCM_TAG_LENGTH = 16;
    private static final int GCM_IV_LENGTH = 12;
    
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.trim().isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(algorithm);
            
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and ciphertext
            byte[] encryptedWithIv = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(ciphertext, 0, encryptedWithIv, iv.length, ciphertext.length);
            
            return Base64.getEncoder().encodeToString(encryptedWithIv);
            
        } catch (Exception e) {
            // Log encryption failure without exposing sensitive data
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    public String decrypt(String encryptedData) {
        try {
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedData);
            
            // Extract IV and ciphertext
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] ciphertext = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            
            System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
            System.arraycopy(encryptedWithIv, iv.length, ciphertext, 0, ciphertext.length);
            
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(algorithm);
            
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            // Log decryption failure without exposing sensitive data
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    private SecretKey getSecretKey() {
        if (encryptionKeyBase64 == null || encryptionKeyBase64.trim().isEmpty()) {
            throw new IllegalStateException("ENCRYPTION_KEY environment variable must be set");
        }
        
        try {
            byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyBase64);
            if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
                throw new IllegalArgumentException("Invalid AES key length: " + keyBytes.length + " bytes");
            }
            return new SecretKeySpec(keyBytes, "AES");
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid encryption key format", e);
        }
    }
}