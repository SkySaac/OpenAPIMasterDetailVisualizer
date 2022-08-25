package openapivisualizer.application.ui.components.detaillayout.detailcomponents;

import openapivisualizer.application.rest.client.restdatamodel.DataValue;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.ui.components.detaillayout.DetailSwitchListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        if (schema == null)
            System.out.println("hi");
        this.additionalSchema = schema.getStrucValue().getAdditionalPropertySchema();

        formLayout = new FormLayout();

        schema.getStrucValue().getProperties().keySet().forEach(key ->
                formLayout.add(createDetailComponent(schema.getStrucValue().getProperties().get(key), key))
        );

        this.add(formLayout);
    }

    private Component createDetailComponent(StrucSchema strucSchema, String title) {
        if (strucSchema.getStrucValue().getType().equals(DataPropertyType.OBJECT)) {

            ObjectComponent objectComponent = new ObjectComponent(title, strucSchema, detailSwitchListener);
            detailLayoutComponents.put(title, objectComponent);
            Button objectButton = new Button(title);
            objectButton.addClickListener(e -> detailSwitchListener.switchToObject(this, title, objectComponent));
            return objectButton;
        } else if (strucSchema.getStrucValue().getType().equals(DataPropertyType.ARRAY)) {
            ArrayComponent arrayComponent = new ArrayComponent(title, strucSchema.getStrucValue().getArrayElements(), detailSwitchListener);
            detailLayoutComponents.put(title, arrayComponent);
            Button arrayButton = new Button("List of " + title);
            arrayButton.addClickListener(e -> detailSwitchListener.switchToArray(this, title, arrayComponent));
            return arrayButton;
        } else {
            DetailComponent detailComponent = switch (strucSchema.getStrucValue().getType()) {
                case INTEGER -> new NumberComponent(title);
                case DOUBLE -> new DoubleComponent(title);
                case BOOLEAN -> new BooleanComponent(title);
                default -> new TextComponent(detailSwitchListener, title);
            };

            detailLayoutComponents.put(title, detailComponent);

            return detailComponent;

        }
    }

    public void fillDetailLayout(DataValue dataValue) {
        Map<String, DataPropertyType> unusedProperties = dataValue.getProperties().entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().getValue().getDataPropertyType()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        dataValue.getProperties().keySet().forEach(key -> {
            if (detailLayoutComponents.containsKey(key)) {
                detailLayoutComponents.get(key).fillDetailLayout(dataValue.get(key).getValue());
                unusedProperties.remove(key);
            }
        });

        createAdditionalPropertyComponents(dataValue, unusedProperties);
    }

    private void createAdditionalPropertyComponents(DataValue dataValue, Map<String, DataPropertyType> unusedProperties) {
        if (additionalSchema != null) {
            unusedProperties.forEach((key, value) -> {
                if (value.equals(additionalSchema.getStrucValue().getType())) {
                    DetailComponent detailComponent = createAdditionalDetailComponent(additionalSchema, key);
                    detailComponent.fillDetailLayout(dataValue.get(key).getValue());
                } else {
                    log.info("Not usable property in return found: {}", key);
                }
            });
        }
    }

    private DetailComponent createAdditionalDetailComponent(StrucSchema additionalSchema, String title) {
        if (additionalSchema.getStrucValue().getType().equals(DataPropertyType.OBJECT)) {

            ObjectComponent objectComponent = new ObjectComponent(title, additionalSchema, detailSwitchListener);
            detailLayoutComponents.put(title, objectComponent);

            Button objectButton = new Button(title);
            objectButton.addClickListener(e -> detailSwitchListener.switchToObject(this, title, objectComponent));
            formLayout.add(objectButton);
            return objectComponent;
        } else if (additionalSchema.getStrucValue().getType().equals(DataPropertyType.ARRAY)) {

            ArrayComponent arrayComponent = new ArrayComponent(title, additionalSchema.getStrucValue().getArrayElements(), detailSwitchListener);
            detailLayoutComponents.put(title, arrayComponent);

            Button arrayButton = new Button("List of " + title);
            arrayButton.addClickListener(e -> detailSwitchListener.switchToArray(this, title, arrayComponent));
            formLayout.add(arrayButton);
            return arrayComponent;
        } else {
            DetailComponent detailComponent = switch (additionalSchema.getStrucValue().getType()) {
                case INTEGER -> new NumberComponent(title);
                case DOUBLE -> new DoubleComponent(title);
                case BOOLEAN -> new BooleanComponent(title);
                default -> new TextComponent(detailSwitchListener, title);
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
