package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.vaadin.flow.component.textfield.NumberField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DoubleComponent extends DetailComponent {
    private final NumberField numberField;

    public DoubleComponent(String title) {
        super(title);
        numberField = new NumberField(title);
        numberField.setReadOnly(true);
        numberField.setSizeFull();
        add(numberField);
    }

    public DoubleComponent() {
        super("TODO");
        numberField = new NumberField();
        numberField.setReadOnly(true);
        add(numberField);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        try {
            numberField.setValue(Double.parseDouble(dataValue.getPlainValue()));
        }catch(NumberFormatException e){
            numberField.setValue(Double.NaN);
        }
    }

    @Override
    public void clearDetailLayout() {
        numberField.clear();
    }
}
