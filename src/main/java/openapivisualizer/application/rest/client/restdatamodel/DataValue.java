package openapivisualizer.application.rest.client.restdatamodel;

import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataValue {
    private DataPropertyType dataPropertyType;
    private String plainValue; //If this value is a string or number
    private List<DataSchema> dataSchemas = new ArrayList<>(); //If this value is an array
    private Map<String, DataSchema> properties = new HashMap<>(); //If this value is an object
    public DataValue(String plainValue, DataPropertyType dataPropertyType){
        this.dataPropertyType = dataPropertyType;
        this.plainValue = plainValue;
    }

    public DataValue(List<DataSchema> dataSchemas, DataPropertyType dataPropertyType){
        this.dataPropertyType = dataPropertyType;
        this.dataSchemas = dataSchemas;
    }
    public DataValue(Map<String, DataSchema> properties, DataPropertyType dataPropertyType){
        this.dataPropertyType = dataPropertyType;
        this.properties = properties;
    }

    public DataSchema get(String key){
        return properties.get(key);
    }
}
