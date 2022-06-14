package com.example.application.data.dataModel;

import com.example.application.data.structureModel.PropertyTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataValue {
    private PropertyTypeEnum propertyTypeEnum;
    private String plainValue; //If this value is a string or number
    private List<DataSchema> dataSchemas; //If this value is an array
    private Map<String, DataSchema> properties; //If this value is an object
    public DataValue(String plainValue, PropertyTypeEnum propertyTypeEnum){
        this.propertyTypeEnum = propertyTypeEnum;
        this.plainValue = plainValue;
    }

    public DataValue(List<DataSchema> dataSchemas,PropertyTypeEnum propertyTypeEnum){
        this.propertyTypeEnum = propertyTypeEnum;
        this.dataSchemas = dataSchemas;
    }
    public DataValue(Map<String, DataSchema> properties,PropertyTypeEnum propertyTypeEnum){
        this.propertyTypeEnum = propertyTypeEnum;
        this.properties = properties;
    }

    public DataSchema get(String key){
        return properties.get(key);
    }
}
