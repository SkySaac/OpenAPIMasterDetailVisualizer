package openapivisualizer.application.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaadin.flow.spring.annotation.UIScope;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.rest.client.restdatamodel.DataValue;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucPath;
import openapivisualizer.application.ui.service.NotificationService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.*;

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

    @Setter
    private String username = null;
    @Setter
    private String password = null;

    public ClientDataService(ClientRequestService clientRequestService, NotificationService notificationService) {
        this.clientRequestService = clientRequestService;
        this.notificationService = notificationService;
    }

    public OpenAPI getOpenApi(String path) {
        if (username != null && password != null && path.startsWith("http")) {
            ResponseEntity<String> response = sendRequest(HttpMethod.GET, path, "", null, null, null);

            OpenAPI openAPI = new OpenAPIV3Parser().readContents(response.getBody()).getOpenAPI();
            return openAPI;
        } else {
            OpenAPI openAPI = new OpenAPIV3Parser().read(path);
            return openAPI;
        }

    }

    private ResponseEntity<String> sendRequest(HttpMethod httpMethod, String url, String path, Map<String, String> pathParams,
                                               MultiValueMap<String, String> queryParams, String body) {
        final var requestWrapper = new ClientRequestWrapper(httpMethod, url);

        requestWrapper.requestBuilder()
                .queryParams(queryParams)
                .pathParams(pathParams)
                //.accept(mediaType)
                .path(path);

        if (username != null && password != null)
            requestWrapper.requestBuilder().basicAuth(username, password);

        if (body != null) {
            requestWrapper.requestBuilder()
                    .body(body);
        }

        final var response = clientRequestService.request(requestWrapper);
        log.debug(response.getBody());
        return response;
    }

    public void postData(StrucPath strucPath, DataSchema bodyData, MultiValueMap<String, String> queryParams, Map<String, String> pathVariables) {
        String body = null;
        if (bodyData.getValue().getProperties().size() != 0)
            body = convertToJson(bodyData).toString();
        ResponseEntity<String> response = sendRequest(HttpMethod.POST, serverUrl, strucPath.getPath(), pathVariables, queryParams, body);
        log.info(response.getStatusCode().toString());

        if (response.getStatusCode().is2xxSuccessful())
            notificationService.postNotification("POST successful", false);
    }

    public void putData(StrucPath strucPath, DataSchema bodyData, MultiValueMap<String, String> queryParams, Map<String, String> pathVariables) {
        String body = null;
        if (bodyData.getValue().getProperties().size() != 0)
            body = convertToJson(bodyData).toString();
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

    public DataSchema getData(StrucPath strucPath, String wrappedPath, Map<String, String> pathParams, MultiValueMap<String, String> queryParameters) throws RequestException {

        ResponseEntity<String> response = sendRequest(HttpMethod.GET, serverUrl, strucPath.getPath(), pathParams, queryParameters, null);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode node = objectMapper.readTree(response.getBody());
                DataSchema dataSchema = convertToDataSchema("root", node);
                if (wrappedPath != null) {
                    //String key = dataSchema.getValue().get("_embedded").getValue().getProperties().keySet().iterator().next();
                    //dataSchema = dataSchema.getValue().get("_embedded").getValue().get(key);
                    DataSchema tempSchema = dataSchema;
                    for (String attributeName : wrappedPath.split("/")) {
                        if (tempSchema.getValue().getProperties().containsKey(attributeName)) {
                            tempSchema = tempSchema.getValue().getProperties().get(attributeName);
                        } else {
                            tempSchema = dataSchema;
                            break;
                        }
                    }
                    dataSchema = tempSchema;
                }
                return dataSchema;
            } catch (JsonProcessingException e) {
                throw new RequestException("Server response not equaling OpenAPI Specification");
            }
        } else if (response.getBody() == null) {
            throw new RequestException("Server response empty");
        } else {
            throw new RequestException(response.getStatusCode().toString());
        }
    }

    private ObjectNode convertToJson(DataSchema dataSchema) {
        ObjectNode node = objectMapper.createObjectNode();
        dataSchema.getValue().getProperties().forEach((key, value) -> {
            switch (value.getValue().getDataPropertyType()) {
                case INTEGER:
                    node.put(key, Integer.valueOf(value.getValue().getPlainValue()));
                case DOUBLE:
                    node.put(key, Double.valueOf(value.getValue().getPlainValue()));
                case BOOLEAN:
                    node.put(key, Boolean.valueOf(value.getValue().getPlainValue()));
                default:
                    node.put(key, value.getValue().getPlainValue());
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
                dataValue = new DataValue(dataSchemas, DataPropertyType.OBJECT);
            }
            case ARRAY -> {
                List<DataSchema> dataSchemas = new ArrayList<>();
                ArrayNode arrayNode = (ArrayNode) node;
                arrayNode.forEach(elementNode -> dataSchemas.add(convertToDataSchema("-", elementNode)));
                dataValue = new DataValue(dataSchemas, DataPropertyType.ARRAY);
            }
            case BOOLEAN -> dataValue = new DataValue(String.valueOf(node.asBoolean()), DataPropertyType.BOOLEAN);
            case NUMBER -> {
                //TODO node.canConvertToInt()? maybe useable instead of this check, werden so überhaupt doubles angezeigt wenn sie zufällig intable insd
                //check if value is double or int
                if ((node.asDouble() % 1) == 0)
                    dataValue = new DataValue(String.valueOf(node.asInt()), DataPropertyType.INTEGER);
                else
                    dataValue = new DataValue(String.valueOf(node.asDouble()), DataPropertyType.DOUBLE);

            }
            default -> dataValue = new DataValue(node.asText(), DataPropertyType.STRING);
        }
        return new DataSchema(name, dataValue);
    }
}
