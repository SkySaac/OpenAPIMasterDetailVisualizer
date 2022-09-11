package openapivisualizer.application.generation.structuremodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ViewGroupMDV {
    private String tagName;
    private Map<HttpMethod,StrucPath> primaryStrucPathMap;
    private ViewGroupMDV secondaryViewGroup;

    private final Map<String, ViewGroupMDV> internalMDVs = new HashMap<>();

}

