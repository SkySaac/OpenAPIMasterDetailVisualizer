package com.example.application.data.structureModel;

public enum PropertyTypeEnum {
    STRING,
    NUMBER,
    BOOLEAN,
    SCHEMA,
    ARRAY,
    OBJECT;

    public static PropertyTypeEnum fromString(String value) {
        switch (value) {
            case "string":
                return STRING;
            case "number", "integer":
                return NUMBER;
            case "boolean":
                return BOOLEAN;
            case "object":
                return OBJECT;
            case "array":
                return ARRAY;
            default:
                return STRING;
        }
    }
}
