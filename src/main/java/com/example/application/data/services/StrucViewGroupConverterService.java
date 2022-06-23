package com.example.application.data.services;

import com.example.application.data.structureModel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StrucViewGroupConverterService {

    public StrucViewGroupLV createStrucViewGroupLV(StrucViewGroup strucViewGroup){
        log.info("List primary paths for {}: ",strucViewGroup.getTagName(),strucViewGroup.getPrimaryPaths());
        //TODO
        return new StrucViewGroupLV(strucViewGroup.getTagName(),strucViewGroup.getPrimaryPaths(),strucViewGroup.getSecondaryPaths(),strucViewGroup.getStrucSchemaMap(),strucViewGroup.getStrucPathMap());
    }

    public StrucViewGroupMDV createStrucViewGroupMDV(StrucViewGroup strucViewGroup) {
        if (!isMDVStructure(strucViewGroup)) return null;
        Map<HttpMethod, StrucPath> strucPathMap = new HashMap<>();
        Map<HttpMethod, StrucSchema> strucSchemaMap = new HashMap<>();

        //There should only be one primary path
        StrucPath primaryGetPath = strucViewGroup.getStrucPathMap().get(strucViewGroup.getPrimaryPaths().get(0)).get(HttpMethod.GET);

        String secondaryPath = strucViewGroup.getSecondaryPaths().get(primaryGetPath.getPath());

        //Add the paged Schema (if it exists)
        StrucSchema pagedStrucSchema = null;
        if (SchemaService.isPagedSchema(strucViewGroup.getStrucSchemaMap().get(primaryGetPath.getExternalResponseBodySchemaName())))
            pagedStrucSchema = strucViewGroup.getStrucSchemaMap().get(SchemaService.getPagedSchemaName(strucViewGroup.getStrucSchemaMap().get(primaryGetPath.getExternalResponseBodySchemaName())));

        //TODO resolve nested schemas

        //GET (needs to exist)
        strucPathMap.put(HttpMethod.GET, primaryGetPath); //TODO PAGED Schema
        if (primaryGetPath.isExternalResponseSchema()) //Ist Ref
            strucSchemaMap.put(HttpMethod.GET, strucViewGroup.getStrucSchemaMap().get(primaryGetPath.getExternalResponseBodySchemaName()));
        else //Ist direkt definiertes Schema
            strucSchemaMap.put(HttpMethod.GET, primaryGetPath.getResponseStrucSchema());

        //POST (only looks as input, not response)
        if (strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).containsKey(HttpMethod.POST)) { //If a POST exists for this path based on the Rest vorgaben
            StrucPath postPath = strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).get(HttpMethod.POST);
            strucPathMap.put(HttpMethod.POST, postPath);
            if (postPath.isExternalRequestSchema()) //Ist Ref
                strucSchemaMap.put(HttpMethod.POST, strucViewGroup.getStrucSchemaMap().get(postPath.getExternalRequestBodySchemaName()));
            else //Ist direkt definiertes Schema
                strucSchemaMap.put(HttpMethod.POST, postPath.getRequestStrucSchema());
        }

        //PUT (only looks as input, not response) IS FOR SECONDARYPATH
        if (strucViewGroup.getStrucPathMap().get(secondaryPath) != null &&
                strucViewGroup.getStrucPathMap().get(secondaryPath).containsKey(HttpMethod.PUT)) { //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = strucViewGroup.getStrucPathMap().get(secondaryPath).get(HttpMethod.PUT);
            strucPathMap.put(HttpMethod.PUT, putPath);
            if (putPath.isExternalRequestSchema()) //Is Reference
                strucSchemaMap.put(HttpMethod.PUT, strucViewGroup.getStrucSchemaMap().get(putPath.getExternalRequestBodySchemaName()));
            else //Is directly defined schema
                strucSchemaMap.put(HttpMethod.PUT, putPath.getRequestStrucSchema());
        }

        //DELETE (only looks as input, not response) IS FOR SECONDARYPATH
        if (strucViewGroup.getStrucPathMap().get(secondaryPath) != null &&
                strucViewGroup.getStrucPathMap().get(secondaryPath).containsKey(HttpMethod.DELETE)) { //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = strucViewGroup.getStrucPathMap().get(secondaryPath).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE, deletePath);
        }

        StrucViewGroupMDV strucViewGroupMDV = new StrucViewGroupMDV(strucViewGroup.getTagName(), pagedStrucSchema, strucPathMap, strucSchemaMap);
        return strucViewGroupMDV;
    }

    public boolean isMDVStructure(StrucViewGroup strucViewGroup) {
        //TODO check if primary path has GET
        //TODO check if primary path GET has valid schema !=null
        return strucViewGroup.getPrimaryPaths().size() == 1
                && strucViewGroup.getStrucPathMap().keySet().stream()
                .allMatch(path -> path.startsWith(strucViewGroup.getPrimaryPaths().get(0)));
    }
}
