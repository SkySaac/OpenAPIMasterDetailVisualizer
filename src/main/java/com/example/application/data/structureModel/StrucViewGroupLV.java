package com.example.application.data.structureModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class StrucViewGroupLV {
    private String tagName;
    private List<String> primaryPaths; //TODO benötigt ?
    private Map<String, String> secondaryPaths; //TODO benötigt ?
    private Map<String, StrucSchema> strucSchemaMap;
    private Map<String, Map<HttpMethod, StrucPath>> strucPathMap;
}
