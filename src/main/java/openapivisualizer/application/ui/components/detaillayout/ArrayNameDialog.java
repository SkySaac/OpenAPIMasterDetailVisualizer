package openapivisualizer.application.ui.components.detaillayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class ArrayNameDialog extends Dialog {

    public interface ArrayNameDialogListener {
        void setArrayName(String arrayName);
    }

    public ArrayNameDialog(ArrayNameDialogListener arrayNameDialogListener) {

        TextField textField = new TextField("Attribute to use for the name decision");
        add(textField);

        Button save = new Button("Save");
        save.addClickListener(
                e -> {
                    if (!textField.isEmpty()) {
                        arrayNameDialogListener.setArrayName(textField.getValue());
                        this.close();
                    }
                }
        );
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> this.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        add(buttonLayout);

    }


}
