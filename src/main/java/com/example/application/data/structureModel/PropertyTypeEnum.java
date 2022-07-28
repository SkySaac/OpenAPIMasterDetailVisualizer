package com.example.application.data.structureModel;

public enum PropertyTypeEnum {
    STRING,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    SCHEMA,
    ARRAY,
    OBJECT;

    public static PropertyTypeEnum fromString(String value) {
        return switch (value) {
            case "integer" -> INTEGER;
            case "number" -> DOUBLE;
            case "boolean" -> BOOLEAN;
            case "object" -> OBJECT;
            case "array" -> ARRAY;
            default -> STRING;
        };
    }
}
