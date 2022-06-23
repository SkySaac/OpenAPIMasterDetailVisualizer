package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.components.detaillayout.DetailSwitchListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j

public class ArrayComponent extends DetailComponent {

    private final VerticalLayout verticalLayout;
    private final List<StrucSchema> arrayElements;
    private final DetailSwitchListener detailSwitchListener;

    public ArrayComponent(String label, List<StrucSchema> arrayElements, DetailSwitchListener detailSwitchListener) {
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

    private StrucSchema getArrayFittingStructure(DataSchema dataSchema) {
        //Get the StrucSchema of the Array Element that equals this dataValue since an array can have multiple things inside

        List<StrucSchema> strucProperties = arrayElements.stream().filter(strucProperty
                -> isSameSchema(dataSchema, strucProperty)).collect(Collectors.toList());

        if (strucProperties.size() > 1) {
            log.warn("ArrayComponent has multiple elements with the same name: {} ", strucProperties);
        }
        return strucProperties.get(0);
    }

    private boolean isSameSchema(DataSchema dataSchema, StrucSchema strucSchema) {
        if (dataSchema.getValue().getPropertyTypeEnum().equals(PropertyTypeEnum.OBJECT)
                && strucSchema.getStrucValue().getType().equals(PropertyTypeEnum.OBJECT)) {
            return dataSchema.getValue().getProperties().keySet().stream()
                    .allMatch(propertyName -> strucSchema.getStrucValue().getProperties().containsKey(propertyName)); //TODO and is obj
        } else if (dataSchema.getValue().getPropertyTypeEnum().equals(PropertyTypeEnum.ARRAY)) {
            log.warn("Unsafe comparison of strucSchema and dataSchema for {}",strucSchema.getName());
            return strucSchema.getStrucValue().getType().equals(PropertyTypeEnum.ARRAY); //TODO: How do you compare and array if the type equals another array ?
        } else {
            return strucSchema.getStrucValue().getType().equals(dataSchema.getValue().getPropertyTypeEnum());
        }
    }

    private Component createDetailComponent(String title, DataSchema dataSchema) {
        if (dataSchema.getValue().getPropertyTypeEnum().equals(PropertyTypeEnum.OBJECT)) {
            StrucSchema schema = getArrayFittingStructure(dataSchema);

            ObjectComponent objectComponent = new ObjectComponent(title, schema, detailSwitchListener);
            objectComponent.fillDetailLayout(dataSchema.getValue());

            Button objectButton = new Button(title);
            objectButton.addClickListener(e -> detailSwitchListener.switchToObject(this, title, objectComponent));
            return objectButton;
        } else if (dataSchema.getValue().getPropertyTypeEnum().equals(PropertyTypeEnum.ARRAY)) {
            StrucSchema schema = getArrayFittingStructure(dataSchema);

            ArrayComponent arrayComponent = new ArrayComponent(title, schema.getStrucValue().getArrayElements(), detailSwitchListener); //TODO
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
        verticalLayout.removeAll();
    }
}
