package com.example.application.rest.client;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private String SERVER_URL = "";
    private final ClientRequestService clientRequestService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientDataService(ClientRequestService clientRequestService) {
        this.clientRequestService = clientRequestService;
    }

    private ResponseEntity<String> sendRequest(HttpMethod httpMethod, String url, String path) {
        final var requestWrapper = new ClientRequestWrapper(httpMethod, url);

        requestWrapper.requestBuilder()
                .path(path);

        final var response = clientRequestService.request(requestWrapper);
        log.debug(response.getBody());
        return response;
    }

    public DataSchema getData(StrucPath strucPath, StrucSchema pageSchema) {

        DataSchema dataSchema;

        ResponseEntity<String> response = sendRequest(strucPath.getHttpMethod(), strucPath.getPath(), SERVER_URL);

        //TODO
        if (pageSchema == null) {
            //Data is not paged -> just json or array or string
            JsonNode node = objectMapper.valueToTree(response.getBody());
            dataSchema = convertToDataSchema("-",node);
        } else {
            JsonNode node = objectMapper.valueToTree(response.getBody());

            //TODO erst navigieren dann parsen
            dataSchema = convertToDataSchema("-",node);
        }

        return dataSchema;
    }

    private DataSchema convertToDataSchema(String name, JsonNode node) {
        DataValue dataValue;
        switch (node.getNodeType()) {
            case ARRAY -> {
                List<DataSchema> dataSchemas = new ArrayList<>();
                node.fieldNames().forEachRemaining(fieldName -> {
                    dataSchemas.add(convertToDataSchema(fieldName, node.get(fieldName)));
                });
                dataValue = new DataValue(dataSchemas, PropertyTypeEnum.ARRAY);
            }
            case NUMBER -> dataValue = new DataValue(String.valueOf(node.asDouble()), PropertyTypeEnum.NUMBER);
            case BOOLEAN -> dataValue = new DataValue(String.valueOf(node.asBoolean()), PropertyTypeEnum.BOOLEAN);
            case OBJECT -> {
                Map<String, DataSchema> dataSchemas = new HashMap<>();
                node.fieldNames().forEachRemaining(fieldName -> {
                    dataSchemas.put(fieldName, convertToDataSchema(fieldName, node.get(fieldName)));
                });
                dataValue = new DataValue(dataSchemas, PropertyTypeEnum.OBJECT);
            }
            default -> dataValue = new DataValue(node.asText(), PropertyTypeEnum.STRING);
        }
        return new DataSchema(name,dataValue);
    }
}
