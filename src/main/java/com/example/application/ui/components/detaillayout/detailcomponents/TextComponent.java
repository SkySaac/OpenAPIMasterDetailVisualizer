package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.vaadin.flow.component.textfield.TextField;

public class TextComponent extends DetailComponent {

    private final TextField textField;

    public TextComponent(String label) {
        textField = new TextField(label);
        textField.setReadOnly(true);
        add(textField);
    }
    public TextComponent() {
        textField = new TextField();
        textField.setReadOnly(true);
        add(textField);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        textField.setValue(String.valueOf(dataValue.getPlainValue()));
    }

    @Override
    public void clearDetailLayout() {
        textField.clear();
    }
}
