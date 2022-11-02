package openapivisualizer.application.ui.route;

import openapivisualizer.application.ui.MainLayout;
import openapivisualizer.application.ui.other.AccessPoint;
import openapivisualizer.application.ui.presenter.NotFoundPresenter;
import openapivisualizer.application.ui.presenter.TagPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

@PageTitle("List")
@Route(value="list/:tag", layout = MainLayout.class)
@PreserveOnRefresh
@Slf4j
@UIScope
public class ListRoute extends Div implements BeforeEnterObserver,BeforeLeaveObserver{

    private final TagPresenter presenter;
    private final NotFoundPresenter notFoundPresenter;

    private Component activeView;

    public ListRoute(TagPresenter presenter, NotFoundPresenter notFoundPresenter) {
        this.presenter = presenter;
        this.notFoundPresenter = notFoundPresenter;
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent){
        String tag = beforeEnterEvent.getRouteParameters().get("tag").get().replace("%20"," "); //replace spaces
        System.out.println("Route tag:"+ tag);
        if(presenter.getListPresenter(tag) !=null) {
            activeView = presenter.getListPresenter(tag).getView();
            AccessPoint.getMainLayout().setCurrentPageTitle(tag);
            add(activeView);
        }
        else{
            add(notFoundPresenter.getView());
            activeView = null;
            AccessPoint.getMainLayout().setCurrentPageTitle("404");
        }

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent){
        if(activeView==null)
            log.warn("activeView is null when it should be removed");
        else
            remove(activeView);
        activeView = null;
    }
}
