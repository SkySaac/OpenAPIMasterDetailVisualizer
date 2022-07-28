package com.example.application.ui.route;

import com.example.application.ui.MainLayout;
import com.example.application.ui.presenter.TagPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;


@PageTitle("MasterDetail")
@Route(value = "masterDetail/:tag", layout = MainLayout.class)
@PreserveOnRefresh
@Slf4j
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
    @Deprecated
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {

        //String tag = event.getRouteParameters().get("tag").get().replace("%20", " "); //replace spaces
        String targetedPath = "/" + event.getRouteParameters().get("tag").get() + "/" + parameter; //TODO besser machen

        log.info("targeted path {}", targetedPath);

        //TODO what if secondaryPath -> make getPrimaryPathToTagNameMap have a list that has primary and secondary path

        if (presenter.getPrimaryPathToTagNameMap().containsKey(targetedPath)) {
            activeView = presenter.getMasterDetailPresenter(presenter.getPrimaryPathToTagNameMap().get(targetedPath)).getView();
            add(activeView);
        } else {
            log.info("Route with parameter {} detected", targetedPath);
            activeView = presenter.getMDVInternalNavigationTargetFromPath(targetedPath);
            //activeView = presenter.getMasterDetailPresenter(targetedPath).getInternalView(List.of(parameter.split("/")));
            if(activeView==null)
            {
                //TODO activeView = navigate to 404
            }
            add(activeView);
        }
    }

}
