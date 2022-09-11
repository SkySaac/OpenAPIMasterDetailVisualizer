package openapivisualizer.application.ui.components.createComponents;

import com.vaadin.flow.component.checkbox.Checkbox;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;

public class CheckboxComponent extends CreateComponent {

    private final Checkbox checkbox;

    public CheckboxComponent(String label) {
        checkbox = new Checkbox(label);
        this.add(checkbox);
    }

    @Override
    public void setRequired(boolean required) {
        checkbox.setRequiredIndicatorVisible(required);
    }

    @Override
    public boolean isRequired() {
        return checkbox.isRequiredIndicatorVisible();
    }

    @Override
    public void setValue(String value) {
        checkbox.setValue(Boolean.valueOf(value));
    }

    @Override
    public String getValue() {
        return checkbox.getValue().toString();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String getLabel() {
        return checkbox.getLabel();
    }

    @Override
    public DataPropertyType getDataPropertyType() {
        return DataPropertyType.BOOLEAN;
    }
}
