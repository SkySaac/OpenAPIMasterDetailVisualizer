package com.example.application.data.dataModel;

import com.example.application.data.structureModel.PropertyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataValue {
    private PropertyTypeEnum propertyTypeEnum;
    private String value; //If this value is a string or number
    private List<DataSchema> dataSchemas; //If this value is an array
    private Map<String, DataSchema> properties; //If this value is an object
    public DataValue(String value,PropertyTypeEnum propertyTypeEnum){
        this.propertyTypeEnum = propertyTypeEnum;
        this.value = value;
    }

    public DataValue(List<DataSchema> dataSchemas,PropertyTypeEnum propertyTypeEnum){
        this.propertyTypeEnum = propertyTypeEnum;
        this.dataSchemas = dataSchemas;
    }
    public DataValue(Map<String, DataSchema> properties,PropertyTypeEnum propertyTypeEnum){
        this.propertyTypeEnum = propertyTypeEnum;
        this.properties = properties;
    }
}
