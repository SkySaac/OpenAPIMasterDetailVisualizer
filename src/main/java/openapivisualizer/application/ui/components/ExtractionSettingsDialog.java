package openapivisualizer.application.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ExtractionSettingsDialog extends Dialog {

    public interface ActionListener {
        void save(boolean onlyListViews, boolean showAllPaths);
    }

    private final Checkbox onlyListViewCheckbox = new Checkbox("Extract only List Views");
    private final Checkbox showAllPathsListView = new Checkbox("Show all paths in List Views");

    public ExtractionSettingsDialog(ActionListener actionListener) {

        VerticalLayout content = new VerticalLayout(onlyListViewCheckbox,showAllPathsListView);
        add(content);

        Button save = new Button("Save");
        save.addClickListener(event -> {
            actionListener.save(onlyListViewCheckbox.getValue(), showAllPathsListView.getValue());
            this.close();
        });
        Button cancel = new Button("Cancel");
        cancel.addClickListener(event -> this.close());
        HorizontalLayout horizontalLayout = new HorizontalLayout(save, cancel);
        add(horizontalLayout);

    }

    public void open(boolean onlyListViews, boolean showAllPaths) {
        onlyListViewCheckbox.setValue(onlyListViews);
        showAllPathsListView.setValue(showAllPaths);
        this.open();
    }
}
