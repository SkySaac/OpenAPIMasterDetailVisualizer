package com.example.application.data.structureModel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
public class StrucPath {
    //TODO was wenn request/response body keine schemas sind sondern direkt objekte ?
    private String path;
    private HttpMethod httpMethod;
    private String externalResponseBodySchemaName; // im String steht der Name des Schemas
    private StrucSchema responseStrucSchema;
    private String externalRequestBodySchemaName; // im String steht der Name des Schemas
    private StrucSchema requestStrucSchema;
    //TODO: query params
    //TODO: path params
    //TODO: sonstiges

    public boolean isExternalResponseSchema() {
        return externalResponseBodySchemaName != null;
    }

    public boolean isExternalRequestSchema() {
        return externalRequestBodySchemaName != null;
    }
}
