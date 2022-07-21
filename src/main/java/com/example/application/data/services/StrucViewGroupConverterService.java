package com.example.application.data.services;

import com.example.application.data.structureModel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StrucViewGroupConverterService {

    public StrucViewGroupLV createStrucViewGroupLV(StrucViewGroup strucViewGroup) {
        log.info("List primary paths for {}: {}", strucViewGroup.getTagName(), strucViewGroup.getPrimaryPaths());

        //creates internal MDVs
        Map<String,StrucViewGroupMDV> internalMDVStrucViewGroups = new HashMap<>();
        strucViewGroup.getPrimaryPaths().forEach(primaryPath -> {
            StrucViewGroup strucViewGroupInternalMD = new StrucViewGroup(strucViewGroup.getTagName(),List.of(primaryPath)
            , strucViewGroup.getSecondaryPaths().entrySet().stream().filter( entry -> entry.getValue().equals(primaryPath)).collect(Collectors.toMap(e->e.getKey(),e->e.getValue()))
                    , strucViewGroup.getStrucSchemaMap()
                    ,strucViewGroup.getStrucPathMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(primaryPath)).collect(Collectors.toMap(e->e.getKey(),e->e.getValue())));
            internalMDVStrucViewGroups.put(primaryPath,createStrucViewGroupMDV(strucViewGroupInternalMD));
        });

        return new StrucViewGroupLV(strucViewGroup.getTagName(), strucViewGroup.getPrimaryPaths(), strucViewGroup.getSecondaryPaths(), strucViewGroup.getStrucSchemaMap(), strucViewGroup.getStrucPathMap(), internalMDVStrucViewGroups);
    }

    public StrucViewGroupMDV createStrucViewGroupMDV(StrucViewGroup strucViewGroup) {
        if (!isMDVStructure(strucViewGroup)) return null; //TODO glaube kann raus, würde sonst lsitview dinger
        Map<HttpMethod, StrucPath> strucPathMap = new HashMap<>();
        Map<HttpMethod, StrucSchema> strucSchemaMap = new HashMap<>();

        //There should only be one primary path
        StrucPath primaryGetPath = strucViewGroup.getStrucPathMap().get(strucViewGroup.getPrimaryPaths().get(0)).get(HttpMethod.GET);

        String secondaryPath = strucViewGroup.getSecondaryPaths().get(primaryGetPath.getPath());

        //Add the paged Schema (if it exists)
        StrucSchema pagedStrucSchema = null;
        if (SchemaService.isPagedSchema(primaryGetPath.getResponseStrucSchema()))
            pagedStrucSchema = strucViewGroup.getStrucSchemaMap().get(SchemaService.getPagedSchemaName(primaryGetPath.getResponseStrucSchema()));

        //GET (needs to exist)
        strucPathMap.put(HttpMethod.GET, primaryGetPath);
        strucSchemaMap.put(HttpMethod.GET, primaryGetPath.getResponseStrucSchema());

        //POST (only looks as input, not response)
        if (strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).containsKey(HttpMethod.POST)) { //If a POST exists for this path based on the Rest vorgaben
            StrucPath postPath = strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).get(HttpMethod.POST);

            strucPathMap.put(HttpMethod.POST, postPath);
            strucSchemaMap.put(HttpMethod.POST, postPath.getRequestStrucSchema());
        }

        //PUT (only looks as input, not response) IS FOR SECONDARYPATH
        if (strucViewGroup.getStrucPathMap().get(secondaryPath) != null &&
                strucViewGroup.getStrucPathMap().get(secondaryPath).containsKey(HttpMethod.PUT)) { //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = strucViewGroup.getStrucPathMap().get(secondaryPath).get(HttpMethod.PUT);

            strucPathMap.put(HttpMethod.PUT, putPath);
            strucSchemaMap.put(HttpMethod.PUT, putPath.getRequestStrucSchema());
        }

        //DELETE (only looks as input, not response) IS FOR SECONDARYPATH
        if (strucViewGroup.getStrucPathMap().get(secondaryPath) != null &&
                strucViewGroup.getStrucPathMap().get(secondaryPath).containsKey(HttpMethod.DELETE)) { //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = strucViewGroup.getStrucPathMap().get(secondaryPath).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE, deletePath);
        }

        return new StrucViewGroupMDV(strucViewGroup.getTagName(), pagedStrucSchema, strucPathMap, strucSchemaMap);
    }

    public boolean isMDVStructure(StrucViewGroup strucViewGroup) {
        //TODO check if primary path has GET
        //TODO check if primary path GET has valid schema !=null
        return strucViewGroup.getPrimaryPaths().size() == 1
                && strucViewGroup.getStrucPathMap().keySet().stream()
                .allMatch(path -> path.startsWith(strucViewGroup.getPrimaryPaths().get(0)));
    }
}
