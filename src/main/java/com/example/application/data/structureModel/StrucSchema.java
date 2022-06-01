package com.example.application.data.structureModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StrucSchema {
    @Setter
    private String name;
    private Map<String, StrucProperty> properties = new HashMap<>();

    @Setter
    private boolean isFreeSchema = false;

    public void addProperty(String key,StrucProperty property){
        properties.put(key,property);
    }
}
