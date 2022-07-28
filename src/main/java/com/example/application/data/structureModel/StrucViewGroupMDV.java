package com.example.application.data.structureModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class StrucViewGroupMDV {
    private String tagName;
    private StrucSchema behindPagedGetSchema;
    private Map<HttpMethod,StrucPath> primaryStrucPathMap;
    private Map<HttpMethod,StrucSchema> strucSchemaMap;

    private final Map<String, StrucViewGroupMDV> internalMDVs = new HashMap<>();

    public boolean isPaged(){
        return behindPagedGetSchema!=null;
    }
}

