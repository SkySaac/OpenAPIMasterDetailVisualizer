package openapivisualizer.application.generation.structuremodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class TagGroup {
    private String tagName;
    private List<String> apiPaths;
    private Map<String, String> uriPaths;
    private MultiValueMap<String, String> relationPaths;
    private Map<String, StrucSchema> schemaMap;
    private Map<String, Map<HttpMethod, StrucPath>> pathMap;
}
