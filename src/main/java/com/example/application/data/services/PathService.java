package com.example.application.data.services;

import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PathService {

    public static List<String> getPrimaryViewPaths(Map<String, Map<HttpMethod, StrucPath>> pathsForTag) {
        List<String> primaryPaths = new ArrayList<>();

        pathsForTag.forEach((key, value) -> {
            //wenn kein {} drinne kanns n primary path sein
            if (!key.contains("{") && value.containsKey(HttpMethod.GET)) {
                if (value.get(HttpMethod.GET).getResponseStrucSchema() != null) { //TODO geht null überhaupt ?
                    primaryPaths.add(value.get(HttpMethod.GET).getPath());
                    log.info("Detected primary path: " + value.get(HttpMethod.GET).getPath());
                }
            }
        });
        //Master Detail View (BSP Artifacts)
        return primaryPaths;
    }

    public static Map<String, String> getSecondaryViewPaths(Map<String, Map<HttpMethod, StrucPath>> pathsForTag, List<String> primaryPaths) {
        //primary: /artifact/ -> secondary: /artifact/{id}/

        //secondaryPath -> primaryPath
        Map<String,String> secondaryPaths = new HashMap<>();

        primaryPaths.forEach(primaryPath -> {
            String secondaryRegex = primaryPath;
            if (!primaryPath.endsWith("/")) {
                secondaryRegex += "/";
            }
            secondaryRegex += "{";
            String finalSecondaryRegex = secondaryRegex;
            pathsForTag.keySet().forEach(path -> {
                if (path.startsWith(finalSecondaryRegex) && path.split("}").length == 1
                        && (path.endsWith("}") || path.endsWith("}/"))) {
                    secondaryPaths.put(primaryPath, path);
                }
            });
        });
        return secondaryPaths;
    }

    public static StrucPath operationToStrucPath(String path, HttpMethod httpMethod, Operation operation, Map<String, StrucSchema> strucSchemaMap) {
        StrucPath strucPath = new StrucPath();
        strucPath.setPath(path);
        strucPath.setHttpMethod(httpMethod);
        log.info("Converting Operation to Path for Path {} and HttpMethod {}", path, httpMethod.toString());

        //Querry params
        if(operation.getParameters()!=null && !operation.getParameters().isEmpty()){
            operation.getParameters().forEach( parameter -> {
                strucPath.getQueryParams().add(new StrucPath.StruckQueryParameter(parameter.getName(),
                        PropertyTypeEnum.fromString(parameter.getSchema().getType()),parameter.getRequired()));
            });
        }

        if (HttpMethod.POST.equals(httpMethod) || HttpMethod.PUT.equals(httpMethod)) { //TODO was in get oder delete ?, was tun wenn kein ref schema
            if (operation.getRequestBody() != null) { //example: /api/apps/{id}/actions and HttpMethod PUT-
                if (operation.getRequestBody().getContent().containsKey("application/json") && operation.getRequestBody().getContent().get("application/json").getSchema() != null) { //Has actual Schema (Example /artifacts/id/data)
                    //TODO application/octet-stream (data PUT)
                    String externalSchemaPath = operation.getRequestBody().getContent().get("application/json").getSchema().get$ref(); //TODO was wenn kein href & für andere arten von content
                    if (externalSchemaPath != null) {
                        StrucSchema strucSchema = strucSchemaMap.get(SchemaService.stripSchemaRefPath(externalSchemaPath));
                        strucPath.setRequestStrucSchema(strucSchema);
                    } else {
                        StrucSchema strucSchema = SchemaService.mapSchemaToStrucSchema("noName", operation.getRequestBody().getContent().get("application/json").getSchema());
                        strucPath.setRequestStrucSchema(strucSchema);
                    }

                } else {
                    StrucSchema strucSchema = new StrucSchema();
                    strucSchema.setFreeSchema(true);
                }
            }
        }
        if (HttpMethod.GET.equals(httpMethod)) { //TODO was wenn mehrere rückgaben möglich
            if (operation.getResponses().containsKey("200") && operation.getResponses().get("200").getContent() != null) {
                if (operation.getResponses().get("200").getContent().containsKey("*/*"))
                    setResponseSchema(strucPath, operation, "200", "*/*",strucSchemaMap);
                else if (operation.getResponses().get("200").getContent().containsKey("application/json"))
                    setResponseSchema(strucPath, operation, "200", "application/json",strucSchemaMap);
            }
        } else {
            log.info("The current path can only respond with the following http codes: {}", operation.getResponses().keySet());
        }
        return strucPath;
    }

    /**
     * This function finds the schema of the content in the response and puts it into the StrucPath object
     *
     * @param strucPath  the Strucpath to set the ResponseBodySchema in
     * @param operation  the Operation that holds the inital Schema
     * @param returnCode the http returnCode to be searched
     * @param returnType the returnType to be searched
     */
    public static void setResponseSchema(StrucPath strucPath, Operation operation, String returnCode, String returnType, Map<String, StrucSchema> strucSchemaMap) {
        if (operation.getResponses().get(returnCode).getContent().containsKey(returnType)) {
            if (operation.getResponses().get(returnCode).getContent().get(returnType).getSchema().get$ref() != null) {
                String externalSchemaPath = operation.getResponses().get(returnCode).getContent().get(returnType).getSchema().get$ref();
                strucPath.setResponseStrucSchema(strucSchemaMap.get(SchemaService.stripSchemaRefPath(externalSchemaPath)));
            } else {
                StrucSchema strucSchema = SchemaService.mapSchemaToStrucSchema("noName", operation.getResponses().get(returnCode).getContent().get(returnType).getSchema());
                strucPath.setResponseStrucSchema(strucSchema);
            }
        }
    }

    public static Map<String, Map<HttpMethod, StrucPath>> getPathsForTag(String tagName, Paths paths,Map<String, StrucSchema> strucSchemaMap) {
        Map<String, Map<HttpMethod, StrucPath>> pathOperationMap = new HashMap<>(); //Path -> List
        paths.keySet().forEach(key -> { //TODO all paths without recognised tags into one sonstiges tag
            Map<HttpMethod, StrucPath> methodOperationMap = new HashMap<>();
            if (paths.get(key).getGet() != null
                    && paths.get(key).getGet().getTags() != null
                    && paths.get(key).getGet().getTags().contains(tagName)) {
                methodOperationMap.put(HttpMethod.GET,
                        operationToStrucPath(key, HttpMethod.GET, paths.get(key).getGet(),strucSchemaMap));
            }
            if (paths.get(key).getPost() != null
                    && paths.get(key).getPost().getTags() != null
                    && paths.get(key).getPost().getTags().contains(tagName)) {
                methodOperationMap.put(HttpMethod.POST,
                        operationToStrucPath(key, HttpMethod.POST, paths.get(key).getPost(),strucSchemaMap));
            }
            if (paths.get(key).getPut() != null
                    && paths.get(key).getPut().getTags() != null
                    && paths.get(key).getPut().getTags().contains(tagName)) {
                methodOperationMap.put(HttpMethod.PUT,
                        operationToStrucPath(key, HttpMethod.PUT, paths.get(key).getPut(),strucSchemaMap));
            }
            if (paths.get(key).getDelete() != null
                    && paths.get(key).getDelete().getTags() != null
                    && paths.get(key).getDelete().getTags().contains(tagName)) {
                methodOperationMap.put(HttpMethod.DELETE,
                        operationToStrucPath(key, HttpMethod.DELETE, paths.get(key).getDelete(),strucSchemaMap));
            }
            if (methodOperationMap.size() > 0)
                pathOperationMap.put(key, methodOperationMap);
        });
        return pathOperationMap;
    }
}
