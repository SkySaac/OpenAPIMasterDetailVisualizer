package openapivisualizer.application.generation.structuremodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StrucPath {
    //TODO was wenn request/response body keine schemas sind sondern direkt objekte ?
    private String path;
    private HttpMethod httpMethod;
    private StrucSchema responseStrucSchema;
    private StrucSchema requestStrucSchema;
    private List<StrucParameter> queryParams = new ArrayList<>();
    private List<StrucParameter> pathParams = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    public static class StrucParameter {
        private String name;
        private DataPropertyType type;
        private String format;
        private boolean required;
    }

}
