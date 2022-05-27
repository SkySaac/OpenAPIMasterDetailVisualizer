package com.example.application.data.dataModel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class DataSchema {
    private String tagName; //TODO brauchen wir hier ?

    private Map<String,String> properties;
}
