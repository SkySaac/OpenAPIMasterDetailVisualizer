package com.example.application.ui.route;

import com.example.application.ui.MainLayout;
import com.example.application.ui.presenter.TagPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.annotation.SessionScope;

@PageTitle("List")
@Route(value="list/:tag", layout = MainLayout.class) //TODO rename "tag"
@PreserveOnRefresh
@Slf4j
@SessionScope
public class ListRoute extends Div implements BeforeEnterObserver,BeforeLeaveObserver{

    private final TagPresenter presenter;

    private Component activeView;

    public ListRoute(TagPresenter presenter) {
        this.presenter = presenter;
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent){
        String tag = beforeEnterEvent.getRouteParameters().get("tag").get().replace("%20"," "); //replace spaces
        System.out.println("Route tag:"+ tag);

        activeView = presenter.getListPresenter(tag).getView();
        add(activeView);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent){
        if(activeView==null)
            log.warn("activeView is null when it should be removed");
        else
            remove(activeView);
        activeView = null; //TODO immer neu erstellen oder nur einmal und dann abspeichern und abrufen über ne Map ?
    }
}
