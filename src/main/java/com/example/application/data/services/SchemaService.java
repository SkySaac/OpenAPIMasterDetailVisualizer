package com.example.application.data.services;

import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.data.structureModel.StrucValue;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class SchemaService {

    public static String getPagedSchemaName(StrucSchema schema) { //TODO GENERALISIEREN
        Map<String, StrucSchema> properties = schema.getStrucValue().getProperties().get("_embedded").getStrucValue().getProperties();
        Map.Entry<String, StrucSchema> entry = properties.entrySet().iterator().next();
        if (entry.getValue().getStrucValue().getRef() == null || entry.getValue().getStrucValue().getType().equals(PropertyTypeEnum.SCHEMA)) {
            log.warn("ERROR GETTING PAGE FOR SCHEMA {}", schema.getName());
            return entry.getKey();
        }
        return entry.getValue().getStrucValue().getRef();
    }

    public static boolean isPagedSchema(StrucSchema schema) { //TODO GENERALISIEREN
        if (schema == null)
            System.out.println("hi");
        Map<String, StrucSchema> properties = schema.getStrucValue().getProperties();
        return properties != null && properties.containsKey("_embedded");
    }

    public static StrucSchema mapSchemaToStrucSchema(String name, Schema schema) {
        StrucSchema strucSchema = new StrucSchema();
        strucSchema.setName(name);
        StrucValue strucValue;
        if (schema.getType() != null) {
            //Schema is not a reference
            if (Objects.equals(schema.getType(), "object")) {
                strucValue = new StrucValue(PropertyTypeEnum.OBJECT);
                if (schema.getProperties() != null) {
                    Map<String, Schema> properties = schema.getProperties();
                    if (properties == null) {
                        log.info("debug me"); //TODO
                    }
                    properties.forEach((propertyName, property) ->
                            strucValue.getProperties().put(propertyName, mapSchemaToStrucSchema(propertyName, property))
                    );

                }
                if (schema.getAdditionalProperties() != null) {
                    if (schema.getAdditionalProperties().getClass().getSimpleName().equals("Boolean")) {
                        Schema additionalSchema = (Schema) schema.getAdditionalProperties();
                        strucValue.setAdditionalPropertySchema(mapSchemaToStrucSchema("additionalProperties", additionalSchema));
                    } else if (schema.getAdditionalProperties().getClass().getSimpleName().contains("Schema")) {
                        Schema additionalSchema = (Schema) schema.getAdditionalProperties();
                        strucValue.setAdditionalPropertySchema(mapSchemaToStrucSchema("additionalProperties", additionalSchema));
                    } else {
                        String nameS = schema.getAdditionalProperties().getClass().getSimpleName();
                        log.warn("Additional Properties is not boolean nor schema");
                    }
                }
            } else if (Objects.equals(schema.getType(), "array")) {
                strucValue = new StrucValue(PropertyTypeEnum.ARRAY);
                if (schema.getItems().get$ref() != null) {
                    //Reference is inside array
                    strucValue.setRef(stripSchemaRefPath(schema.getItems().get$ref())); //TODO so ok ? oder nur wie unten ?
                    StrucSchema oneOfStrucSchema = mapSchemaToStrucSchema("oneOf", schema.getItems());
                    strucValue.getArrayElements().add(oneOfStrucSchema);
                } else if (schema.getItems().getOneOf() != null
                        && !schema.getItems().getOneOf().isEmpty()) {
                    //is OneOf multiple schemas or refs
                    List<Schema> oneOfSchemas = schema.getItems().getOneOf();
                    oneOfSchemas.forEach(oneOfSchema -> {
                        StrucSchema oneOfStrucSchema = mapSchemaToStrucSchema("oneOf", oneOfSchema);
                        strucValue.getArrayElements().add(oneOfStrucSchema);
                    });
                } else {
                    //simple item inside array or object
                    strucValue.getArrayElements().add(mapSchemaToStrucSchema("oneOf", schema.getItems()));
                }
            } else {
                //schema is string
                strucValue = new StrucValue(PropertyTypeEnum.fromString(schema.getType()));
            }

        } else {
            //Schema is ref
            if (schema.get$ref() != null) {
                strucValue = new StrucValue(PropertyTypeEnum.SCHEMA);
                strucValue.setRef(stripSchemaRefPath(schema.get$ref()));
            } else {
                log.warn("Schema {} has no type and is no reference", schema.getName());
                strucValue = new StrucValue(PropertyTypeEnum.STRING);
            }
        }
        strucSchema.setStrucValue(strucValue);
        return strucSchema;
    }


    public static Map<String, StrucSchema> mapSchemasToStrucSchemas(Map<String, Schema> schemaMap) {
        Map<String, StrucSchema> strucSchemaMap = new HashMap<>();

        //Map all schemas to StrucSchemas
        for (Map.Entry<String, Schema> schemaEntry : schemaMap.entrySet()) {
            StrucSchema strucSchema = mapSchemaToStrucSchema(schemaEntry.getKey(), schemaEntry.getValue());
            strucSchemaMap.put(schemaEntry.getKey(), strucSchema);
        }

        //Resolve the internal refs to other strucSchemas
        for (Map.Entry<String, StrucSchema> schemaEntry : strucSchemaMap.entrySet()) {
            replaceInternalRefs(schemaEntry.getValue(), strucSchemaMap);
        }
        return strucSchemaMap;
    }

    public static void replaceInternalRefs(StrucSchema schema, Map<String, StrucSchema> schemaMap) {
        if (schema.getStrucValue().getType().equals(PropertyTypeEnum.OBJECT)) {
            //If any of the properties is a ref, replace it with the actual schema, otherwise repeat this function for it
            List<Map.Entry<String, StrucSchema>> properties = schema.getStrucValue().getProperties().entrySet().stream().toList();
            for (int i = 0; i < properties.size(); i++) {

                if (!properties.get(i).getValue().getStrucValue().getType().equals(PropertyTypeEnum.SCHEMA))
                    replaceInternalRefs(properties.get(i).getValue(), schemaMap);
                else
                    schema.getStrucValue().getProperties().put(
                            properties.get(i).getKey(),
                            schemaMap.get(properties.get(i).getValue().getStrucValue().getRef()));
            }
            //Check if the schema of the additional properties has a ref
            if(schema.getStrucValue().getAdditionalPropertySchema()!=null){
                //If the additional Property Schema is a ref -> replace it else
                if(!schema.getStrucValue().getAdditionalPropertySchema().getStrucValue().getType().equals(PropertyTypeEnum.SCHEMA)){
                    replaceInternalRefs(schema.getStrucValue().getAdditionalPropertySchema(), schemaMap);
                }else{
                    schema.getStrucValue().setAdditionalPropertySchema(
                            schemaMap.get(schema.getStrucValue().getAdditionalPropertySchema().getStrucValue().getRef()));
                }

            }
        } else if (schema.getStrucValue().getType().equals(PropertyTypeEnum.ARRAY)) {
            if(schema.getStrucValue().getRef()!=null){
                log.warn("Ref inside array, when it should be inside schema");
            }

            //If any of the array elements is a ref, replace it with the actual schema, otherwise repeat this function for it
            for (int i = 0; i < schema.getStrucValue().getArrayElements().size(); i++) {
                if (!schema.getStrucValue().getArrayElements().get(i).getStrucValue().getType().equals(PropertyTypeEnum.SCHEMA))
                    replaceInternalRefs(schema.getStrucValue().getArrayElements().get(i), schemaMap);
                else //is nested Schema
                    schema.getStrucValue().getArrayElements().set(
                            i,
                            schemaMap.get(schema.getStrucValue().getArrayElements().get(i).getStrucValue().getRef()));
            }
        } else {
            //nothing
            if (schema.getStrucValue().getType().equals(PropertyTypeEnum.SCHEMA))
                log.warn("Unreplaced internal schema found: {}", schema.getName());
        }
    }

    public static String stripSchemaRefPath(String schemaRef) {
        return schemaRef.substring(schemaRef.lastIndexOf('/') + 1);
    }

    @Deprecated
    public static Set<String> getNestedSchemaNames(Set<String> schemas, Schema schema) {
        if (schema != null) {
            if (schema.get$ref() != null)
                schemas.add(schema.get$ref());
            else if (schema.getProperties() != null) {
                Map<String, Schema> properties = schema.getProperties();
                properties.forEach((key, value) -> getNestedSchemaNames(schemas, value));
            } else if (schema.getType().equals("array")) {
                //TODO for all items
            }
        }
        return schemas;
    }
}
