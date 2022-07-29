package com.example.application.ui.route;

import com.example.application.ui.MainLayout;
import com.example.application.ui.presenter.AboutPresenter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;

@Route(value = "404", layout = MainLayout.class)
public class NotFoundRoute extends Div {

    public NotFoundRoute() {
        add(new Span("404 - Page not found"));
        setSizeFull();
    }
}