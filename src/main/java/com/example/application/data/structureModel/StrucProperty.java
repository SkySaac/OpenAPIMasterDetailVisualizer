package com.example.application.data.structureModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StrucProperty {
    private PropertyTypeEnum type;
    private StrucSchema schema; //if type is object the internal schema
    private String ref; //if type is array this is the reference to the schema
    private List<StrucProperty> arrayElements;

    public StrucProperty(PropertyTypeEnum propertyTypeEnum){
        type = propertyTypeEnum;
    }
}
