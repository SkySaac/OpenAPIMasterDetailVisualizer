package openapivisualizer.application.ui.components.createComponents;

import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;

public class IntegerfieldComponent extends CreateComponent {

    private final IntegerField integerField;

    public IntegerfieldComponent(String label){
        integerField = new IntegerField(label);
        this.add(integerField);
    }

    @Override
    public void setRequired(boolean required) {
        integerField.setRequiredIndicatorVisible(required);
    }

    @Override
    public boolean isRequired() {
        return integerField.isRequiredIndicatorVisible();
    }

    @Override
    public void setValue(String value) {
        integerField.setValue(Integer.parseInt(value));
    }

    @Override
    public String getValue() {
        return integerField.getValue().toString();
    }

    @Override
    public boolean isEmpty() {
        return integerField.isEmpty();
    }

    @Override
    public String getLabel() {
        return integerField.getLabel();
    }

    @Override
    public DataPropertyType getDataPropertyType() {
        return DataPropertyType.INTEGER;
    }
}
