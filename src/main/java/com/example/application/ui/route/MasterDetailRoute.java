package com.example.application.ui.route;

import com.example.application.ui.MainLayout;
import com.example.application.ui.presenter.TagPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.annotation.SessionScope;


@PageTitle("MasterDetail")
@Route(value = "masterDetail/:tag", layout = MainLayout.class) //TODO klein schreiben
@PreserveOnRefresh
@Slf4j
@SessionScope
public class MasterDetailRoute extends Div implements BeforeLeaveObserver, HasUrlParameter<String> {

    private final TagPresenter presenter;

    private Component activeView;

    public MasterDetailRoute(TagPresenter presenter) {
        this.presenter = presenter;
        setSizeFull();
    }


    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (activeView != null)
            remove(activeView);
        activeView = null;
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {

        //String tag = event.getRouteParameters().get("tag").get().replace("%20", " "); //replace spaces
        String targetedPath = "/" + event.getRouteParameters().get("tag").get() + "/" + parameter; //TODO besser machen

        log.info("targeted path {}", targetedPath);

        if (presenter.getPrimaryPathToTagNameMap().containsKey(targetedPath)) {
            activeView = presenter.getMasterDetailPresenter(presenter.getPrimaryPathToTagNameMap().get(targetedPath)).getView();
            add(activeView);
        }else {
            log.info("Route with parameter {} detected", targetedPath);
            activeView = presenter.getMDVInternalNavigationTargetFromPath(targetedPath);
            //activeView = presenter.getMasterDetailPresenter(targetedPath).getInternalView(List.of(parameter.split("/")));
            if(activeView==null)
            {
                UI.getCurrent().navigate("/404");
            }
            add(activeView);
        }
    }

}
