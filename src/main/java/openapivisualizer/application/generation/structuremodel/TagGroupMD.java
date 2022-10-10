package openapivisualizer.application.generation.structuremodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class TagGroupMD {
    private String tagName;
    private Map<HttpMethod,StrucPath> apiPathMap;
    private TagGroupMD uriTagGroup;

    private final Map<String, TagGroupMD> relationTagGroup = new HashMap<>();

}

