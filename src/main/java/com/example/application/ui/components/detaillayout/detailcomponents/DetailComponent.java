package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;

public abstract class DetailComponent extends Div {
    @Getter
    private String componentTitle;

    public DetailComponent(String componentTitle) {
        this.componentTitle = componentTitle;
    }

    public abstract void fillDetailLayout(DataValue dataValue);

    public abstract void clearDetailLayout();
}
