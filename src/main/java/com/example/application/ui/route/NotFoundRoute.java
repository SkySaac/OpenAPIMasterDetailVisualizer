package com.example.application.ui.route;

import com.example.application.ui.MainLayout;
import com.example.application.ui.presenter.AboutPresenter;
import com.example.application.ui.presenter.NotFoundPresenter;
import com.example.application.ui.presenter.TagPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;

import javax.annotation.PostConstruct;

@Route(value = "404", layout = MainLayout.class)
public class NotFoundRoute extends Div {

    private final NotFoundPresenter presenter;
    public NotFoundRoute(NotFoundPresenter presenter) {
        this.presenter = presenter;
        setSizeFull();
    }

    @PostConstruct
    public void init() {
        add(presenter.getView());
    }
}