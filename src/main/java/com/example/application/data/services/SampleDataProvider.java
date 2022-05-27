package com.example.application.data.services;

import com.example.application.data.dataModel.DataSchema;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SampleDataProvider {
    public List<DataSchema> getSampleData(){
        List<DataSchema> dataobjects = new ArrayList<>();

        Map<String,String> propertyMap = new HashMap<>();
        propertyMap.put("creationDate","16.07.2078");
        propertyMap.put("modificationDate","16.09.2078");
        propertyMap.put("remoteId","genesis");
        propertyMap.put("title","Example Title");
        propertyMap.put("description","Example Description");
        propertyMap.put("numAccessed","5");
        propertyMap.put("byteSize","23456789");
        propertyMap.put("checkSum","0");
        DataSchema dataSchema = new DataSchema("ArtifactView",propertyMap);
        dataobjects.add(dataSchema);

        Map<String,String> propertyMap2 = new HashMap<>();
        propertyMap2.put("creationDate","16.07.2878");
        propertyMap2.put("modificationDate","13.09.2078");
        propertyMap2.put("remoteId","genesis");
        propertyMap2.put("title","Example Title 2");
        propertyMap2.put("description","Example Description 2");
        propertyMap2.put("numAccessed","56");
        propertyMap2.put("byteSize","3");
        propertyMap2.put("checkSum","6");
        DataSchema dataSchema2 = new DataSchema("ArtifactView",propertyMap2);
        dataobjects.add(dataSchema2);

        Map<String,String> propertyMap3 = new HashMap<>();
        propertyMap3.put("creationDate","16.07.2898");
        propertyMap3.put("modificationDate","05.10.2078");
        propertyMap3.put("remoteId","genesis");
        propertyMap3.put("title","Example Title 3");
        propertyMap3.put("description","Example Description 3");
        propertyMap3.put("numAccessed","0");
        propertyMap3.put("byteSize","3675");
        propertyMap3.put("checkSum","4567");
        DataSchema dataSchema3 = new DataSchema("ArtifactView",propertyMap3);
        dataobjects.add(dataSchema3);

//        Map<String,String> propertyMap2 = new HashMap<>();
//        propertyMap2.put("SampleProperty21","this is a sample property2");
//        propertyMap2.put("SampleProperty22","this is another sample property2");
//        propertyMap2.put("SampleProperty23","this is yet another sample property2");
//        DataSchema dataSchema2 = new DataSchema("SampleView",propertyMap2);
//        dataobjects.add(dataSchema2);


        return dataobjects;
    }
}
