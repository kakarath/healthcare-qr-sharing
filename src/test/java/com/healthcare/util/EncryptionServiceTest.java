package com.healthcare.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;
    private final String testKey = Base64.getEncoder().encodeToString("test-encryption-key-256-bits".getBytes());

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        ReflectionTestUtils.setField(encryptionService, "encryptionKeyBase64", testKey);
        ReflectionTestUtils.setField(encryptionService, "algorithm", "AES/GCM/NoPadding");
    }

    @Test
    void encrypt_ValidPlaintext_ShouldReturnEncryptedString() {
        // Given
        String plaintext = "sensitive-health-data";

        // When
        String encrypted = encryptionService.encrypt(plaintext);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);
        assertTrue(encrypted.length() > plaintext.length());
    }

    @Test
    void decrypt_ValidEncryptedData_ShouldReturnOriginalPlaintext() {
        // Given
        String originalText = "patient-health-information";
        String encrypted = encryptionService.encrypt(originalText);

        // When
        String decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertEquals(originalText, decrypted);
    }

    @Test
    void encryptDecrypt_MultipleRounds_ShouldMaintainDataIntegrity() {
        // Given
        String originalData = "complex-medical-data-with-special-chars-!@#$%^&*()";

        // When
        String encrypted1 = encryptionService.encrypt(originalData);
        String decrypted1 = encryptionService.decrypt(encrypted1);
        String encrypted2 = encryptionService.encrypt(decrypted1);
        String decrypted2 = encryptionService.decrypt(encrypted2);

        // Then
        assertEquals(originalData, decrypted1);
        assertEquals(originalData, decrypted2);
        assertNotEquals(encrypted1, encrypted2); // Each encryption should be unique due to random IV
    }

    @Test
    void decrypt_InvalidData_ShouldThrowException() {
        // Given
        String invalidEncryptedData = "invalid-base64-data";

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> encryptionService.decrypt(invalidEncryptedData));
    }
}