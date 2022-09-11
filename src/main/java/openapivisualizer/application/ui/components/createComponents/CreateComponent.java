package openapivisualizer.application.ui.components.createComponents;

import com.vaadin.flow.component.html.Div;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;

public abstract class CreateComponent extends Div {
    public abstract void setRequired(boolean required);

    public abstract boolean isRequired();

    public abstract void setValue(String value);

    public abstract String getValue();

    public abstract boolean isEmpty();

    public abstract String getLabel();

    public abstract DataPropertyType getDataPropertyType();
}
