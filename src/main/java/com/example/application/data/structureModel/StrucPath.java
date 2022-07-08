package com.example.application.data.structureModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StrucPath {
    //TODO was wenn request/response body keine schemas sind sondern direkt objekte ?
    private String path;
    private HttpMethod httpMethod;
    private StrucSchema responseStrucSchema;
    private StrucSchema requestStrucSchema;
    private List<StruckQueryParameter> queryParams = new ArrayList<>();
    //TODO: path params
    //TODO: sonstiges

    @Getter
    @AllArgsConstructor
    public static class StruckQueryParameter {
        private String name;
        private PropertyTypeEnum type;
        private boolean required;
    }
}
