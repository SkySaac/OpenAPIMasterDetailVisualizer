package com.example.application.data.services;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Deprecated
public class SampleDataProviderService {
    private DataSchema dataobjectsArtficatView;
    private DataSchema dataobjectsCatalogsView;

    public SampleDataProviderService() {

        Map<String,DataSchema> schemas1 = new HashMap<>();
        schemas1.put("creationDate",new DataSchema("creationDate", new DataValue("16.07.2078", PropertyTypeEnum.STRING)));
        schemas1.put("modificationDate",new DataSchema("modificationDate", new DataValue("16.09.2078", PropertyTypeEnum.STRING)));
        schemas1.put("remoteId",new DataSchema("remoteId", new DataValue("genesis", PropertyTypeEnum.STRING)));
        schemas1.put("title",new DataSchema("title", new DataValue("Example Title", PropertyTypeEnum.STRING)));
        schemas1.put("description",new DataSchema("description", new DataValue("Example Description", PropertyTypeEnum.STRING)));
        schemas1.put("numAccessed",new DataSchema("numAccessed", new DataValue("5", PropertyTypeEnum.NUMBER)));
        schemas1.put("byteSize",new DataSchema("byteSize", new DataValue("23456789", PropertyTypeEnum.NUMBER)));
        schemas1.put("checkSum",new DataSchema("checkSum", new DataValue("0", PropertyTypeEnum.NUMBER)));
        schemas1.put("_links",new DataSchema("_links", new DataValue(new ArrayList<>(), PropertyTypeEnum.OBJECT)));
        schemas1.put("additional",new DataSchema("additional", new DataValue(new ArrayList<>(), PropertyTypeEnum.OBJECT)));

        DataValue dataValue1 = new DataValue(schemas1, PropertyTypeEnum.OBJECT);
        DataSchema dataSchema1 = new DataSchema("ArtifactView", dataValue1);


        Map<String,DataSchema> schemas2 = new HashMap<>();
        schemas2.put("creationDate",new DataSchema("creationDate", new DataValue("16.07.2878", PropertyTypeEnum.STRING)));
        schemas2.put("modificationDate",new DataSchema("modificationDate", new DataValue("13.09.2078", PropertyTypeEnum.STRING)));
        schemas2.put("remoteId",new DataSchema("remoteId", new DataValue("genesis", PropertyTypeEnum.STRING)));
        schemas2.put("title",new DataSchema("title", new DataValue("Example Title 2", PropertyTypeEnum.STRING)));
        schemas2.put("description",new DataSchema("description", new DataValue("Example Description 2", PropertyTypeEnum.STRING)));
        schemas2.put("numAccessed",new DataSchema("numAccessed", new DataValue("56", PropertyTypeEnum.NUMBER)));
        schemas2.put("byteSize",new DataSchema("byteSize", new DataValue("3", PropertyTypeEnum.NUMBER)));
        schemas2.put("checkSum",new DataSchema("checkSum", new DataValue("6", PropertyTypeEnum.NUMBER)));
        schemas2.put("_links",new DataSchema("_links", new DataValue(new ArrayList<>(), PropertyTypeEnum.OBJECT)));
        schemas2.put("additional",new DataSchema("additional", new DataValue(new ArrayList<>(), PropertyTypeEnum.OBJECT)));

        DataValue dataValue2 = new DataValue(schemas2, PropertyTypeEnum.OBJECT);
        DataSchema dataSchema2 = new DataSchema("ArtifactView", dataValue2);


        Map<String,DataSchema> schemas3 = new HashMap<>();
        schemas3.put("creationDate",new DataSchema("creationDate", new DataValue("16.07.2898", PropertyTypeEnum.STRING)));
        schemas3.put("modificationDate",new DataSchema("modificationDate", new DataValue("05.10.2078", PropertyTypeEnum.STRING)));
        schemas3.put("remoteId",new DataSchema("remoteId", new DataValue("genesis", PropertyTypeEnum.STRING)));
        schemas3.put("title",new DataSchema("title", new DataValue("Example Title 3", PropertyTypeEnum.STRING)));
        schemas3.put("description",new DataSchema("description", new DataValue("Example Description 3", PropertyTypeEnum.STRING)));
        schemas3.put("numAccessed",new DataSchema("numAccessed", new DataValue("56", PropertyTypeEnum.NUMBER)));
        schemas3.put("byteSize",new DataSchema("byteSize", new DataValue("3675", PropertyTypeEnum.NUMBER)));
        schemas3.put("checkSum",new DataSchema("checkSum", new DataValue("4567", PropertyTypeEnum.NUMBER)));
        schemas3.put("_links",new DataSchema("_links", new DataValue(new ArrayList<>(), PropertyTypeEnum.OBJECT)));
        schemas3.put("additional",new DataSchema("additional", new DataValue(new ArrayList<>(), PropertyTypeEnum.OBJECT)));

        DataValue dataValue3 = new DataValue(schemas3, PropertyTypeEnum.OBJECT);
        DataSchema dataSchema3 = new DataSchema("ArtifactView", dataValue3);


        Map<String,DataSchema> schemas4 = new HashMap<>();
        schemas4.put("creationDate",new DataSchema("creationDate", new DataValue("16.07.2078", PropertyTypeEnum.STRING)));
        schemas4.put("modificationDate",new DataSchema("modificationDate", new DataValue("16.09.2078", PropertyTypeEnum.STRING)));
        schemas4.put("title",new DataSchema("title", new DataValue("Example Title", PropertyTypeEnum.STRING)));
        schemas4.put("description",new DataSchema("description", new DataValue("Example Description", PropertyTypeEnum.STRING)));
        schemas4.put("_links",new DataSchema("_links", new DataValue(new ArrayList<>(), PropertyTypeEnum.OBJECT)));
        schemas4.put("additional",new DataSchema("additional", new DataValue(new ArrayList<>(), PropertyTypeEnum.OBJECT)));

        DataValue dataValue4 = new DataValue(schemas4, PropertyTypeEnum.OBJECT);
        DataSchema dataSchema4 = new DataSchema("CatalogView", dataValue4);

        dataobjectsArtficatView = new DataSchema("noName", new DataValue(List.of(dataSchema1, dataSchema2, dataSchema3), PropertyTypeEnum.ARRAY));
        dataobjectsCatalogsView = new DataSchema("noName", new DataValue(List.of(dataSchema4), PropertyTypeEnum.ARRAY));
    }

    public DataSchema getSampleData(String tagName) {
        switch (tagName) {
            case "Artifacts":
                return dataobjectsArtficatView;
            case "Catalogs":
                return dataobjectsCatalogsView;
            default:
                System.out.println("WARNING: TRYING TO GET DATA FOR A NOT EXISTING TAG: " + tagName);
                return dataobjectsArtficatView;

        }
    }
}
