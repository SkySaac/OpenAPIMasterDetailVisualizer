package openapivisualizer.application.ui.components.detaillayout.detailcomponents;

import openapivisualizer.application.rest.client.restdatamodel.DataValue;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;

public abstract class DetailComponent extends Div {
    @Getter
    private final String componentTitle;

    public DetailComponent(String componentTitle) {
        this.componentTitle = componentTitle;
    }

    public abstract void fillDetailLayout(DataValue dataValue);

    public abstract void clearDetailLayout();
}
