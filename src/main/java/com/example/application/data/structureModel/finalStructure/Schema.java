package com.example.application.data.structureModel.finalStructure;

import com.example.application.data.structureModel.finalStructure.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Schema {
    private String name;
    private Map<String, Property> properties;
}
