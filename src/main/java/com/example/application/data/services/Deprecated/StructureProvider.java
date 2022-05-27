package com.example.application.data.services.Deprecated;

import com.example.application.data.structureModel.Info;
import com.example.application.data.structureModel.OpenApi;
import com.example.application.data.structureModel.finalStructure.Schema;
import com.example.application.data.structureModel.paths.Path;
import com.example.application.data.structureModel.paths.PathMethod;
import com.example.application.data.structureModel.tags.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.Iterator;

public class StructureProvider {

    /**
     * An attempt to parse everything directly into the openapi class
     * @param yaml
     * @return
     * @throws JsonProcessingException
     */
    @Deprecated
    public static OpenApi createStructure(String yaml) throws JsonProcessingException {
        String json = "";
        try {
            json = convertYamlToJson(yaml);
        } catch (JsonProcessingException e) {
            System.out.println("Error parsing yaml to json");
            //TODO
        }

        ObjectMapper objectMapper = new ObjectMapper();

        OpenApi openapi = objectMapper.readValue(json, OpenApi.class);

        return openapi;
    }

    /**
     * Function for manual parsing from the yaml into openapi objects
     * @param yaml
     * @return
     * @throws JsonProcessingException
     */
    @Deprecated
    public static OpenApi createTreeStructure(String yaml) throws JsonProcessingException {
        String json = "";
        try {
            //TODO what if it's already json ?
            json = convertYamlToJson(yaml);
        } catch (JsonProcessingException e) {
            System.out.println("Error parsing yaml to json");
            //TODO put good error message and feedback to frontend
        }

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(json);

        OpenApi openApi = new OpenApi();


        //tags
        //TODO: read tags from paths, that arent listed in the tags list
        JsonNode tagNodes = rootNode.get("tags");
        if (tagNodes.isArray()) {
            for (JsonNode tag : tagNodes) {
                System.out.println(tag);
                openApi.getTags().add(objectMapper.treeToValue(tag, Tag.class));
            }
        }


        //info
        JsonNode infoNode = rootNode.get("info");
        Info info = objectMapper.convertValue(infoNode, Info.class);
        openApi.setInfo(info);


        //paths
        JsonNode pathNodes = rootNode.get("paths");
        Iterator<String> pathIterator = pathNodes.fieldNames();
        pathIterator.forEachRemaining(
                fieldname -> openApi.getPaths().add(createPath(fieldname, pathNodes.get(fieldname)))
        );


        //schemas
        JsonNode componentNodes = rootNode.get("components").get("schemas");
        Iterator<String> iterator = componentNodes.fieldNames();
        iterator.forEachRemaining(
                fieldname -> openApi.getSchemas().add(createSchema(fieldname, componentNodes.get(fieldname)))
        );


        return openApi;
    }

    private static Path createPath(String fieldname, JsonNode jsonNode) {
        Path path = new Path();
        path.setPath(fieldname);
        if (jsonNode.has("get"))
            path.setGet(createPathMethod(jsonNode.get("get")));
        if (jsonNode.has("put"))
            path.setGet(createPathMethod(jsonNode.get("put")));
        if (jsonNode.has("post"))
            path.setGet(createPathMethod(jsonNode.get("post")));
        if (jsonNode.has("delete"))
            path.setGet(createPathMethod(jsonNode.get("delete")));
        return path;
    }

    private static PathMethod createPathMethod(JsonNode jsonNode) {
        PathMethod pathMethod = new PathMethod();
        pathMethod.setSummary(jsonNode.get("summary").textValue());
        pathMethod.setOperationId(jsonNode.get("operationId").textValue());
        //pathMethod.setRequestBody(); //TODO check if reuqestBody is Schema or defined here (createSchema())

        //Path & Query Parameters
        JsonNode parametersNode = jsonNode.get("parameters");
        if (parametersNode.isArray()) {
            for (JsonNode parameterNode : parametersNode) {
                switch (parameterNode.get("in").textValue()) {
                    case "query":
                        pathMethod.addQuerryParameter(
                                parameterNode.get("name").asText(),
                                parameterNode.get("required").asBoolean(),
                                parameterNode.get("schema").asText()); //TODO schema verarbeiten und eintragen (createSchema())
                        break;
                    case "path":
                        pathMethod.addPathParameter(
                                parameterNode.get("name").asText(),
                                parameterNode.get("schema").asText()); //TODO schema verarbeiten und eintragen (createSchema())
                        break;
                    default:
                        //TODO was für andere Parameter kann es geben ?
                        break;
                }
            }
        }
        return pathMethod;
    }


    private static Schema createSchema(String fieldname, JsonNode jsonNode) {
        return null; //TODO
    }


    private static String convertYamlToJson(String yaml) throws JsonProcessingException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(yaml, Object.class);

        ObjectMapper jsonWriter = new ObjectMapper();
        return jsonWriter.writeValueAsString(obj);
    }

}
