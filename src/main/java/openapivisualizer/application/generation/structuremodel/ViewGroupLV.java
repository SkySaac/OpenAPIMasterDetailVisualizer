package openapivisualizer.application.generation.structuremodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ViewGroupLV {
    private String tagName;
    private Map<String, StrucSchema> notMatchedStrucSchemaMap;
    private Map<String, Map<HttpMethod, StrucPath>> notMatchedStrucPathMap;

    private Map<String, ViewGroupMDV> strucViewGroupMDVS; //TODO path -> SVGMDV
}
