package openapivisualizer.application.ui.route;

import com.vaadin.flow.component.page.Page;
import openapivisualizer.application.ui.MainLayout;
import openapivisualizer.application.ui.other.AccessPoint;
import openapivisualizer.application.ui.presenter.TagPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;


@PageTitle("MasterDetail")
@Route(value = "masterDetail", layout = MainLayout.class) //TODO klein schreiben
@PreserveOnRefresh
@Slf4j
@UIScope
public class MasterDetailRoute extends Div implements  BeforeLeaveObserver, HasUrlParameter<String> {

    private final TagPresenter tagPresenter;

    private Component activeView;

    public MasterDetailRoute(TagPresenter tagPresenter) {
        this.tagPresenter = tagPresenter;
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
        UI.getCurrent().getPage().setTitle("title");
        //String tag = event.getRouteParameters().get("tag").get().replace("%20", " "); //replace spaces
        String targetedPath = "/" + parameter; //TODO besser machen

        log.info("targeted path {}", targetedPath);

        if (tagPresenter.hasMasterDetailPresenter(targetedPath)) {
            activeView = tagPresenter.getMasterDetailPresenter(targetedPath).getView();
            AccessPoint.getMainLayout().setCurrentPageTitle(tagPresenter.getMasterDetailPresenter(targetedPath).getStrucViewGroup().getTagName());
            add(activeView);
        }else {
            log.info("Route with parameter {} detected", targetedPath);
            activeView = tagPresenter.getMDVInternalNavigationTargetFromPath(targetedPath);
            //activeView = presenter.getMasterDetailPresenter(targetedPath).getInternalView(List.of(parameter.split("/")));
            if(activeView==null)
            {
                UI.getCurrent().navigate("/404");
            }
            add(activeView);
        }
    }

}
