package com.example.application.rest.client;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.controller.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@UIScope
public class ClientDataService {
    private final ClientRequestService clientRequestService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Setter
    @Getter
    private String serverUrl = "";

    public ClientDataService(ClientRequestService clientRequestService, NotificationService notificationService) {
        this.clientRequestService = clientRequestService;
        this.notificationService = notificationService;
    }

    private ResponseEntity<String> sendRequest(HttpMethod httpMethod, String url, String path, Map<String, String> pathParams,
                                               MultiValueMap<String, String> queryParams, String body) {
        final var requestWrapper = new ClientRequestWrapper(httpMethod, url);

        requestWrapper.requestBuilder()
                .queryParams(queryParams)
                .pathParams(pathParams)
                .path(path);

        if (body != null) {
            requestWrapper.requestBuilder()
                    .body(body);
        }

        final var response = clientRequestService.request(requestWrapper);
        log.debug(response.getBody());
        return response;
    }

    public void postData(StrucPath strucPath, DataSchema bodyData, MultiValueMap<String, String> queryParams, Map<String, String> pathVariables) {
        String body = convertToJson(bodyData).toString();
        ResponseEntity<String> response = sendRequest(HttpMethod.POST, serverUrl, strucPath.getPath(), pathVariables, queryParams, body);
        log.info(response.getStatusCode().toString());

        if (response.getStatusCode().is2xxSuccessful())
            notificationService.postNotification("POST successful", false);
    }

    public void putData(StrucPath strucPath, DataSchema bodyData, MultiValueMap<String, String> queryParams, Map<String, String> pathVariables) {
        String body = convertToJson(bodyData).toString();
        ResponseEntity<String> response = sendRequest(HttpMethod.PUT, serverUrl, strucPath.getPath(), pathVariables, queryParams, body);
        log.info(response.getStatusCode().toString());

        if (response.getStatusCode().is2xxSuccessful())
            notificationService.postNotification("PUT successful", false);
    }

    public void deleteData(String path, Map<String, String> pathVariables, MultiValueMap<String, String> queryParams) {
        log.info("Sending DELETE request to: {}", serverUrl + path);

        ResponseEntity<String> response = sendRequest(HttpMethod.DELETE, serverUrl, path, pathVariables, queryParams, null);

        if (response.getStatusCode().is2xxSuccessful())
            notificationService.postNotification("DELETE successful", false);

    }

    public DataSchema getData(StrucPath strucPath, StrucSchema innerSchema, Map<String, String> pathParams, MultiValueMap<String, String> queryParameters) {

        DataSchema dataSchema = null;

        //TODO http bzw https -> url aus openapi doc ändern
        ResponseEntity<String> response = sendRequest(HttpMethod.GET, serverUrl, strucPath.getPath(), pathParams, queryParameters, null);
        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            dataSchema = convertToDataSchema("root", node);
            if (innerSchema != null) {
                String key = dataSchema.getValue().get("_embedded").getValue().getProperties().keySet().iterator().next();
                dataSchema = dataSchema.getValue().get("_embedded").getValue().get(key);
            }
        } catch (JsonProcessingException je) {
            notificationService.postNotification("Malformed body", true);
            return dataSchema;
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            notificationService.postNotification("GET successful", false);
        }

        return dataSchema;
    }

    private ObjectNode convertToJson(DataSchema dataSchema) {
        //TODO make dynamic, get nested objects
        ObjectNode node = objectMapper.createObjectNode();
        dataSchema.getValue().getProperties().forEach((key, value) -> {
            switch (value.getValue().getPropertyTypeEnum()) {
                case INTEGER -> node.put(key, Integer.valueOf(value.getValue().getPlainValue()));
                case DOUBLE -> node.put(key, Double.valueOf(value.getValue().getPlainValue()));
                case BOOLEAN -> node.put(key, Boolean.valueOf(value.getValue().getPlainValue()));
                default -> node.put(key, value.getValue().getPlainValue());
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
            }
            case ARRAY -> {
                List<DataSchema> dataSchemas = new ArrayList<>();
                ArrayNode arrayNode = (ArrayNode) node;
                arrayNode.forEach(elementNode -> dataSchemas.add(convertToDataSchema("-", elementNode)));
                dataValue = new DataValue(dataSchemas, PropertyTypeEnum.ARRAY);
            }
            case BOOLEAN -> dataValue = new DataValue(String.valueOf(node.asBoolean()), PropertyTypeEnum.BOOLEAN);
            case NUMBER -> {
                //TODO node.canConvertToInt()? maybe useable instead of this check, werden so überhaupt doubles angezeigt wenn sie zufällig intable insd
                //check if value is double or int
                if ((node.asDouble() % 1) == 0)
                    dataValue = new DataValue(String.valueOf(node.asInt()), PropertyTypeEnum.INTEGER);
                else
                    dataValue = new DataValue(String.valueOf(node.asDouble()), PropertyTypeEnum.DOUBLE);

            }
            default -> dataValue = new DataValue(node.asText(), PropertyTypeEnum.STRING);
        }
        return new DataSchema(name, dataValue);
    }
}
