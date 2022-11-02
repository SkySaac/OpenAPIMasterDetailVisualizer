package openapivisualizer.application.generation.services;

import com.vaadin.flow.spring.annotation.UIScope;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.structuremodel.OpenApiStructure;
import openapivisualizer.application.generation.structuremodel.StrucPath;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.generation.structuremodel.TagGroup;
import openapivisualizer.application.rest.client.ClientDataService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@UIScope
public class StructureProviderService {
    public static final String DEFAULT_PARSE_OBJECT = "testOpenApi.yaml";

    private final SchemaService schemaService;
    private final PathService pathService;
    private final ClientDataService clientDataService;
    public StructureProviderService(SchemaService schemaService, PathService pathService, ClientDataService clientDataService) {
        this.schemaService = schemaService;
        this.pathService = pathService;
        this.clientDataService = clientDataService;
    }

    public OpenApiStructure generateApiStructure(String pathToOpenApiFile) {
        OpenAPI openApi = clientDataService.getOpenApi(pathToOpenApiFile);

        OpenApiStructure openApiStructure = new OpenApiStructure();

        if (openApi.getSecurity() != null)
            openApiStructure.setHasHttpBasic(openApi.getSecurity().contains("basicAuth"));

        if (openApi.getServers() != null) //TODO change -> server url can also come from lower objects
            openApiStructure.setServers(openApi.getServers().stream().map(Server::getUrl).collect(Collectors.toList()));

        List<TagGroup> tagGroupList = new ArrayList<>();

        Map<String, StrucSchema> strucSchemaMap = schemaService.mapSchemasToStrucSchemas(openApi.getComponents().getSchemas());

        //TODO was wenn kein Tag vorhanden

        Set<String> tagNames = collectTags(openApi);
        log.info("Collected {} tags: {}", tagNames.size(), tagNames);

        tagNames.forEach(tag -> {
            log.info("Now looking for tag: " + tag);

            Map<String, Map<HttpMethod, StrucPath>> pathsForTag = pathService.getPathsForTag(tag, openApi.getPaths(), strucSchemaMap);
            log.debug("A total of {} paths have been found for the tag {}", pathsForTag.size(), tag);

            Map<String, StrucSchema> strucViewGroupSchemaMap = createViewGroupSchemaMap(strucSchemaMap, pathsForTag);
            log.debug("A total of {} schemas have been found for the tag {}", pathsForTag.size(), tag);

            //Find apipaths for this viewgroup
            List<String> apiPaths = pathService.getApiPaths(pathsForTag);
            log.debug("API Paths for tag " + tag + " are: " + apiPaths);


            //Find uriPaths
            Map<String, String> uriPaths = pathService.getUriPaths(pathsForTag, apiPaths);
            log.debug("URI Paths for tag " + tag + " are: " + uriPaths);

            //TODO collect relation paths like /api/artifacts/{id}/representations -> make another list like api paths
            //TODO relation path need to have following pattern: apipath/{...}/...
            MultiValueMap<String, String> relationPaths = pathService.getRelationPaths(pathsForTag, apiPaths);
            log.debug("Relation Paths for tag " + tag + " are: " + relationPaths);

            //TODO put relationPath into StrucViewGroup, Put relationPath into MDVStrucViewGroup
            TagGroup tagGroup = new TagGroup(tag, apiPaths, uriPaths, relationPaths, strucViewGroupSchemaMap, pathsForTag);

            tagGroupList.add(tagGroup);
        });

        openApiStructure.setTagGroups(tagGroupList);

        return openApiStructure;
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

    private Map<String, StrucSchema> createViewGroupSchemaMap(Map<String, StrucSchema> strucSchemaMap, Map<String, Map<HttpMethod, StrucPath>> pathsForTag) {
        Map<String, StrucSchema> strucViewGroupSchemaMap = new HashMap<>();
        pathsForTag.forEach((tag, paths) -> paths.forEach((path, pathValue) -> {
            //Check Request Body Schema
            if (pathValue.getRequestStrucSchema() != null) {

                strucViewGroupSchemaMap.put(pathValue.getRequestStrucSchema().getName(), pathValue.getRequestStrucSchema());
            }
            //Check Response Body Schema
            if (pathValue.getResponseStrucSchema() != null) {
                strucViewGroupSchemaMap.put(pathValue.getResponseStrucSchema().getName(), pathValue.getResponseStrucSchema());
                if (schemaService.isPagedSchema(pathValue.getResponseStrucSchema())) {
                    String pagedSchemaName = schemaService.getPagedSchemaName(pathValue.getResponseStrucSchema());
                    strucViewGroupSchemaMap.put(pagedSchemaName, strucSchemaMap.get(pagedSchemaName));
                }

            }
        }));
        return strucViewGroupSchemaMap;
    }
}
