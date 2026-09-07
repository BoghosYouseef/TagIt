package com.tagit.file;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.file.Path;

@Converter(autoApply = true)
public class PathConverter implements AttributeConverter<Path, String> {

    @Override
    public String convertToDatabaseColumn(Path path) {
        return path != null ? path.toAbsolutePath().toString() : null;
    }

    @Override
    public Path convertToEntityAttribute(String dbData) {
        return dbData != null ? Path.of(dbData) : null;
    }
}
