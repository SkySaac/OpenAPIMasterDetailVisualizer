package openapivisualizer.application.ui.components.detaillayout.detailcomponents;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.rest.client.restdatamodel.DataValue;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.ui.components.ExtractionSettingsDialog;
import openapivisualizer.application.ui.components.detaillayout.ArrayNameDialog;
import openapivisualizer.application.ui.components.detaillayout.DetailSwitchListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j

public class ArrayComponent extends DetailComponent implements ArrayNameDialog.ArrayNameDialogListener {

    private final VerticalLayout verticalLayout = new VerticalLayout();
    private final List<StrucSchema> arrayElements;
    private final DetailSwitchListener detailSwitchListener;
    private final ArrayNameDialog arrayNameDialog = new ArrayNameDialog(this);

    private DataValue currentDataValue;
    private String currentArrayNameSetter;

    public ArrayComponent(String title, List<StrucSchema> arrayElements, DetailSwitchListener detailSwitchListener) {
        super(title);
        this.arrayElements = arrayElements;
        this.detailSwitchListener = detailSwitchListener;


        Label titleLabel = new Label(title);
        titleLabel.getStyle().set("font-size", "large");
        Button settingsButton = new Button(VaadinIcon.SLIDERS.create());
        settingsButton.addClickListener(e -> arrayNameDialog.open());
        HorizontalLayout topbar = new HorizontalLayout(titleLabel, settingsButton);
        topbar.setAlignItems(FlexComponent.Alignment.CENTER);

        add(topbar, verticalLayout);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        this.currentDataValue = dataValue;
        dataValue.getDataSchemas().forEach(dataSchemaElement -> {
            Component component = createDetailComponent(dataSchemaElement);
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
        if (strucProperties.size() == 0) {
            log.error("The returned objects structure is different from the structure defined in the openapi document");
        }
        return strucProperties.get(0);
    }

    private boolean isSameSchema(DataSchema dataSchema, StrucSchema strucSchema) {
        if (dataSchema.getValue().getDataPropertyType().equals(DataPropertyType.OBJECT)
                && strucSchema.getStrucValue().getType().equals(DataPropertyType.OBJECT)) {
            return dataSchema.getValue().getProperties().keySet().stream()
                    .allMatch(propertyName -> strucSchema.getStrucValue().getProperties().containsKey(propertyName)); //TODO and is obj
        } else if (dataSchema.getValue().getDataPropertyType().equals(DataPropertyType.ARRAY)) {
            log.warn("Unsafe comparison of strucSchema and dataSchema for {}", strucSchema.getName());
            return strucSchema.getStrucValue().getType().equals(DataPropertyType.ARRAY); //TODO: How do you compare and array if the type equals another array ?
        } else {
            return strucSchema.getStrucValue().getType().equals(dataSchema.getValue().getDataPropertyType());
        }
    }

    private Component createDetailComponent(DataSchema dataSchema) {
        String title;
        if(dataSchema.getValue().getProperties().containsKey(currentArrayNameSetter))
            title = dataSchema.getValue().getProperties().get(currentArrayNameSetter).getValue().getPlainValue();
        else
            title = dataSchema.getName(); //this.getComponentTitle();
        if (dataSchema.getValue().getDataPropertyType().equals(DataPropertyType.OBJECT)) {
            StrucSchema schema = getArrayFittingStructure(dataSchema);

            ObjectComponent objectComponent = new ObjectComponent(title, schema, detailSwitchListener);
            objectComponent.fillDetailLayout(dataSchema.getValue());

            Button objectButton = new Button(title);
            objectButton.addClickListener(e -> detailSwitchListener.switchToObject(this, title, objectComponent));
            return objectButton;
        } else if (dataSchema.getValue().getDataPropertyType().equals(DataPropertyType.ARRAY)) {
            StrucSchema schema = getArrayFittingStructure(dataSchema);

            ArrayComponent arrayComponent = new ArrayComponent(title, schema.getStrucValue().getArrayElements(), detailSwitchListener); //TODO
            arrayComponent.fillDetailLayout(dataSchema.getValue());
            Button arrayButton = new Button("List of " + title);
            arrayButton.addClickListener(e -> detailSwitchListener.switchToArray(this, title, arrayComponent));
            return arrayButton;
        } else {
            DetailComponent detailComponent = switch (dataSchema.getValue().getDataPropertyType()) {
                case INTEGER -> new NumberComponent();
                case DOUBLE -> new DoubleComponent();
                case BOOLEAN -> new BooleanComponent();
                default -> new TextComponent(detailSwitchListener);
            };
            detailComponent.fillDetailLayout(dataSchema.getValue());
            detailComponent.setSizeFull();
            return detailComponent;
        }
    }

    @Override
    public void clearDetailLayout() {
        verticalLayout.removeAll();
    }

    @Override
    public void setArrayName(String arrayName) {
        clearDetailLayout();
        this.currentArrayNameSetter = arrayName;
        if (currentDataValue != null)
            fillDetailLayout(currentDataValue);
    }
}
