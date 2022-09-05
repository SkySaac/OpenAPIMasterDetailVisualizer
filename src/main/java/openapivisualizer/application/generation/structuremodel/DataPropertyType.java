package openapivisualizer.application.generation.structuremodel;

public enum DataPropertyType {
    STRING,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    SCHEMA,
    ARRAY,

    FILE,
    OBJECT;

    public static DataPropertyType fromString(String value) {
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
