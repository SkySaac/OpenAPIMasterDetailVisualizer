package com.example.application.data.structureModel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StrucValue {
    private PropertyTypeEnum type;
    private String ref; //if type is array this is the reference to the schema
    private List<StrucSchema> arrayElements = new ArrayList<>();
    private Map<String, StrucSchema> properties = new HashMap<>();

    private StrucSchema additionalPropertySchema;

    public StrucValue(PropertyTypeEnum propertyTypeEnum){
        type = propertyTypeEnum;
    }

    public void addArrayElement(StrucSchema strucValue){
        arrayElements.add(strucValue);
    }

}
