package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucProperty;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.components.detaillayout.DetailSwitchListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ObjectComponent extends DetailComponent {

    private final Map<String, DetailComponent> detailLayoutComponents = new HashMap<>();
    @Getter
    private final String objectTitle;

    private final DetailSwitchListener detailSwitchListener;

    public ObjectComponent(String objectTitle, StrucSchema schema, DetailSwitchListener detailSwitchListener) {
        this.objectTitle = objectTitle;
        this.detailSwitchListener = detailSwitchListener;

        FormLayout formLayout = new FormLayout();

        schema.getProperties().keySet().forEach(key ->
                formLayout.add(createDetailComponent(schema.getProperties().get(key), key))
        );

        this.add(formLayout);
    }

    private Component createDetailComponent(StrucProperty strucProperty, String title) {
        if (strucProperty.getType().equals(PropertyTypeEnum.OBJECT)) {

            ObjectComponent objectComponent = new ObjectComponent(title, strucProperty.getSchema(), detailSwitchListener);
            detailLayoutComponents.put(title, objectComponent);
            Button objectButton = new Button(title);
            objectButton.addClickListener(e -> detailSwitchListener.switchToObject(this,title,objectComponent));
            return objectButton;
        }else if (strucProperty.getType().equals(PropertyTypeEnum.ARRAY)){
            ArrayComponent arrayComponent = new ArrayComponent(title, strucProperty.getArrayElements(), detailSwitchListener);
            detailLayoutComponents.put(title, arrayComponent);
            Button arrayButton = new Button("List of "+title);
            arrayButton.addClickListener(e -> detailSwitchListener.switchToArray(this,title,arrayComponent));
            return arrayButton;
        }else { //TODO Array
            DetailComponent detailComponent = switch (strucProperty.getType()) {
                case NUMBER -> new NumberComponent(title);
                case BOOLEAN -> new BooleanComponent(title);
                case STRING -> new TextComponent(title);
                default -> new TextComponent(title);
            };

            detailLayoutComponents.put(title, detailComponent);
            return detailComponent;

        }
    }

    public void fillDetailLayout(DataValue dataValue) { //TODO zuasmmenfassen
        dataValue.getProperties().keySet().forEach(key -> {
            if (detailLayoutComponents.get(key) != null) {
                switch (dataValue.getProperties().get(key).getValue().getPropertyTypeEnum()) {
                    case NUMBER ->
                            detailLayoutComponents.get(key).fillDetailLayout(dataValue.getProperties().get(key).getValue());
                    case BOOLEAN ->
                            detailLayoutComponents.get(key).fillDetailLayout(dataValue.getProperties().get(key).getValue());
                    case STRING ->
                            detailLayoutComponents.get(key).fillDetailLayout(dataValue.getProperties().get(key).getValue());
                    case OBJECT ->
                            detailLayoutComponents.get(key).fillDetailLayout(dataValue.getProperties().get(key).getValue());
                    case ARRAY ->
                            detailLayoutComponents.get(key).fillDetailLayout(dataValue.getProperties().get(key).getValue());
                    default ->
                            detailLayoutComponents.get(key).fillDetailLayout(dataValue.getProperties().get(key).getValue());
                }
            }
        });
    }

    public void clearDetailLayout() {
        detailLayoutComponents.values().forEach(DetailComponent::clearDetailLayout);
    }
}
