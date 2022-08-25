package openapivisualizer.application.generation.structuremodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ViewGroupMDV {
    private String tagName;
    private StrucSchema wrappedGetSchema;
    private Map<HttpMethod,StrucPath> primaryStrucPathMap;
    private String secondaryGetPath;
    private Map<HttpMethod,StrucSchema> strucSchemaMap;

    private final Map<String, ViewGroupMDV> internalMDVs = new HashMap<>();

    public boolean isWrapped(){
        return wrappedGetSchema !=null;
    }
}

