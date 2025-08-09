package com.healthcare.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute; // Minimal implementation
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData; // Minimal implementation
    }
}