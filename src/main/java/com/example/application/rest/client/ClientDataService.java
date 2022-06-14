package com.example.application.rest.client;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ClientDataService {
    @Setter
    private String serverUrl = "";
    private final ClientRequestService clientRequestService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientDataService(ClientRequestService clientRequestService) {
        this.clientRequestService = clientRequestService;
    }

    private ResponseEntity<String> sendRequest(HttpMethod httpMethod, String url, String path, String body) {
        final var requestWrapper = new ClientRequestWrapper(httpMethod, url);

        requestWrapper.requestBuilder()
                .path(path);

        if (body != null) {
            requestWrapper.requestBuilder()
                    .body(body);
        }

        final var response = clientRequestService.request(requestWrapper);
        log.debug(response.getBody());
        return response;
    }

    public void postData(StrucPath strucPath, DataSchema bodyData) {
        String body = convertToJson(bodyData).toString();
        ResponseEntity<String> response = sendRequest(HttpMethod.POST, serverUrl, strucPath.getPath(), body);
        log.info(response.getStatusCode().toString());

        //TODO what if 400
    }

    public void deleteData(StrucPath strucPath, String parameterName, String parameterValue) {

        String finalPath = strucPath.getPath().replace("{parameterName}",parameterValue);

        ResponseEntity<String> response = sendRequest(HttpMethod.DELETE, serverUrl, finalPath, null);
    }

    public DataSchema getData(StrucPath strucPath, StrucSchema pageSchema) {

        DataSchema dataSchema = null;

        //TODO http bzw https -> url aus openapi doc ändern
        ResponseEntity<String> response = sendRequest(HttpMethod.GET, serverUrl, strucPath.getPath(), null);
        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            dataSchema = convertToDataSchema("-", node);
            //TODO
            if (pageSchema == null) {
                //Data is not paged -> just json or array or string
            } else { //TODO ABSTRAHIEREN
                String key = dataSchema.getValue().get("_embedded").getValue().getProperties().keySet().iterator().next();
                dataSchema = dataSchema.getValue().get("_embedded").getValue().get(key);
            }
        } catch (JsonProcessingException je) {
            //TODO: error fetching data for this
        }

        return dataSchema;
    }

    private ObjectNode convertToJson(DataSchema dataSchema) {
        //TODO make dynamic, get nested objects
        ObjectNode node = objectMapper.createObjectNode();
        dataSchema.getValue().getProperties().entrySet().forEach(entrySet -> {
            switch (entrySet.getValue().getValue().getPropertyTypeEnum()) {
                case NUMBER -> node.put(entrySet.getKey(), Double.valueOf(entrySet.getValue().getValue().getPlainValue()));
                case BOOLEAN -> node.put(entrySet.getKey(), Boolean.valueOf(entrySet.getValue().getValue().getPlainValue()));
                default -> node.put(entrySet.getKey(), entrySet.getValue().getValue().getPlainValue());
            }
        });
        return node;
    }

    private DataSchema convertToDataSchema(String name, JsonNode node) {
        DataValue dataValue;

        switch (node.getNodeType()) {
            case OBJECT -> { // object
                Map<String, DataSchema> dataSchemas = new HashMap<>();
                ObjectNode objectNode = (ObjectNode) node;
                objectNode.fieldNames().forEachRemaining(fieldName -> dataSchemas.put(fieldName, convertToDataSchema(fieldName, objectNode.get(fieldName))));
                dataValue = new DataValue(dataSchemas, PropertyTypeEnum.OBJECT);
                break;
            }
            case ARRAY -> {
                List<DataSchema> dataSchemas = new ArrayList<>();
                ArrayNode arrayNode = (ArrayNode) node;
                arrayNode.forEach(elementNode -> dataSchemas.add(convertToDataSchema("-", elementNode)));
                dataValue = new DataValue(dataSchemas, PropertyTypeEnum.ARRAY);
                break;
            }
            case BOOLEAN -> {
                dataValue = new DataValue(String.valueOf(node.asBoolean()), PropertyTypeEnum.BOOLEAN);
                break;
            }
            case NUMBER -> {
                dataValue = new DataValue(String.valueOf(node.asDouble()), PropertyTypeEnum.NUMBER);
                break;
            }
            default -> {
                dataValue = new DataValue(node.asText(), PropertyTypeEnum.STRING);
                break;
            }
        }
        return new DataSchema(name, dataValue);
    }
}
