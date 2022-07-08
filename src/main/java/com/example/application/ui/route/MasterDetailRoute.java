package com.example.application.ui.route;

import com.example.application.ui.MainLayout;
import com.example.application.ui.presenter.TagPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;


@PageTitle("MasterDetail")
@Route(value = "masterDetail/:tag", layout = MainLayout.class)
@PreserveOnRefresh
public class MasterDetailRoute extends Div implements BeforeEnterObserver, BeforeLeaveObserver {

    private final TagPresenter presenter;

    private Component activeView;

    public MasterDetailRoute(TagPresenter presenter) {
        this.presenter = presenter;
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String tag = beforeEnterEvent.getRouteParameters().get("tag").get().replace("%20", " "); //replace spaces

        activeView = presenter.getMasterDetailPresenter(tag).getView();
        add(activeView);

        System.out.println("Route tag:" + tag);
        //TODO choose correct presenter to get View from -> if not exists show 404
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (activeView != null)
            remove(activeView);
        activeView = null; //TODO immer neu erstellen oder nur einmal und dann abspeichern und abrufen über ne Map ?
    }

}
