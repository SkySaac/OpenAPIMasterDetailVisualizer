package com.example.application.data.services;

import com.example.application.data.dataModel.DataValue;
import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.PropertyTypeEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SampleDataProviderService {
    private List<DataSchema> dataobjectsArtficatView = new ArrayList<>();
    private List<DataSchema> dataobjectsCatalogsView = new ArrayList<>();

    public SampleDataProviderService() {

        Map<String, DataValue> propertyMap = new HashMap<>();
        propertyMap.put("creationDate", new DataValue("16.07.2078", PropertyTypeEnum.STRING));
        propertyMap.put("modificationDate", new DataValue("16.09.2078", PropertyTypeEnum.STRING));
        propertyMap.put("remoteId", new DataValue("genesis", PropertyTypeEnum.STRING));
        propertyMap.put("title", new DataValue("Example Title", PropertyTypeEnum.STRING));
        propertyMap.put("description", new DataValue("Example Description", PropertyTypeEnum.STRING));
        propertyMap.put("numAccessed", new DataValue("5", PropertyTypeEnum.NUMBER));
        propertyMap.put("byteSize", new DataValue("23456789", PropertyTypeEnum.NUMBER));
        propertyMap.put("checkSum", new DataValue("0", PropertyTypeEnum.NUMBER));
        propertyMap.put("_links", new DataValue("linksTest", PropertyTypeEnum.OBJECT));
        propertyMap.put("additional", new DataValue("additionalTest", PropertyTypeEnum.OBJECT));

        DataSchema dataSchema = new DataSchema("ArtifactView", propertyMap);
        dataobjectsArtficatView.add(dataSchema);

        Map<String, DataValue> propertyMap2 = new HashMap<>();
        propertyMap2.put("creationDate", new DataValue("16.07.2878", PropertyTypeEnum.STRING));
        propertyMap2.put("modificationDate", new DataValue("13.09.2078", PropertyTypeEnum.STRING));
        propertyMap2.put("remoteId", new DataValue("genesis", PropertyTypeEnum.STRING));
        propertyMap2.put("title", new DataValue("Example Title 2", PropertyTypeEnum.STRING));
        propertyMap2.put("description", new DataValue("Example Description 2", PropertyTypeEnum.STRING));
        propertyMap2.put("numAccessed", new DataValue("56", PropertyTypeEnum.NUMBER));
        propertyMap2.put("byteSize", new DataValue("3", PropertyTypeEnum.NUMBER));
        propertyMap2.put("checkSum", new DataValue("6", PropertyTypeEnum.NUMBER));
        propertyMap2.put("_links", new DataValue("linksTest", PropertyTypeEnum.OBJECT));
        propertyMap2.put("additional", new DataValue("additionalTest", PropertyTypeEnum.OBJECT));

        DataSchema dataSchema2 = new DataSchema("ArtifactView", propertyMap2);
        dataobjectsArtficatView.add(dataSchema2);

        Map<String, DataValue> propertyMap3 = new HashMap<>();
        propertyMap3.put("creationDate", new DataValue("16.07.2898", PropertyTypeEnum.STRING));
        propertyMap3.put("modificationDate", new DataValue("05.10.2078", PropertyTypeEnum.STRING));
        propertyMap3.put("remoteId", new DataValue("genesis", PropertyTypeEnum.STRING));
        propertyMap3.put("title", new DataValue("Example Title 3", PropertyTypeEnum.STRING));
        propertyMap3.put("description", new DataValue("Example Description 3", PropertyTypeEnum.STRING));
        propertyMap2.put("numAccessed", new DataValue("56", PropertyTypeEnum.NUMBER));
        propertyMap3.put("byteSize", new DataValue("3675", PropertyTypeEnum.NUMBER));
        propertyMap3.put("checkSum", new DataValue("4567", PropertyTypeEnum.NUMBER));
        propertyMap3.put("_links", new DataValue("linksTest", PropertyTypeEnum.OBJECT));
        propertyMap3.put("additional", new DataValue("additionalTest", PropertyTypeEnum.OBJECT));

        DataSchema dataSchema3 = new DataSchema("ArtifactView", propertyMap3);
        dataobjectsArtficatView.add(dataSchema3);


        Map<String, DataValue> propertyMap4 = new HashMap<>();
        propertyMap4.put("creationDate", new DataValue("16.07.2078", PropertyTypeEnum.STRING));
        propertyMap4.put("modificationDate", new DataValue("16.09.2078", PropertyTypeEnum.STRING));
        propertyMap4.put("title", new DataValue("Example Title", PropertyTypeEnum.STRING));
        propertyMap4.put("description", new DataValue("Example Description", PropertyTypeEnum.STRING));
        propertyMap4.put("_links", new DataValue("linksTest", PropertyTypeEnum.OBJECT));
        propertyMap4.put("additional", new DataValue("additionalTest", PropertyTypeEnum.OBJECT));

        DataSchema dataSchema4 = new DataSchema("CatalogView", propertyMap4);
        dataobjectsCatalogsView.add(dataSchema4);
    }

    public List<DataSchema> getSampleData(String tagName) {
        switch (tagName) {
            case "Artifacts":
                return dataobjectsArtficatView;
            case "Catalogs":
                return dataobjectsCatalogsView;
            default:
                System.out.println("WARNING: TRYING TO GET DATA FOR A NOT EXISTING TAG: " +tagName);
                return dataobjectsArtficatView;

        }
    }
}
