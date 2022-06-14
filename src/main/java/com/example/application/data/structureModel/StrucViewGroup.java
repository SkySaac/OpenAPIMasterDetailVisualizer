package com.example.application.data.structureModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class StrucViewGroup {
    private String tagName;
    private List<String> primaryPaths;
    private Map<String, String> secondaryPaths;
    private Map<String, StrucSchema> strucSchemaMap;
    private Map<String, Map<HttpMethod, StrucPath>> strucPathMap;

}
