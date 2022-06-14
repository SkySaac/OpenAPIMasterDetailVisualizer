package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.vaadin.flow.component.checkbox.Checkbox;

public class BooleanComponent extends DetailComponent {

    private final Checkbox checkbox;

    public BooleanComponent(String label) {
        checkbox = new Checkbox(label);
        checkbox.setReadOnly(true);
        add(checkbox);
    }public BooleanComponent() {
        checkbox = new Checkbox();
        checkbox.setReadOnly(true);
        add(checkbox);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        checkbox.setValue(Boolean.parseBoolean(dataValue.getPlainValue()));
    }

    @Override
    public void clearDetailLayout() {
        checkbox.clear();
    }
}
