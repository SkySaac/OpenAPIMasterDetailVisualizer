package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.vaadin.flow.component.textfield.TextField;

public class TextComponent extends DetailComponent {

    private final TextField textField;

    public TextComponent(String label) {
        super(label);
        textField = new TextField(label);
        textField.setReadOnly(true);
        textField.setSizeFull();

        add(textField);
    }

    public TextComponent() {
        super("TODO");
        textField = new TextField();
        textField.setReadOnly(true);
        add(textField);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        //if(URLValidator.isValid(dataValue.getPlainValue()))
        //TODO if is application link (starting with server url and going to a path that we know -> has GET) or else just normal url
        textField.setValue(String.valueOf(dataValue.getPlainValue()));
    }

    @Override
    public void clearDetailLayout() {
        textField.clear();
    }
}
