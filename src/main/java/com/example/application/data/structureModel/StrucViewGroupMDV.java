package com.example.application.data.structureModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class StrucViewGroupMDV {
    private String tagName;
    private StrucSchema behindPagedGetSchema;
    private Map<HttpMethod,StrucPath> strucPathMap;
    private Map<HttpMethod,StrucSchema> strucSchemaMap;

    public boolean isPaged(){
        return behindPagedGetSchema!=null;
    }
}

