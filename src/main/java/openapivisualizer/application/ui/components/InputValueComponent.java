package openapivisualizer.application.ui.components;

import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import com.vaadin.flow.component.AbstractField;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InputValueComponent {
    private String title;
    private AbstractField component;
    private DataPropertyType dataPropertyType;
}
