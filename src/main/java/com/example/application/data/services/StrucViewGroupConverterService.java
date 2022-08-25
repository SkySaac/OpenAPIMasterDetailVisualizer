package com.example.application.data.services;

import com.example.application.data.structureModel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class StrucViewGroupConverterService {

    public static StrucViewGroupLV createStrucViewGroupLV(StrucViewGroup strucViewGroup) {
        log.debug("List LV primary paths for {}: {}", strucViewGroup.getTagName(), strucViewGroup.getPrimaryPaths());

        //creates internal MDVs
        Map<String, StrucViewGroupMDV> internalMDVStrucViewGroups = new HashMap<>();
        strucViewGroup.getPrimaryPaths().forEach(primaryPath -> {
            StrucViewGroup strucViewGroupInternalMD = new StrucViewGroup(strucViewGroup.getTagName(), List.of(primaryPath)
                    , strucViewGroup.getSecondaryPaths().entrySet().stream().filter(e -> e.getKey().equals(primaryPath)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    , new LinkedMultiValueMap<>()
                    , strucViewGroup.getStrucSchemaMap()
                    , strucViewGroup.getStrucPathMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(primaryPath)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            internalMDVStrucViewGroups.put(primaryPath, createStrucViewGroupMDV(strucViewGroupInternalMD));
        });

        return new StrucViewGroupLV(strucViewGroup.getTagName(), strucViewGroup.getStrucSchemaMap(), //TODO remove secondary paths
                strucViewGroup.getStrucPathMap().entrySet().stream()
                        .filter(entry -> !strucViewGroup.getSecondaryPaths().containsValue(entry.getKey()) //TODO mal strucviewgroup angucken ob alles richtig zugeordnet ist
                                && !strucViewGroup.getPrimaryPaths().contains(entry.getKey())
                                && strucViewGroup.getInternalPrimaryPaths().values().stream().noneMatch(values -> values.contains(entry.getKey())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                internalMDVStrucViewGroups);
    }

    public static StrucViewGroupMDV createStrucViewGroupMDV(StrucViewGroup strucViewGroup) {
        //if (!isMDVStructure(strucViewGroup)) return null; //TODO glaube kann raus, würde sonst listview dinger

        StrucViewGroupMDV primaryStrucViewGroup = createSingleStrucViewGroupMDV(strucViewGroup.getTagName(),
                strucViewGroup.getPrimaryPaths().get(0),
                strucViewGroup.getSecondaryPaths().get(strucViewGroup.getPrimaryPaths().get(0)),
                strucViewGroup.getStrucSchemaMap(),
                strucViewGroup.getStrucPathMap());

        //there should only be 1 entry (the primary path)
        if (strucViewGroup.getInternalPrimaryPaths().containsKey(strucViewGroup.getPrimaryPaths().get(0)))
            strucViewGroup.getInternalPrimaryPaths().get(strucViewGroup.getPrimaryPaths().get(0)).forEach(internalPrimaryPath -> {
                StrucViewGroupMDV internalStrucViewGroupMDV = createSingleStrucViewGroupMDV(strucViewGroup.getTagName(),
                        internalPrimaryPath, null, strucViewGroup.getStrucSchemaMap(),
                        strucViewGroup.getStrucPathMap());
                primaryStrucViewGroup.getInternalMDVs().put(internalPrimaryPath, internalStrucViewGroupMDV);
            });

        return primaryStrucViewGroup;
    }

    public static StrucViewGroupMDV createSingleStrucViewGroupMDV(String tagName, String primaryPath,
                                                           String secondaryPath,
                                                           Map<String, StrucSchema> groupStrucSchemaMap,
                                                           Map<String, Map<HttpMethod, StrucPath>> groupStrucPathMap) {

        Map<HttpMethod, StrucPath> strucPathMap = new HashMap<>();
        Map<HttpMethod, StrucSchema> strucSchemaMap = new HashMap<>();
        //There should only be one primary path
        StrucPath primaryGetPath = groupStrucPathMap.get(primaryPath).get(HttpMethod.GET);

        //Add the paged Schema (if it exists)
        StrucSchema pagedStrucSchema = null;
        if (primaryGetPath == null)
            log.info("debug me");
        if (SchemaService.isPagedSchema(primaryGetPath.getResponseStrucSchema()))
            pagedStrucSchema = groupStrucSchemaMap.get(SchemaService.getPagedSchemaName(primaryGetPath.getResponseStrucSchema()));

        //GET (needs to exist)
        strucPathMap.put(HttpMethod.GET, primaryGetPath);
        strucSchemaMap.put(HttpMethod.GET, primaryGetPath.getResponseStrucSchema());

        //POST (only looks as input, not response)
        if (groupStrucPathMap.get(primaryGetPath.getPath()).containsKey(HttpMethod.POST)) { //If a POST exists for this path based on the Rest vorgaben
            StrucPath postPath = groupStrucPathMap.get(primaryGetPath.getPath()).get(HttpMethod.POST);

            strucPathMap.put(HttpMethod.POST, postPath);
            strucSchemaMap.put(HttpMethod.POST, postPath.getRequestStrucSchema());
        }

        //PUT (only looks as input, not response) IS FOR SECONDARYPATH //TODO can also be primary path -> prob has reuqired query param
        if (groupStrucPathMap.get(secondaryPath) != null &&
                groupStrucPathMap.get(secondaryPath).containsKey(HttpMethod.PUT)) { //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = groupStrucPathMap.get(secondaryPath).get(HttpMethod.PUT);

            strucPathMap.put(HttpMethod.PUT, putPath);
            strucSchemaMap.put(HttpMethod.PUT, putPath.getRequestStrucSchema());
        } else if (groupStrucPathMap.get(primaryGetPath) != null &&
                groupStrucPathMap.get(primaryGetPath).containsKey(HttpMethod.PUT)) { //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = groupStrucPathMap.get(primaryGetPath).get(HttpMethod.PUT);

            strucPathMap.put(HttpMethod.PUT, putPath);
            strucSchemaMap.put(HttpMethod.PUT, putPath.getRequestStrucSchema());
        }

        //DELETE (only looks as input, not response) IS FOR SECONDARYPATH  //TODO can also be primary path -> prob has reuqired query param
        if (groupStrucPathMap.get(secondaryPath) != null &&
                groupStrucPathMap.get(secondaryPath).containsKey(HttpMethod.DELETE)) { //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = groupStrucPathMap.get(secondaryPath).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE, deletePath);
        } else if (groupStrucPathMap.get(primaryGetPath) != null &&
                groupStrucPathMap.get(primaryGetPath).containsKey(HttpMethod.DELETE)) { //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = groupStrucPathMap.get(primaryGetPath).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE, deletePath);
        }

        return new StrucViewGroupMDV(tagName, pagedStrucSchema, strucPathMap, secondaryPath, strucSchemaMap);
    }

    public static boolean isMDVStructure(StrucViewGroup strucViewGroup) {
        return strucViewGroup.getPrimaryPaths().size() == 1
                && strucViewGroup.getStrucPathMap().keySet().stream()
                .allMatch(path -> path.startsWith(strucViewGroup.getPrimaryPaths().get(0)))
                && strucViewGroup.getStrucPathMap().get(strucViewGroup.getPrimaryPaths().get(0)).containsKey(HttpMethod.GET)
                && strucViewGroup.getStrucPathMap().get(strucViewGroup.getPrimaryPaths().get(0)).get(HttpMethod.GET)
                .getResponseStrucSchema() != null
                && strucViewGroup.getStrucPathMap().keySet().stream() //All paths
                .allMatch(path -> strucViewGroup.getPrimaryPaths().get(0).equals(path)
                        || strucViewGroup.getSecondaryPaths().containsValue(path)
                        || (strucViewGroup.getInternalPrimaryPaths().containsKey(strucViewGroup.getPrimaryPaths().get(0)) &&
                        strucViewGroup.getInternalPrimaryPaths().get(strucViewGroup.getPrimaryPaths().get(0)).contains(path)));
    }
}
