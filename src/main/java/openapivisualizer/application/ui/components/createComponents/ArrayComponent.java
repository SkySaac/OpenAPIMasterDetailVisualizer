package openapivisualizer.application.ui.components.createComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;

import java.util.ArrayList;
import java.util.List;

public class ArrayComponent extends CreateComponent implements ArrayElementComponent.ArrayElementActionListener {

    private final List<CreateComponent> arrayElements = new ArrayList<>();
    private final VerticalLayout verticalLayout;
    private final String label;

    private final CreateComponent basicCreateComponent;

    public ArrayComponent(String label, DataPropertyType type, String format) {
        this.label = label;
        verticalLayout = new VerticalLayout(new Label(label));

        basicCreateComponent = createEditorComponent(type, format, label);

        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addArrayElement(basicCreateComponent.getValue(), type));
        HorizontalLayout mainComponent = new HorizontalLayout(basicCreateComponent, addButton);
        mainComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.add(mainComponent);
        this.add(verticalLayout);
    }

    private void addArrayElement(String value, DataPropertyType dataPropertyType) {
        CreateComponent createComponent = new TextfieldComponent("", null);
        createComponent.setValue(value);
        ArrayElementComponent arrayElementComponent = new ArrayElementComponent(this, createComponent);
        arrayElements.add(createComponent);
        verticalLayout.add(arrayElementComponent);
        basicCreateComponent.setValue("");
    }

    private CreateComponent createEditorComponent(DataPropertyType type, String format, String title) {
        CreateComponent inputComponent;
        switch (type) {
            case INTEGER -> inputComponent = new IntegerfieldComponent(title);
            case DOUBLE -> inputComponent = new NumberfieldComponent(title);
            case BOOLEAN -> inputComponent = new CheckboxComponent(title);
            default -> inputComponent = new TextfieldComponent(title, format);

        } //TODO Objekte & Arrays


        return inputComponent;
    }

    @Override
    public void setRequired(boolean required) {

    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public String getValue() {
        String[] value = {"["};
        arrayElements.forEach(arrayElement -> value[0]+=arrayElement.getValue()+",");
        if(value[0].lastIndexOf(",")==value[0].length()-1)
            value[0] = value[0].substring(0,value[0].length()-1);
        value[0]+="]";
        return value[0];
    }

    @Override
    public boolean isEmpty() {
        return arrayElements.isEmpty();

    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public DataPropertyType getDataPropertyType() {
        return DataPropertyType.ARRAY;
    }

    @Override
    public void delete(ArrayElementComponent arrayElementComponent) {
        arrayElements.remove(arrayElementComponent);
        verticalLayout.remove(arrayElementComponent);
    }


}


