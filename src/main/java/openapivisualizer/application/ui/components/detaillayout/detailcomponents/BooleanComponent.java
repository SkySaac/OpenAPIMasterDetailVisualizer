package openapivisualizer.application.ui.components.detaillayout.detailcomponents;

import openapivisualizer.application.rest.client.restdatamodel.DataValue;
import com.vaadin.flow.component.checkbox.Checkbox;

public class BooleanComponent extends DetailComponent {

    private final Checkbox checkbox;

    public BooleanComponent(String title) {
        super(title);
        checkbox = new Checkbox(title);
        checkbox.setReadOnly(true);
        checkbox.setSizeFull();
        add(checkbox);
    }

    public BooleanComponent() {
        super("TODO");
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
