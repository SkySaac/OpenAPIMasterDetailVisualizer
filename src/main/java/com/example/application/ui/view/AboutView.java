package com.example.application.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class AboutView extends VerticalLayout {

    public interface ActionListener{
        void action();
    }

    private final ActionListener actionListener;

    public AboutView(ActionListener actionListener) {
        this.actionListener = actionListener;
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        add(new H2("This place intentionally left empty"));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
        Button button = new Button("Test Me!");
        button.addClickListener(e -> actionListener.action());
        add(button);


        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}
