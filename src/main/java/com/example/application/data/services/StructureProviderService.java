package com.example.application.data.services;

import com.example.application.data.structureModel.StrucOpenApi;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.data.structureModel.StrucViewGroup;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StructureProviderService {
    public static final String PARSE_OBJECT = "testOpenApi.yaml";

    public StrucOpenApi generateApiStructure(String pathToOpenApiFile) {
        OpenAPI openAPI = new OpenAPIV3Parser().read(pathToOpenApiFile);

        StrucOpenApi strucOpenApi = new StrucOpenApi();


        if (openAPI.getServers() != null) //TODO change -> server url can also come from lower objects
            strucOpenApi.setServers(openAPI.getServers().stream().map(Server::getUrl).collect(Collectors.toList()));

        List<StrucViewGroup> strucViewGroupList = new ArrayList<>();

        //alle components in dataschemas übersetzen und in liste tun
        Map<String, StrucSchema> strucSchemaMap = SchemaService.mapSchemasToStrucSchemas(openAPI.getComponents().getSchemas());

        //TODO was wenn kein Tag vorhanden

        Set<String> tagNames = collectTags(openAPI);
        log.info("Collected {} tags: {}", tagNames.size(), tagNames);

        tagNames.forEach(tag -> {
            log.info("Now looking for tag: " + tag);

            //sucht alle paths die zu diesem tag gehören und wandelt sie in StrucPath Objekte um
            Map<String, Map<HttpMethod, StrucPath>> pathsForTag = PathService.getPathsForTag(tag, openAPI.getPaths(), strucSchemaMap);
            log.debug("A total of {} paths have been found for the tag {}", pathsForTag.size(), tag);

            //alle components die zu path gehören aus strucSchemaMap holen und in strucViewGroup eintragen
            Map<String, StrucSchema> strucViewGroupSchemaMap = createStrucViewGroupSchemaMap(strucSchemaMap, pathsForTag);
            log.debug("A total of {} schemas have been found for the tag {}", pathsForTag.size(), tag);

            //Find primarypaths for this viewgroup
            List<String> primaryPaths = PathService.getPrimaryViewPaths(pathsForTag);
            log.debug("Primary Paths for tag " + tag + " are: " + primaryPaths);


            //TODO rethink how secondary and primaryPath get saved -> maybe List of primary+secondary List
            //Find secondaryPaths
            Map<String, String> secondaryPaths = PathService.getSecondaryViewPaths(pathsForTag, primaryPaths);
            log.debug("Secondary Paths for tag " + tag + " are: " + secondaryPaths);

            //TODO collect internal primary paths like /api/artifacts/{id}/representations -> make another list like primary paths
            //TODO internal primary path need to have following pattern: primarypath/{...}/...
            MultiValueMap<String, String> internalPrimaryPaths = PathService.getInternalPrimaryViewPaths(pathsForTag,primaryPaths);
            log.debug("Internal Primary Paths for tag " + tag + " are: " + internalPrimaryPaths);

            //TODO put internal primaryPath into StrucViewGroup, Put internal primaryPath into MDVStrucViewGroup
            StrucViewGroup strucViewGroup = new StrucViewGroup(tag, primaryPaths, secondaryPaths, internalPrimaryPaths,strucViewGroupSchemaMap, pathsForTag);

            strucViewGroupList.add(strucViewGroup);
        });

        strucOpenApi.setStrucViewGroups(strucViewGroupList);

        return strucOpenApi;
    }

    private Set<String> collectTags(OpenAPI openAPI) {
        Set<String> tags = new HashSet<>();
        if (openAPI.getTags() != null) {
            tags.addAll(openAPI.getTags().stream().map(Tag::getName).toList());
        }

        if (openAPI.getPaths() != null) {
            openAPI.getPaths().forEach((key, value) -> tags.addAll(collectTags(value)));
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
        pathsForTag.forEach((tag, paths) -> paths.forEach((path, pathValue) -> {
            //Check Request Body Schema
            if (pathValue.getRequestStrucSchema() != null) {

                strucViewGroupSchemaMap.put(pathValue.getRequestStrucSchema().getName(), pathValue.getRequestStrucSchema());
            }
            //Check Response Body Schema
            if (pathValue.getResponseStrucSchema() != null) {
                strucViewGroupSchemaMap.put(pathValue.getResponseStrucSchema().getName(), pathValue.getResponseStrucSchema());
                //If Response is a PagedObject -> Add whats behind the paged Object //TODO replace with add all nested objects
                if (SchemaService.isPagedSchema(pathValue.getResponseStrucSchema())) {
                    String pagedSchemaName = SchemaService.getPagedSchemaName(pathValue.getResponseStrucSchema());
                    strucViewGroupSchemaMap.put(pagedSchemaName, strucSchemaMap.get(pagedSchemaName));
                }

            }

            //TODO collect get refs from pathValue.getExternalResponseBodySchemaName()
            //TODO add those refs
            //TODO check internal (nested) Schemas
            //SchemaService.getNestedSchemaNames();

        }));
        return strucViewGroupSchemaMap;
    }
}
