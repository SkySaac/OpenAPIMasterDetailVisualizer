package com.example.application.data.services;

import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucProperty;
import com.example.application.data.structureModel.StrucSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class SchemaService {

    public static String getPagedSchemaName(StrucSchema schema) { //TODO GENERALISIEREN
        Map<String, StrucProperty> properties = schema.getProperties();
        Map<String, StrucProperty> properties2 = properties.get("_embedded").getSchema().getProperties();
        Map.Entry<String, StrucProperty> entry = properties2.entrySet().iterator().next();
        if (entry.getValue().getRef() == null || entry.getValue().getType().equals(PropertyTypeEnum.SCHEMA)) {
            log.warn("ERROR GETTING PAGE FOR SCHEMA {}", schema.getName());
            return entry.getKey();
        }
        return entry.getValue().getRef();
    }

    public static boolean isPagedSchema(StrucSchema schema) { //TODO GENERALISIEREN
        if (schema == null)
            System.out.println("hi");
        Map<String, StrucProperty> properties = schema.getProperties();
        return properties != null && properties.containsKey("_embedded");
    }

    public static StrucSchema mapSchemaToStrucSchema(String name, Schema schema) {
        StrucSchema strucSchema = new StrucSchema();
        strucSchema.setName(name);
        Map<String, Schema> schemaProperties = schema.getProperties();
        if (schemaProperties != null) {
            for (Map.Entry<String, Schema> schemaProperty : schemaProperties.entrySet()) {
                StrucProperty strucProperty;
                if (schemaProperty.getValue().getType() != null)
                    //Property is no reference -> must be another type
                    if (Objects.equals(schemaProperty.getValue().getType(), "object")) {
                        strucProperty = new StrucProperty(PropertyTypeEnum.OBJECT);
                        strucProperty.setSchema(mapSchemaToStrucSchema(schemaProperty.getKey(), schemaProperty.getValue()));

                    } else if (Objects.equals(schemaProperty.getValue().getType(), "array")) {
                        strucProperty = new StrucProperty(PropertyTypeEnum.ARRAY);
                        if (schemaProperty.getValue().getItems().get$ref() != null) { //is Reference inside array
                            strucProperty.setRef(stripSchemaRefPath(schemaProperty.getValue().getItems().get$ref()));
                        } else { //is Schema inside array //TODO erst checked ob nicht StrucProperty sein muss (wenn zb direkt String)
                            strucProperty.setSchema(mapSchemaToStrucSchema(schemaProperty.getKey(), schemaProperty.getValue().getItems()));
                        }

                    } else {
                        //Die Property ist irgendeine art von normalem typ
                        strucProperty = new StrucProperty(PropertyTypeEnum.fromString(schemaProperty.getValue().getType()));
                    }
                else {
                    //Property is a reference
                    //TODO Die Property ist wohl eine referenz auf ein anderes Schema $ref, muss hier noch gebaut werden

                    strucProperty = new StrucProperty(PropertyTypeEnum.SCHEMA);
                }
                strucSchema.getProperties().put(schemaProperty.getKey(), strucProperty);
            }
        }
        return strucSchema;
    }

    public static Map<String, StrucSchema> mapSchemasToStrucSchemas(Map<String, Schema> schemaMap) {
        Map<String, StrucSchema> strucSchemaMap = new HashMap<>();
        for (Map.Entry<String, Schema> schemaEntry : schemaMap.entrySet()) {
            StrucSchema strucSchema = mapSchemaToStrucSchema(schemaEntry.getKey(), schemaEntry.getValue());
            strucSchemaMap.put(schemaEntry.getKey(), strucSchema);
            schemaMap.get(schemaEntry.getKey()).getAdditionalProperties(); //TODO die müssen noch eingebaut werden
        }
        return strucSchemaMap;
    }

    public static String stripSchemaRefPath(String schemaRef) {
        return schemaRef.substring(schemaRef.lastIndexOf('/') + 1);
    }

    public static Set<String> getNestedSchemaNames(Set<String> schemas, Schema schema) {
        if (schema != null) {
            if (schema.get$ref() != null)
                schemas.add(schema.get$ref());
            else if (schema.getProperties() != null) {
                Map<String, Schema> properties = schema.getProperties();
                properties.forEach((key, value) -> getNestedSchemaNames(schemas, value));
            } else if (schema.getType().equals( "array")) {
                //TODO for all items
            }
        }
        return schemas;
    }
}
