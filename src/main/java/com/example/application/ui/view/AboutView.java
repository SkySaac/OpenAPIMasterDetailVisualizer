package com.example.application.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;


public class AboutView extends VerticalLayout {

    public interface ActionListener{
        void action(String source);
    }

    private final ActionListener actionListener;

    public AboutView(ActionListener actionListener,String defaultSource) {
        this.actionListener = actionListener;
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        //TODO uploadButton

        TextField textField = new TextField("OpenAPI Doc");
        textField.setValue(defaultSource);
        add(textField);

        Button button = new Button("Test Me!");
        button.addClickListener(e -> actionListener.action(textField.getValue()));
        add(button);


        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}
