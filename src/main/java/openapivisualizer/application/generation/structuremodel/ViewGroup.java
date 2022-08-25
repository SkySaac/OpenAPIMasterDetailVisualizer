package openapivisualizer.application.generation.structuremodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ViewGroup {
    private String tagName;
    private List<String> primaryPaths;
    private Map<String, String> secondaryPaths;
    private MultiValueMap<String, String> internalPrimaryPaths;
    private Map<String, StrucSchema> strucSchemaMap; //TODO remove ?
    private Map<String, Map<HttpMethod, StrucPath>> strucPathMap;
}
