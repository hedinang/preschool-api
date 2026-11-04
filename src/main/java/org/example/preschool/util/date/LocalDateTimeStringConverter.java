package org.example.preschool.util.date;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Converter
public class LocalDateTimeStringConverter implements AttributeConverter<String, String> {

    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            if (attribute != null) {
                return LocalDateTime.now().format(OUTPUT_FORMATTER);
            }
            return null;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            if (dbData != null) {
                return dbData;
            }
            return null;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}