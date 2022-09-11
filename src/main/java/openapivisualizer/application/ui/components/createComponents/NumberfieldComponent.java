package openapivisualizer.application.ui.components.createComponents;

import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;

public class NumberfieldComponent extends CreateComponent {

    private final NumberField numberField;

    public NumberfieldComponent(String label){
        numberField = new NumberField(label);
        this.add(numberField);
    }

    @Override
    public void setRequired(boolean required) {
        numberField.setRequiredIndicatorVisible(required);
    }

    @Override
    public boolean isRequired() {
        return numberField.isRequiredIndicatorVisible();
    }

    @Override
    public void setValue(String value) {
        numberField.setValue(Double.parseDouble(value));
    }

    @Override
    public String getValue() {
        return numberField.getValue().toString();
    }

    @Override
    public boolean isEmpty() {
        return numberField.isEmpty();
    }

    @Override
    public String getLabel() {
        return numberField.getLabel();
    }

    @Override
    public DataPropertyType getDataPropertyType() {
        return DataPropertyType.DOUBLE;
    }
}
