package openapivisualizer.application.generation.services;

import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.structuremodel.StrucPath;
import openapivisualizer.application.generation.structuremodel.TagGroup;
import openapivisualizer.application.generation.structuremodel.TagGroupLV;
import openapivisualizer.application.generation.structuremodel.TagGroupMD;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagGroupConverterService {

    public TagGroupLV createTagGroupLV(TagGroup tagGroup, boolean showAllPaths) {
        log.debug("List LV apiPaths for {}: {}", tagGroup.getTagName(), tagGroup.getApiPaths());

        //creates relation MDVs
        Map<String, TagGroupMD> relationTagGroups = new HashMap<>();
        tagGroup.getApiPaths().forEach(apiPath -> {

            MultiValueMap<String, String> relationPaths = new LinkedMultiValueMap<>();
            relationPaths.put(apiPath, tagGroup.getRelationPaths().get(apiPath));

            TagGroup tagGroupInternalMD = new TagGroup(
                    tagGroup.getTagName(), List.of(apiPath)
                    , tagGroup.getUriPaths().entrySet().stream().filter(e -> e.getKey().equals(apiPath)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    , relationPaths
                    , tagGroup.getSchemaMap()
                    , tagGroup.getPathMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(apiPath)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
            relationTagGroups.put(apiPath, createMDTagGroup(tagGroupInternalMD));
        });

        Map<String, Map<HttpMethod, StrucPath>> notMatchedPaths;
//        if (showAllPaths) {
//            notMatchedPaths = tagGroup.getPathMap();
//        } else {
            notMatchedPaths = tagGroup.getPathMap().entrySet().stream()
                    .filter(entry -> !tagGroup.getUriPaths().containsValue(entry.getKey())
                            && !tagGroup.getApiPaths().contains(entry.getKey())
                            && tagGroup.getRelationPaths().values().stream().noneMatch(values -> values.contains(entry.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        }

        notMatchedPaths.forEach((path, httpMethodStrucPathMap) -> {
            if (httpMethodStrucPathMap.containsKey(HttpMethod.GET)) {

                MultiValueMap<String, String> internalPaths = new LinkedMultiValueMap<>();
                internalPaths.put(path, tagGroup.getRelationPaths().get(path));

                TagGroup tagGroupInternalMD = new TagGroup(
                        tagGroup.getTagName(), List.of(path)
                        , tagGroup.getUriPaths().entrySet().stream().filter(e -> e.getKey().equals(path)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                        , internalPaths
                        , tagGroup.getSchemaMap()
                        , tagGroup.getPathMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(path)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );
                relationTagGroups.put(path, createMDTagGroup(tagGroupInternalMD));
            }
        });

        notMatchedPaths.forEach((key, value) -> value.remove(HttpMethod.GET));

        return new TagGroupLV(tagGroup.getTagName(), tagGroup.getSchemaMap(),
                notMatchedPaths, relationTagGroups);
    }

    public TagGroupMD createMDTagGroup(TagGroup tagGroup) {
        TagGroupMD apiViewGroup = createSingleTagGroupMD(tagGroup.getTagName(),
                tagGroup.getApiPaths().get(0),
                tagGroup.getUriPaths().get(tagGroup.getApiPaths().get(0)),
                tagGroup.getPathMap());

        //there should only be 1 entry (the api path)
        if (tagGroup.getRelationPaths().containsKey(tagGroup.getApiPaths().get(0))
                && tagGroup.getRelationPaths().get(tagGroup.getApiPaths().get(0)) != null)
            tagGroup.getRelationPaths().get(tagGroup.getApiPaths().get(0)).forEach(relationPath -> {
                TagGroupMD relationTagGroupMD = createSingleTagGroupMD(tagGroup.getTagName(),
                        relationPath, null,
                        tagGroup.getPathMap());
                apiViewGroup.getRelationTagGroup().put(relationPath, relationTagGroupMD);
            });

        return apiViewGroup;
    }

    private TagGroupMD createSingleTagGroupMD(String tagName, String apiPath,
                                              String uriPath,
                                              Map<String, Map<HttpMethod, StrucPath>> groupStrucPathMap) {

        Map<HttpMethod, StrucPath> strucPathMap = new HashMap<>();
        //There should only be one api path
        StrucPath apiGetPath = groupStrucPathMap.get(apiPath).get(HttpMethod.GET);

        TagGroupMD uriTagGroupMD = null;

        if (apiGetPath == null)
            log.info("debug me");

        //GET (needs to exist)
        strucPathMap.put(HttpMethod.GET, apiGetPath);

        //POST (only looks as input, not response)
        if (groupStrucPathMap.get(apiGetPath.getPath()).containsKey(HttpMethod.POST)) { //If a POST exists for this path based on the Rest vorgaben
            StrucPath postPath = groupStrucPathMap.get(apiGetPath.getPath()).get(HttpMethod.POST);

            strucPathMap.put(HttpMethod.POST, postPath);
        }

        //PUT (only looks as input, not response) IS FOR URIPATH //TODO can also be api path -> prob has reuqired query param
        if (groupStrucPathMap.get(uriPath) != null &&
                groupStrucPathMap.get(uriPath).containsKey(HttpMethod.PUT)) { //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = groupStrucPathMap.get(uriPath).get(HttpMethod.PUT);

            strucPathMap.put(HttpMethod.PUT, putPath);
        } else if (groupStrucPathMap.containsKey(apiGetPath) &&
                groupStrucPathMap.get(apiGetPath).containsKey(HttpMethod.PUT)) { //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = groupStrucPathMap.get(apiGetPath).get(HttpMethod.PUT);

            strucPathMap.put(HttpMethod.PUT, putPath);
        }

        //DELETE (only looks as input, not response) IS FOR uri path  //TODO can also be api path -> prob has reuqired query param
        if (groupStrucPathMap.get(uriPath) != null &&
                groupStrucPathMap.get(uriPath).containsKey(HttpMethod.DELETE)) { //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = groupStrucPathMap.get(uriPath).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE, deletePath);
        } else if (groupStrucPathMap.containsKey(apiGetPath) &&
                groupStrucPathMap.get(apiGetPath).containsKey(HttpMethod.DELETE)) { //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = groupStrucPathMap.get(apiGetPath).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE, deletePath);
        }

        if (groupStrucPathMap.get(uriPath) != null) {
            uriTagGroupMD = new TagGroupMD(tagName, groupStrucPathMap.get(uriPath), null);
        }

        return new TagGroupMD(tagName, strucPathMap, uriTagGroupMD);
    }

    public boolean isMDVStructure(TagGroup tagGroup) {
        return tagGroup.getApiPaths().size() == 1
                && tagGroup.getPathMap().keySet().stream()
                .allMatch(path -> path.startsWith(tagGroup.getApiPaths().get(0)))
                && tagGroup.getPathMap().get(tagGroup.getApiPaths().get(0)).containsKey(HttpMethod.GET)
                && tagGroup.getPathMap().get(tagGroup.getApiPaths().get(0)).get(HttpMethod.GET)
                .getResponseStrucSchema() != null
                && tagGroup.getPathMap().keySet().stream() //All paths
                .allMatch(path -> tagGroup.getApiPaths().get(0).equals(path)
                        || tagGroup.getUriPaths().containsValue(path)
                        || (tagGroup.getRelationPaths().containsKey(tagGroup.getApiPaths().get(0)) &&
                        tagGroup.getRelationPaths().get(tagGroup.getApiPaths().get(0)).contains(path)));
    }
}
