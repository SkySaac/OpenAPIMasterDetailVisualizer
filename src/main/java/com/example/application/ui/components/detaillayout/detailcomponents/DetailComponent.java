package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.vaadin.flow.component.html.Div;

public abstract class DetailComponent extends Div {

    public abstract void fillDetailLayout(DataValue dataValue);
    public abstract void clearDetailLayout();
}
