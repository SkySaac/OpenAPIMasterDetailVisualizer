package openapivisualizer.application.ui.route;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.ui.MainLayout;
import openapivisualizer.application.ui.other.AccessPoint;
import openapivisualizer.application.ui.presenter.NotFoundPresenter;
import openapivisualizer.application.ui.presenter.TagPresenter;
import openapivisualizer.application.ui.view.View;


@PageTitle("MasterDetail")
@Route(value = "masterDetail", layout = MainLayout.class)
@PreserveOnRefresh
@Slf4j
@UIScope
public class MasterDetailRoute extends Div implements BeforeLeaveObserver, HasUrlParameter<String> {

    private final TagPresenter tagPresenter;
    private final NotFoundPresenter notFoundPresenter;

    private Component activeView;

    public MasterDetailRoute(TagPresenter tagPresenter, NotFoundPresenter notFoundPresenter) {
        this.tagPresenter = tagPresenter;
        this.notFoundPresenter = notFoundPresenter;
        setSizeFull();
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (activeView != null)
            remove(activeView);
        activeView = null;
    }

    @Override
    public void replace(Component oldComponent, Component newComponent) {
        super.replace(oldComponent, newComponent);
        activeView = newComponent;
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        UI.getCurrent().getPage().setTitle("title");
        //String tag = event.getRouteParameters().get("tag").get().replace("%20", " "); //replace spaces
        String targetedPath = "/" + parameter;

        log.info("targeted path {}", targetedPath);

        if (tagPresenter.hasMasterDetailPresenter(targetedPath)) {
            activeView = tagPresenter.getMasterDetailPresenter(targetedPath).getView();
            AccessPoint.getMainLayout().setCurrentPageTitle(tagPresenter.getMasterDetailPresenter(targetedPath).getTagGroupMD().getTagName());
            add(activeView);
        } else {
            log.info("Route with parameter {} detected", targetedPath);
            View activeView = tagPresenter.getMDVNavigationView(targetedPath);
            this.activeView = activeView;
            //activeView = presenter.getMasterDetailPresenter(targetedPath).getInternalView(List.of(parameter.split("/")));
            if (activeView == null) {
                add(notFoundPresenter.getView());
                //UI.getCurrent().navigate("/404");
            } else {
                add(activeView);
            }
            AccessPoint.getMainLayout().setCurrentPageTitle(activeView.getTag());

        }
    }

}
