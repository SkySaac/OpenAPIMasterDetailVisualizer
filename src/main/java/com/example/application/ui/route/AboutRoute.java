package com.example.application.ui.route;

import com.example.application.ui.MainLayout;
import com.example.application.ui.presenter.AboutPresenter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import javax.annotation.PostConstruct;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class AboutRoute extends Div {
    private final AboutPresenter presenter;

    public AboutRoute(AboutPresenter presenter) {
        this.presenter = presenter;
        setSizeFull();
    }

    @PostConstruct
    public void init() {
        add(presenter.getView());
    }
}
