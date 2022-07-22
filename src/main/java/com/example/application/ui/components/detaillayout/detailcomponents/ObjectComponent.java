package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.components.detaillayout.DetailSwitchListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectComponent extends DetailComponent {

    private final Map<String, DetailComponent> detailLayoutComponents = new HashMap<>();
    @Getter
    private final String objectTitle;

    private final DetailSwitchListener detailSwitchListener;
    private final StrucSchema additionalSchema;

    private final List<Component> additionalComponents = new ArrayList<>();

    private final FormLayout formLayout;

    public ObjectComponent(String objectTitle, StrucSchema schema, DetailSwitchListener detailSwitchListener) {
        super(objectTitle);
        this.objectTitle = objectTitle;
        this.detailSwitchListener = detailSwitchListener;
        this.additionalSchema = schema.getStrucValue().getAdditionalPropertySchema();

        formLayout = new FormLayout();

        schema.getStrucValue().getProperties().keySet().forEach(key ->
                formLayout.add(createDetailComponent(schema.getStrucValue().getProperties().get(key), key))
        );

        this.add(formLayout);
    }

    private Component createDetailComponent(StrucSchema strucSchema, String title) {
        if (strucSchema.getStrucValue().getType().equals(PropertyTypeEnum.OBJECT)) {

            ObjectComponent objectComponent = new ObjectComponent(title, strucSchema, detailSwitchListener);
            detailLayoutComponents.put(title, objectComponent);
            Button objectButton = new Button(title);
            objectButton.addClickListener(e -> detailSwitchListener.switchToObject(this, title, objectComponent));
            return objectButton;
        } else if (strucSchema.getStrucValue().getType().equals(PropertyTypeEnum.ARRAY)) {
            ArrayComponent arrayComponent = new ArrayComponent(title, strucSchema.getStrucValue().getArrayElements(), detailSwitchListener);
            detailLayoutComponents.put(title, arrayComponent);
            Button arrayButton = new Button("List of " + title);
            arrayButton.addClickListener(e -> detailSwitchListener.switchToArray(this, title, arrayComponent));
            return arrayButton;
        } else {
            DetailComponent detailComponent = switch (strucSchema.getStrucValue().getType()) {
                case NUMBER -> new NumberComponent(title);
                case BOOLEAN -> new BooleanComponent(title);
                default -> new TextComponent(title);
            };

            detailLayoutComponents.put(title, detailComponent);

            return detailComponent;

        }
    }

    public void fillDetailLayout(DataValue dataValue) {
        List<String> unusedProperties = new ArrayList<>(dataValue.getProperties().keySet());
        dataValue.getProperties().keySet().forEach(key -> {
            if (detailLayoutComponents.get(key) != null) {
                detailLayoutComponents.get(key).fillDetailLayout(dataValue.get(key).getValue());
                unusedProperties.remove(key);
            }
        });

        createAdditionalPropertyComponents(dataValue, unusedProperties);
    }

    private void createAdditionalPropertyComponents(DataValue dataValue, List<String> unusedProperties) {
        if (additionalSchema != null) {
            //TODO check if dataValue.getProperties().get(key).getValue() has same type/properties or so as the additional one
            //TODO if not -> remove it
            unusedProperties.forEach(unusedProperty -> {
                DetailComponent detailComponent = createAdditionalDetailComponent(additionalSchema, unusedProperty);
                detailComponent.fillDetailLayout(dataValue.get(unusedProperty).getValue());
            });
        }
    }

    private DetailComponent createAdditionalDetailComponent(StrucSchema strucSchema, String title) {
        if (strucSchema.getStrucValue().getType().equals(PropertyTypeEnum.OBJECT)) {

            ObjectComponent objectComponent = new ObjectComponent(title, strucSchema, detailSwitchListener);
            detailLayoutComponents.put(title, objectComponent);

            Button objectButton = new Button(title);
            objectButton.addClickListener(e -> detailSwitchListener.switchToObject(this, title, objectComponent));
            formLayout.add(objectButton);
            return objectComponent;
        } else if (strucSchema.getStrucValue().getType().equals(PropertyTypeEnum.ARRAY)) {

            ArrayComponent arrayComponent = new ArrayComponent(title, strucSchema.getStrucValue().getArrayElements(), detailSwitchListener);
            detailLayoutComponents.put(title, arrayComponent);

            Button arrayButton = new Button("List of " + title);
            arrayButton.addClickListener(e -> detailSwitchListener.switchToArray(this, title, arrayComponent));
            formLayout.add(arrayButton);
            return arrayComponent;
        } else {
            DetailComponent detailComponent = switch (strucSchema.getStrucValue().getType()) {
                case NUMBER -> new NumberComponent(title);
                case BOOLEAN -> new BooleanComponent(title);
                default -> new TextComponent(title);
            };

            detailLayoutComponents.put(title, detailComponent);
            formLayout.add(detailComponent);
            return detailComponent;

        }
    }

    public void clearDetailLayout() {
        detailLayoutComponents.values().forEach(DetailComponent::clearDetailLayout);
        additionalComponents.forEach(formLayout::remove);
        additionalComponents.clear();
    }
}
