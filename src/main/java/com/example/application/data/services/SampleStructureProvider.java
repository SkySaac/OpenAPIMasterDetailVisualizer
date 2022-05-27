package com.example.application.data.services;

import com.example.application.data.structureModel.finalStructure.Component;
import com.example.application.data.structureModel.finalStructure.Property;
import com.example.application.data.structureModel.finalStructure.PropertyTypeEnum;
import com.example.application.data.structureModel.finalStructure.Schema;
import com.example.application.data.structureModel.tags.Tag;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Service
public class SampleStructureProvider {

    public SampleStructureProvider(){}

    public Component getSampleComponent() {
        Tag tag = new Tag("ArtifactTag", "ArtifactTagDescription");

        Map<String, Property> properties = new HashMap<>();
        properties.put("creationDate", new Property(PropertyTypeEnum.STRING));
        properties.put("modificationDate", new Property(PropertyTypeEnum.STRING));
        properties.put("remoteId", new Property(PropertyTypeEnum.STRING));
        properties.put("title", new Property(PropertyTypeEnum.STRING));
        properties.put("description", new Property(PropertyTypeEnum.STRING));
        properties.put("numAccessed", new Property(PropertyTypeEnum.NUMBER));
        properties.put("byteSize", new Property(PropertyTypeEnum.NUMBER));
        properties.put("checkSum", new Property(PropertyTypeEnum.NUMBER));
        Schema schema = new Schema("ArtifactView", properties);
        Component component = new Component(tag, schema); //TODO put real path in

        return component;
    }

    public static String getSampleStructure() throws IOException {
        return StreamUtils.copyToString( new ClassPathResource("testOpenApi.yaml").getInputStream(), Charset.defaultCharset()  );
    }
}
