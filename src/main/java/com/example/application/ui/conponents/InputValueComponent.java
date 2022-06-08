package com.example.application.ui.conponents;

import com.example.application.data.structureModel.PropertyTypeEnum;
import com.vaadin.flow.component.AbstractField;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InputValueComponent {
    private String title;
    private AbstractField component;
    private PropertyTypeEnum propertyTypeEnum;
}
