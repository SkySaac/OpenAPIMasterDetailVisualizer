package com.example.application.data.services;

import com.example.application.data.structureModel.*;
import com.example.application.rest.client.ClientDataService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StructureProviderService {
    private final ClientDataService clientDataService;
    public static final String PARSE_OBJECT = "testOpenApi.yaml";

    @Getter
    public List<StrucViewGroup> strucViewGroups = new ArrayList<>();

    public StructureProviderService(ClientDataService clientDataService1){
        this.clientDataService = clientDataService1;
    }


    public List<StrucViewGroup> generateApiStructure(String pathToOpenApiFile) {
        OpenAPI openAPI = new OpenAPIV3Parser().read(pathToOpenApiFile);

        if(openAPI.getServers()!=null) //TODO change -> server url can also come from lower objects
            clientDataService.setSERVER_URL(openAPI.getServers().get(0).getUrl());

        List<StrucViewGroup> strucViewGroupList = new ArrayList<>();

        //alle components in dataschemas übersetzen und in liste tun
        Map<String, StrucSchema> strucSchemaMap = mapSchemasToStrucSchemas(openAPI.getComponents().getSchemas());

        //TODO was wenn kein Tag vorhanden

        Set<String> tagNames = collectTags(openAPI);
        log.info("Collected {} tags: {}", tagNames.size(), tagNames);

        tagNames.forEach(tag -> {
            log.info("Now looking for tag: " + tag);

            //sucht alle paths die zu diesem tag gehören und wandelt sie in StrucPath Objekte um
            Map<String, Map<HttpMethod, StrucPath>> pathsForTag = getPathsForTag(tag, openAPI.getPaths());
            log.info("A total of {} paths have been found for the tag {}", pathsForTag.size(), tag);

            //alle components die zu path gehören aus strucSchemaMap holen und in strucViewGroup eintragen
            Map<String, StrucSchema> strucViewGroupSchemaMap = createStrucViewGroupSchemaMap(strucSchemaMap, pathsForTag);
            log.debug("A total of {} schemas have been found for the tag {}", pathsForTag.size(), tag);

            //Find primarypaths for this viewgroup
            List<StrucPath> primaryPaths = getPrimaryViewPaths(pathsForTag, tag);
            log.debug("Primary Paths for tag " + tag + " is: " + primaryPaths);

            StrucViewGroup strucViewGroup = new StrucViewGroup(tag, primaryPaths, strucViewGroupSchemaMap, pathsForTag);

            strucViewGroupList.add(strucViewGroup);
        });

        strucViewGroups = strucViewGroupList;

        return strucViewGroupList;
    }

    private Set<String> collectTags(OpenAPI openAPI) {
        Set<String> tags = new HashSet<>();
        if (openAPI.getTags() != null) {
            tags.addAll(openAPI.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toList()));
        }

        if (openAPI.getPaths() != null) {
            openAPI.getPaths().entrySet().forEach(pathEntry -> tags.addAll(collectTags(pathEntry.getValue())));
        }
        return tags;
    }

    private Set<String> collectTags(PathItem pathItem) {
        Set<String> tags = new HashSet<>();
        if (pathItem.getGet() != null && pathItem.getGet().getTags() != null)
            tags.addAll(pathItem.getGet().getTags());

        if (pathItem.getPost() != null && pathItem.getPost().getTags() != null)
            tags.addAll(pathItem.getPost().getTags());

        if (pathItem.getPut() != null && pathItem.getPut().getTags() != null)
            tags.addAll(pathItem.getPut().getTags());

        if (pathItem.getDelete() != null && pathItem.getDelete().getTags() != null)
            tags.addAll(pathItem.getDelete().getTags());

        return tags;
    }

    private Map<String, StrucSchema> createStrucViewGroupSchemaMap(Map<String, StrucSchema> strucSchemaMap, Map<String, Map<HttpMethod, StrucPath>> pathsForTag) {
        Map<String, StrucSchema> strucViewGroupSchemaMap = new HashMap<>();
        pathsForTag.entrySet().forEach(path -> {
            path.getValue().entrySet().forEach(httpPath -> {
                //Check Request Body Schema
                if (httpPath.getValue().getExternalRequestBodySchemaName() != null) {
                    strucViewGroupSchemaMap.put(httpPath.getValue().getExternalRequestBodySchemaName(), strucSchemaMap.get(httpPath.getValue().getExternalRequestBodySchemaName()));
                }
                //Check Response Body Schema
                if (httpPath.getValue().getExternalResponseBodySchemaName() != null) {
                    strucViewGroupSchemaMap.put(httpPath.getValue().getExternalResponseBodySchemaName(), strucSchemaMap.get(httpPath.getValue().getExternalResponseBodySchemaName()));
                    //If Response is a PagedObject -> Add whats behind the paged Object //TODO replace with add all nested objects
                    if (isPagedSchema(strucSchemaMap.get(httpPath.getValue().getExternalResponseBodySchemaName()))) {
                        String pagedSchemaName = getPagedSchemaName(strucSchemaMap.get(httpPath.getValue().getExternalResponseBodySchemaName()));
                        strucViewGroupSchemaMap.put(pagedSchemaName, strucSchemaMap.get(pagedSchemaName));
                    }
                }

                //TODO check internal (nested) Schemas

            });
        });
        return strucViewGroupSchemaMap;
    }

    public String getPagedSchemaName(StrucSchema schema) { //TODO GENERALISIEREN
        Map<String, StrucProperty> properties = schema.getProperties();
        Map<String, StrucProperty> properties2 = properties.get("_embedded").getSchema().getProperties();
        Map.Entry<String, StrucProperty> entry = properties2.entrySet().iterator().next();
        if (entry.getValue().getRef() == null || entry.getValue().getType().equals(PropertyTypeEnum.SCHEMA)) {
            log.warn("ERROR GETTING PAGE FOR SCHEMA {}", schema.getName());
            return entry.getKey();
        }
        return entry.getValue().getRef();

    }

    public boolean isPagedSchema(StrucSchema schema) { //TODO GENERALISIEREN
        if (schema == null)
            System.out.println("hi");
        Map<String, StrucProperty> properties = schema.getProperties();
        return properties != null && properties.containsKey("_embedded");
    }

    public Set<String> getNestedSchemaNames(Set<String> schemas, Schema schema) {
        if (schema != null) {
            if (schema.get$ref() != null)
                schemas.add(schema.get$ref());
            else if (schema.getProperties() != null) {
                Map<String, Schema> properties = schema.getProperties();
                properties.entrySet().forEach(propertyEntry -> getNestedSchemaNames(schemas, propertyEntry.getValue()));
            } else if (schema.getType() == "array") {
                //TODO for all items
            }
        }
        return schemas;
    }

    private List<StrucPath> getPrimaryViewPaths(Map<String, Map<HttpMethod, StrucPath>> pathsForTag, String tagName) {
        List<StrucPath> primaryPaths = new ArrayList<>();

        pathsForTag.entrySet().forEach(pathEntry -> {
            //wenn kein {} drinne kanns n primary path sein
            if (!pathEntry.getKey().contains("{") && pathEntry.getValue().containsKey(HttpMethod.GET)) {
                if (pathEntry.getValue().get(HttpMethod.GET).getExternalResponseBodySchemaName() != null) {
                    primaryPaths.add(pathEntry.getValue().get(HttpMethod.GET));
                    log.info("Detected primary path: " + pathEntry.getValue().get(HttpMethod.GET).getPath() + " for tag: " + tagName);
                }
            }
        });
        //Master Detail View (BSP Artifacts)
        return primaryPaths;
    }

    private StrucSchema mapSchemaToStrucSchema(String name, Schema schema) {
        StrucSchema strucSchema = new StrucSchema();
        strucSchema.setName(name);
        Map<String, Schema> schemaProperties = schema.getProperties();
        if (schemaProperties != null) {
            for (Map.Entry<String, Schema> schemaProperty : schemaProperties.entrySet()) {
                StrucProperty strucProperty;
                if (schemaProperty.getValue().getType() != null)
                    //Property is no reference -> must be another type
                    if (schemaProperty.getValue().getType() == "object") {
                        strucProperty = new StrucProperty(PropertyTypeEnum.OBJECT);
                        strucProperty.setSchema(mapSchemaToStrucSchema(schemaProperty.getKey(), schemaProperty.getValue()));

                    } else if (schemaProperty.getValue().getType() == "array") {
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

    private Map<String, StrucSchema> mapSchemasToStrucSchemas(Map<String, Schema> schemaMap) {
        Map<String, StrucSchema> strucSchemaMap = new HashMap<>();
        for (Map.Entry<String, Schema> schemaEntry : schemaMap.entrySet()) {
            StrucSchema strucSchema = mapSchemaToStrucSchema(schemaEntry.getKey(), schemaEntry.getValue());
            strucSchemaMap.put(schemaEntry.getKey(), strucSchema);
            schemaMap.get(schemaEntry.getKey()).getAdditionalProperties(); //TODO die müssen noch eingebaut werden
        }
        return strucSchemaMap;
    }

    private String stripSchemaRefPath(String schemaRef) {
        return schemaRef.substring(schemaRef.lastIndexOf('/') + 1);
    }

    private StrucPath operationToStrucPath(String path, HttpMethod httpMethod, Operation operation) {
        StrucPath strucPath = new StrucPath();
        strucPath.setPath(path);
        strucPath.setHttpMethod(httpMethod);
        log.info("Converting Operation to Path for Path {} and HttpMethod {}", path, httpMethod.toString());
        if (HttpMethod.POST.equals(httpMethod) || HttpMethod.PUT.equals(httpMethod)) { //TODO was in get oder delete ?, was tun wenn kein ref schema
            if (operation.getRequestBody() != null) { //example: /api/apps/{id}/actions and HttpMethod PUT-
                if (operation.getRequestBody().getContent().containsKey("application/json") && operation.getRequestBody().getContent().get("application/json").getSchema() != null) { //Has actual Schema (Example /artifacts/id/data)
                    //TODO application/octet-stream (data PUT)
                    String externalSchemaPath = operation.getRequestBody().getContent().get("application/json").getSchema().get$ref(); //TODO was wenn kein href & für andere arten von content
                    if (externalSchemaPath != null) {
                        strucPath.setExternalRequestBodySchemaName(stripSchemaRefPath(externalSchemaPath));
                        strucPath.setExternalRequestSchema(true);
                    } else {
                        StrucSchema strucSchema = mapSchemaToStrucSchema("noName", operation.getRequestBody().getContent().get("application/json").getSchema());
                        strucPath.setRequestStrucSchema(strucSchema);
                    }

                } else {
                    StrucSchema strucSchema = new StrucSchema();
                    strucSchema.setFreeSchema(true);
                }
            }
        }
        if (HttpMethod.GET.equals(httpMethod)) { //TODO was wenn mehrere rückgaben möglich
            if (operation.getResponses().containsKey("200") && operation.getResponses().get("200").getContent()!=null) {
                if (operation.getResponses().get("200").getContent().containsKey("*/*"))
                    setResponseSchema(strucPath,operation,"200","*/*");
                else if (operation.getResponses().get("200").getContent().containsKey("application/json"))
                    setResponseSchema(strucPath,operation,"200","application/json");
            }
        } else {
            log.info("The current path can only respond with the following http codes: {}", operation.getResponses().keySet());
        }
        return strucPath;
    }

    /**
     * This function finds the schema of the content in the response and puts it into the StrucPath object
     * @param strucPath the Strucpath to set the ResponseBodySchema in
     * @param operation the Operation that holds the inital Schema
     * @param returnCode the http returnCode to be searched
     * @param returnType the returnType to be searched
     */
    private void setResponseSchema(StrucPath strucPath,Operation operation, String returnCode, String returnType){
        if (operation.getResponses().get(returnCode).getContent().containsKey(returnType)) {
            if (operation.getResponses().get(returnCode).getContent().get(returnType).getSchema().get$ref() != null) {
                String externalSchemaPath = operation.getResponses().get(returnCode).getContent().get(returnType).getSchema().get$ref();
                strucPath.setExternalResponseBodySchemaName(stripSchemaRefPath(externalSchemaPath));
                strucPath.setExternalResponseSchema(true);
            } else {
                StrucSchema strucSchema = mapSchemaToStrucSchema("noName", operation.getResponses().get(returnCode).getContent().get(returnType).getSchema());
                strucPath.setResponseStrucSchema(strucSchema);
            }
        }
    }

    private Map<String, Map<HttpMethod, StrucPath>> getPathsForTag(String tagName, Paths paths) {
        Map<String, Map<HttpMethod, StrucPath>> pathOperationMap = new HashMap<>(); //Path -> List
        paths.keySet().forEach(key -> { //TODO all paths without recognised tags into one sonstiges tag
            Map<HttpMethod, StrucPath> methodOperationMap = new HashMap<>();
            if (paths.get(key).getGet() != null
                    && paths.get(key).getGet().getTags() != null
                    && paths.get(key).getGet().getTags().contains(tagName)) {
                methodOperationMap.put(HttpMethod.GET, operationToStrucPath(key, HttpMethod.GET, paths.get(key).getGet()));
            }
            if (paths.get(key).getPost() != null
                    && paths.get(key).getPost().getTags() != null
                    && paths.get(key).getPost().getTags().contains(tagName)) {
                methodOperationMap.put(HttpMethod.POST, operationToStrucPath(key, HttpMethod.POST, paths.get(key).getPost()));
            }
            if (paths.get(key).getPut() != null
                    && paths.get(key).getPut().getTags() != null
                    && paths.get(key).getPut().getTags().contains(tagName)) {
                methodOperationMap.put(HttpMethod.PUT, operationToStrucPath(key, HttpMethod.PUT, paths.get(key).getPut()));
            }
            if (paths.get(key).getDelete() != null
                    && paths.get(key).getDelete().getTags() != null
                    && paths.get(key).getDelete().getTags().contains(tagName)) {
                methodOperationMap.put(HttpMethod.DELETE, operationToStrucPath(key, HttpMethod.DELETE, paths.get(key).getDelete()));
            }
            if (methodOperationMap.size() > 0)
                pathOperationMap.put(key, methodOperationMap);
        });
        return pathOperationMap;
    }

}
