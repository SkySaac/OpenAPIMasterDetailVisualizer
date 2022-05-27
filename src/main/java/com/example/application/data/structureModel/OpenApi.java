package com.example.application.data.structureModel;

import com.example.application.data.structureModel.finalStructure.Schema;
import com.example.application.data.structureModel.paths.Path;
import com.example.application.data.structureModel.tags.Tag;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpenApi {
    private Info info;
    private List<Server> servers = new ArrayList<>(); //TODO
    private List<Security> security = new ArrayList<>(); //TODO
    private List<Tag> tags = new ArrayList<>();
    private List<Path> paths = new ArrayList<>(); //TODO
    private List<Schema> schemas = new ArrayList<>(); //TODO


    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public class Security {
        private List<String> basicAuth;
        //TODO was das ?
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public class Server {
        private String url;
        private String description;
    }


}
