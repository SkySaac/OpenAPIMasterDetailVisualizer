package com.example.application.ui.view;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class NotFoundView extends VerticalLayout {
    public NotFoundView(){
        add(new Span("404 - Page not found"));
        setSizeFull();
    }
}
