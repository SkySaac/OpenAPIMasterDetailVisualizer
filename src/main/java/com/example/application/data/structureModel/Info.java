package com.example.application.data.structureModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonIgnoreProperties(ignoreUnknown=true)
@NoArgsConstructor
@AllArgsConstructor
public class Info {
    private String title;
    private String description;
    private Contact contact;
    private License license;
    private String version;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown=true)
    @NoArgsConstructor
    @AllArgsConstructor
    public class Contact{
        private String name;
        private String url;
        private String email;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown=true)
    @NoArgsConstructor
    @AllArgsConstructor
    public class License{
        private String name;
        private String url;
    }
}
