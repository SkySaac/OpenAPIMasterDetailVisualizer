package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucProperty;
import com.example.application.ui.components.detaillayout.DetailSwitchListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

public class ArrayComponent extends DetailComponent {

    private final VerticalLayout verticalLayout;
    private final List<StrucProperty> arrayElements;
    private final DetailSwitchListener detailSwitchListener;

    public ArrayComponent(String label, List<StrucProperty> arrayElements, DetailSwitchListener detailSwitchListener) {
        this.arrayElements = arrayElements;
        this.detailSwitchListener = detailSwitchListener;

        Label titleLabel = new Label(label);

        verticalLayout = new VerticalLayout();

        add(titleLabel);
        add(verticalLayout);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        dataValue.getDataSchemas().forEach(dataSchemaElement -> {
            Component component = createDetailComponent(dataSchemaElement.getName(), dataSchemaElement);
            verticalLayout.add(component);
        });
    }

    private StrucProperty getArrayFittingStructure(String dataSchemaName) {
        //Get the StrucSchema of the Array Element that equals this dataValue
        List<StrucProperty> strucProperties = arrayElements.stream().filter(strucproperty
                -> strucproperty.getSchema().getName().equals(dataSchemaName)).collect(Collectors.toList());
        if (strucProperties.size() != 0) {
            log.warn("ArrayComponent has multiple elements with the same name: {} ", strucProperties);
        }
        return strucProperties.get(0);
    }

    private Component createDetailComponent(String title, DataSchema dataSchema) {
        if (dataSchema.getValue().getPropertyTypeEnum().equals(PropertyTypeEnum.OBJECT)) {
            StrucProperty strucProperty = getArrayFittingStructure(dataSchema.getName());

            ObjectComponent objectComponent = new ObjectComponent(title, strucProperty.getSchema(), detailSwitchListener);
            objectComponent.fillDetailLayout(dataSchema.getValue());
            Button objectButton = new Button(title);
            objectButton.addClickListener(e -> detailSwitchListener.switchToObject(this, title, objectComponent));
            return objectButton;
        } else if (dataSchema.getValue().getPropertyTypeEnum().equals(PropertyTypeEnum.ARRAY)) {
            StrucProperty strucProperty = getArrayFittingStructure(dataSchema.getName());

            ArrayComponent arrayComponent = new ArrayComponent(title, strucProperty.getArrayElements(), detailSwitchListener); //TODO
            arrayComponent.fillDetailLayout(dataSchema.getValue());
            Button arrayButton = new Button("List of " + title);
            arrayButton.addClickListener(e -> detailSwitchListener.switchToArray(this, title, arrayComponent));
            return arrayButton;
        } else {
            DetailComponent detailComponent = switch (dataSchema.getValue().getPropertyTypeEnum()) {
                case NUMBER -> new NumberComponent();
                case BOOLEAN -> new BooleanComponent();
                case STRING -> new TextComponent();
                default -> new TextComponent();
            };
            detailComponent.fillDetailLayout(dataSchema.getValue());
            return detailComponent;
        }
    }

    @Override
    public void clearDetailLayout() {
        //elements.forEach(e -> e.clearDetailLayout()); //TODO nötig ?
        verticalLayout.removeAll();
    }
}
