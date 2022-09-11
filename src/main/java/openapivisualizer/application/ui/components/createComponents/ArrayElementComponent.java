package openapivisualizer.application.ui.components.createComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;

public class ArrayElementComponent extends HorizontalLayout {
    public interface ArrayElementActionListener {
        void delete(ArrayElementComponent arrayElementComponent);
    }

    @Getter
    private final CreateComponent createComponent;

    public ArrayElementComponent(ArrayElementActionListener arrayElementActionListener, CreateComponent createComponent) {
        this.createComponent = createComponent;

        this.setAlignItems(Alignment.CENTER);
        this.setPadding(false);

        add(createComponent);

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> arrayElementActionListener.delete(this));
        add(deleteButton);
    }

    public String getValue() {
        return createComponent.getValue();
    }
}
