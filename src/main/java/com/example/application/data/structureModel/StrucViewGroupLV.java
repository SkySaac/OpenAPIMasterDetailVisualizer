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
    private List<String> notMatchedPrimaryPaths; //TODO benötigt ? -> Ja
    private Map<String, String> secondaryPaths; //TODO benötigt ? -> Nein
    private Map<String, StrucSchema> notMatchedStrucSchemaMap;
    private Map<String, Map<HttpMethod, StrucPath>> notMatchedStrucPathMap;

    private Map<String,StrucViewGroupMDV> strucViewGroupMDVS; //TODO path -> SVGMDV
}
