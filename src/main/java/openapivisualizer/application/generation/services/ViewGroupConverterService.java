package openapivisualizer.application.generation.services;

import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.structuremodel.*;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ViewGroupConverterService {

    public static ViewGroupLV createViewGroupLV(ViewGroup viewGroup, boolean showAllPaths) {
        log.debug("List LV primary paths for {}: {}", viewGroup.getTagName(), viewGroup.getPrimaryPaths());

        //creates internal MDVs
        Map<String, ViewGroupMDV> internalMDVStrucViewGroups = new HashMap<>();
        viewGroup.getPrimaryPaths().forEach(primaryPath -> {

            MultiValueMap<String, String> internalPaths = new LinkedMultiValueMap<>();
            internalPaths.put(primaryPath, viewGroup.getInternalPrimaryPaths().get(primaryPath));

            ViewGroup viewGroupInternalMD = new ViewGroup(viewGroup.getTagName(), List.of(primaryPath)
                    , viewGroup.getSecondaryPaths().entrySet().stream().filter(e -> e.getKey().equals(primaryPath)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    , internalPaths
                    , viewGroup.getStrucSchemaMap()
                    , viewGroup.getStrucPathMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(primaryPath)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            internalMDVStrucViewGroups.put(primaryPath, createStrucViewGroupMDV(viewGroupInternalMD));
        });

        Map<String, Map<HttpMethod, StrucPath>> notMatchedPaths;
        if (showAllPaths) {
            notMatchedPaths = viewGroup.getStrucPathMap();
        } else {
            notMatchedPaths = viewGroup.getStrucPathMap().entrySet().stream()
                    .filter(entry -> !viewGroup.getSecondaryPaths().containsValue(entry.getKey()) //TODO mal strucviewgroup angucken ob alles richtig zugeordnet ist
                            && !viewGroup.getPrimaryPaths().contains(entry.getKey())
                            && viewGroup.getInternalPrimaryPaths().values().stream().noneMatch(values -> values.contains(entry.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        return new ViewGroupLV(viewGroup.getTagName(), viewGroup.getStrucSchemaMap(), notMatchedPaths//TODO remove secondary paths
                , internalMDVStrucViewGroups);
    }

    public static ViewGroupMDV createStrucViewGroupMDV(ViewGroup viewGroup) {
        //if (!isMDVStructure(strucViewGroup)) return null; //TODO glaube kann raus, würde sonst listview dinger

        ViewGroupMDV primaryStrucViewGroup = createSingleViewGroupMDV(viewGroup.getTagName(),
                viewGroup.getPrimaryPaths().get(0),
                viewGroup.getSecondaryPaths().get(viewGroup.getPrimaryPaths().get(0)),
                viewGroup.getStrucPathMap());

        //there should only be 1 entry (the primary path)
        if (viewGroup.getInternalPrimaryPaths().containsKey(viewGroup.getPrimaryPaths().get(0))
                && viewGroup.getInternalPrimaryPaths().get(viewGroup.getPrimaryPaths().get(0)) != null)
            viewGroup.getInternalPrimaryPaths().get(viewGroup.getPrimaryPaths().get(0)).forEach(internalPrimaryPath -> {
                ViewGroupMDV internalViewGroupMDV = createSingleViewGroupMDV(viewGroup.getTagName(),
                        internalPrimaryPath, null,
                        viewGroup.getStrucPathMap());
                primaryStrucViewGroup.getInternalMDVs().put(internalPrimaryPath, internalViewGroupMDV);
            });

        return primaryStrucViewGroup;
    }

    private static ViewGroupMDV createSecondaryStrucViewGroupMDV(String tagName, String secondaryPath, Map<String, Map<HttpMethod, StrucPath>> groupStrucPathMap) {
        Map<HttpMethod, StrucPath> strucPathMap = new HashMap<>();
        Map<HttpMethod, StrucSchema> strucSchemaMap = new HashMap<>();

        StrucPath primaryGetPath = groupStrucPathMap.get("primaryPath").get(HttpMethod.GET);
        return null;
    }


    private static ViewGroupMDV createSingleViewGroupMDV(String tagName, String primaryPath,
                                                         String secondaryPath,
                                                         Map<String, Map<HttpMethod, StrucPath>> groupStrucPathMap) {

        Map<HttpMethod, StrucPath> strucPathMap = new HashMap<>();
        //There should only be one primary path
        StrucPath primaryGetPath = groupStrucPathMap.get(primaryPath).get(HttpMethod.GET);

        ViewGroupMDV secondaryViewGroupMDV = null;

        if (primaryGetPath == null)
            log.info("debug me");

        //GET (needs to exist)
        strucPathMap.put(HttpMethod.GET, primaryGetPath);

        //POST (only looks as input, not response)
        if (groupStrucPathMap.get(primaryGetPath.getPath()).containsKey(HttpMethod.POST)) { //If a POST exists for this path based on the Rest vorgaben
            StrucPath postPath = groupStrucPathMap.get(primaryGetPath.getPath()).get(HttpMethod.POST);

            strucPathMap.put(HttpMethod.POST, postPath);
        }

        //PUT (only looks as input, not response) IS FOR SECONDARYPATH //TODO can also be primary path -> prob has reuqired query param
        if (groupStrucPathMap.get(secondaryPath) != null &&
                groupStrucPathMap.get(secondaryPath).containsKey(HttpMethod.PUT)) { //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = groupStrucPathMap.get(secondaryPath).get(HttpMethod.PUT);

            strucPathMap.put(HttpMethod.PUT, putPath);
        } else if (groupStrucPathMap.containsKey(primaryGetPath) &&
                groupStrucPathMap.get(primaryGetPath).containsKey(HttpMethod.PUT)) { //If a Put exists for this path based on the Rest vorgaben
            StrucPath putPath = groupStrucPathMap.get(primaryGetPath).get(HttpMethod.PUT);

            strucPathMap.put(HttpMethod.PUT, putPath);
        }

        //DELETE (only looks as input, not response) IS FOR SECONDARYPATH  //TODO can also be primary path -> prob has reuqired query param
        if (groupStrucPathMap.get(secondaryPath) != null &&
                groupStrucPathMap.get(secondaryPath).containsKey(HttpMethod.DELETE)) { //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = groupStrucPathMap.get(secondaryPath).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE, deletePath);
        } else if (groupStrucPathMap.containsKey(primaryGetPath) &&
                groupStrucPathMap.get(primaryGetPath).containsKey(HttpMethod.DELETE)) { //If a DELETE exists for this path based on the Rest vorgaben
            StrucPath deletePath = groupStrucPathMap.get(primaryGetPath).get(HttpMethod.DELETE);
            strucPathMap.put(HttpMethod.DELETE, deletePath);
        }

        if (groupStrucPathMap.get(secondaryPath) != null) {
            secondaryViewGroupMDV = new ViewGroupMDV(tagName, groupStrucPathMap.get(secondaryPath), null);
        }

        return new ViewGroupMDV(tagName, strucPathMap, secondaryViewGroupMDV);
    }

    public static boolean isMDVStructure(ViewGroup viewGroup) {
        return viewGroup.getPrimaryPaths().size() == 1
                && viewGroup.getStrucPathMap().keySet().stream()
                .allMatch(path -> path.startsWith(viewGroup.getPrimaryPaths().get(0)))
                && viewGroup.getStrucPathMap().get(viewGroup.getPrimaryPaths().get(0)).containsKey(HttpMethod.GET)
                && viewGroup.getStrucPathMap().get(viewGroup.getPrimaryPaths().get(0)).get(HttpMethod.GET)
                .getResponseStrucSchema() != null
                && viewGroup.getStrucPathMap().keySet().stream() //All paths
                .allMatch(path -> viewGroup.getPrimaryPaths().get(0).equals(path)
                        || viewGroup.getSecondaryPaths().containsValue(path)
                        || (viewGroup.getInternalPrimaryPaths().containsKey(viewGroup.getPrimaryPaths().get(0)) &&
                        viewGroup.getInternalPrimaryPaths().get(viewGroup.getPrimaryPaths().get(0)).contains(path)));
    }
}
