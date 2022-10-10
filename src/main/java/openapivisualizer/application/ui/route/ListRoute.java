package openapivisualizer.application.ui.route;

import openapivisualizer.application.ui.MainLayout;
import openapivisualizer.application.ui.other.AccessPoint;
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
        AccessPoint.getMainLayout().setCurrentPageTitle(tag);

        add(activeView);
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
