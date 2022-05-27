package com.example.application.data.structureModel.paths;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PathMethod {
    private String summary;
    private String operationId;
    private List<QueryParameter> queryParameters = new ArrayList<>();
    private List<PathParameter> pathParameters = new ArrayList<>();
    private RequestBody requestBody; //TODO

    @AllArgsConstructor
    @Getter
    public class QueryParameter {
        private String key;
        private boolean required;
        private String schema; //TODO kann ja auch json oder so sein
    }

    @AllArgsConstructor
    @Getter
    public class PathParameter {
        private String key;
        private String schema;
    }

    public void addPathParameter(String key, String schema) {
        pathParameters.add(new PathParameter(key, schema));
    }

    public void addQuerryParameter(String key, boolean required, String schema) {
        queryParameters.add(new QueryParameter(key, required, schema));
    }
}
