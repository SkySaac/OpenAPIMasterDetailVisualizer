package openapivisualizer.application.ui.components.detaillayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucSchema;

import java.util.ArrayList;
import java.util.List;

public class ArrayNameDialog extends Dialog {

    public interface ArrayNameDialogListener {
        void setArrayName(String arrayName);
    }

    private final Select<String> select = new Select<>();
    private final Label noSelectionLabel = new Label("Nothing to select");

    public ArrayNameDialog(ArrayNameDialogListener arrayNameDialogListener) {

        select.setLabel("Select the attribute for the array elements");
        add(select);
        add(noSelectionLabel);

        Button save = new Button("Save");
        save.addClickListener(
                e -> {
                    if (!select.isEmpty()) {
                        arrayNameDialogListener.setArrayName(select.getValue());
                        this.close();
                    }
                }
        );
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> this.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        add(buttonLayout);

    }

    public void open(List<StrucSchema> arrayElements){
        this.open();
        noSelectionLabel.setVisible(false);
        if(arrayElements.get(0).getStrucValue().getType().equals(DataPropertyType.OBJECT))
            select.setItems(arrayElements.get(0).getStrucValue().getProperties().keySet());
        else {
            select.setItems(new ArrayList<>());
            noSelectionLabel.setVisible(true);
        }

    }


}
