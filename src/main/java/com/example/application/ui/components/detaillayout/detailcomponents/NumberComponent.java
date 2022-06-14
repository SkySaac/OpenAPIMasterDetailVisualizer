package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.vaadin.flow.component.textfield.NumberField;

public class NumberComponent extends DetailComponent {
    private final NumberField numberField;

    public NumberComponent(String label) {
        numberField = new NumberField(label);
        numberField.setReadOnly(true);
        add(numberField);
    }

    public NumberComponent(){
        numberField = new NumberField();
        numberField.setReadOnly(true);
        add(numberField);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        numberField.setValue(Double.parseDouble(dataValue.getPlainValue()));
    }

    @Override
    public void clearDetailLayout() {
        numberField.clear();
    }
}
