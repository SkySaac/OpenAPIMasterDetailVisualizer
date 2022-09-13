package openapivisualizer.application.ui.components.createComponents;

import com.vaadin.flow.component.textfield.TextField;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;

import java.util.Objects;

public class TextfieldComponent extends CreateComponent {

    private final TextField textField;

    public TextfieldComponent(String label, String format) {
        if (Objects.equals(label, "noName"))
            textField = new TextField();
        else {
            textField = new TextField(label);
        }
        if (format != null)
            textField.setPlaceholder(format);

        this.add(textField);
    }

    @Override
    public void setRequired(boolean required) {
        textField.setRequired(required);
    }

    @Override
    public boolean isRequired() {
        return textField.isRequired();
    }

    @Override
    public void setValue(String value) {
        textField.setValue(value);
    }

    @Override
    public String getValue() {
        return textField.getValue();
    }

    @Override
    public boolean isEmpty() {
        return textField.isEmpty();
    }

    @Override
    public String getLabel() {
        return textField.getLabel();
    }

    @Override
    public DataPropertyType getDataPropertyType() {
        return DataPropertyType.STRING;
    }
}
