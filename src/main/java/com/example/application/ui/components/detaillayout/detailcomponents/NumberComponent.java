package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.vaadin.flow.component.textfield.IntegerField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumberComponent extends DetailComponent {
    private final IntegerField integerField;

    public NumberComponent(String title) {
        super(title);
        integerField = new IntegerField(title);
        integerField.setReadOnly(true);
        integerField.setSizeFull();
        add(integerField);
    }

    public NumberComponent() {
        super("TODO");
        integerField = new IntegerField();
        integerField.setReadOnly(true);
        add(integerField);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        try {
            integerField.setValue(Integer.parseInt(dataValue.getPlainValue()));
        }catch(NumberFormatException e){
            integerField.setValue(-1);
        }
    }

    @Override
    public void clearDetailLayout() {
        integerField.clear();
    }
}
