package com.example.application.data.services;

import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.data.structureModel.StrucViewGroup;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StrucViewGroupConverterService {

    private final StructureProviderService structureProviderService;

    public StrucViewGroupConverterService(StructureProviderService structureProviderService){
        this.structureProviderService = structureProviderService;
    }
    public StrucViewGroupMDV createStrucViewGroupMDV(StrucViewGroup strucViewGroup) {
        if(!isMDVStructure(strucViewGroup)) return null;
        Map<HttpMethod, StrucPath> strucPathMap = new HashMap<>();
        Map<HttpMethod, StrucSchema> strucSchemaMap = new HashMap<>();

        strucViewGroup.getPrimaryPaths().get(0).getExternalResponseBodySchemaName();

        StrucPath primaryGetPath = strucViewGroup.getPrimaryPaths().get(0);


        //Add the paged Schema (if it exists)
        StrucSchema pagedStrucSchema = null;
        if(structureProviderService.isPagedSchema(strucViewGroup.getStrucSchemaMap().get(primaryGetPath.getExternalResponseBodySchemaName())))
            pagedStrucSchema = strucViewGroup.getStrucSchemaMap().get(structureProviderService.getPagedSchemaName(strucViewGroup.getStrucSchemaMap().get(primaryGetPath.getExternalResponseBodySchemaName())));

        //GET (needs to exist)
        strucPathMap.put(HttpMethod.GET,primaryGetPath); //TODO PAGED Schema
        if(primaryGetPath.isExternalResponseSchema()) //Ist Ref
            strucSchemaMap.put(HttpMethod.GET,strucViewGroup.getStrucSchemaMap().get(primaryGetPath.getExternalResponseBodySchemaName()));
        else //Ist direkt definiertes Schema
            strucSchemaMap.put(HttpMethod.GET,primaryGetPath.getResponseStrucSchema());

        //POST (only looks as input, not response)
        if(strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).containsKey(HttpMethod.POST)){ //If a POST exists for this path based on the Rest vorgaben
            StrucPath postPath = strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).get(HttpMethod.POST);
            strucPathMap.put(HttpMethod.POST,postPath);
            if(postPath.isExternalRequestSchema()) //Ist Ref
                strucSchemaMap.put(HttpMethod.POST,strucViewGroup.getStrucSchemaMap().get(postPath.getExternalRequestBodySchemaName()));
            else //Ist direkt definiertes Schema
                strucSchemaMap.put(HttpMethod.POST,postPath.getRequestStrucSchema());
        }

        //PUT (only looks as input, not response)
        if(strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).containsKey(HttpMethod.PUT)){ //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).get(HttpMethod.PUT);
            strucPathMap.put(HttpMethod.PUT,putPath);
            if(putPath.isExternalRequestSchema()) //Is Reference
                strucSchemaMap.put(HttpMethod.PUT,strucViewGroup.getStrucSchemaMap().get(putPath.getExternalRequestBodySchemaName()));
            else //Is directly defined schema
                strucSchemaMap.put(HttpMethod.PUT,putPath.getRequestStrucSchema());
        }

        //DELETE (only looks as input, not response)
        if(strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).containsKey(HttpMethod.DELETE)){ //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = strucViewGroup.getStrucPathMap().get(primaryGetPath.getPath()).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE,deletePath);
        }

        StrucViewGroupMDV strucViewGroupMDV = new StrucViewGroupMDV(strucViewGroup.getTagName(),pagedStrucSchema,strucPathMap,strucSchemaMap);
        return strucViewGroupMDV;
    }

    public boolean isMDVStructure(StrucViewGroup strucViewGroup) {
        //TODO check if primary path has GET
        //TODO check if primary path GET has valid schema !=null
        return strucViewGroup.getPrimaryPaths().size() == 1
                && strucViewGroup.getStrucPathMap().keySet().stream()
                .allMatch(path -> path.startsWith(strucViewGroup.getPrimaryPaths().get(0).getPath()));
    }
}
