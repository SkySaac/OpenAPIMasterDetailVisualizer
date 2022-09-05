package openapivisualizer.application.ui.route;

import openapivisualizer.application.ui.MainLayout;
import openapivisualizer.application.ui.presenter.MainPresenter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.UIScope;

import javax.annotation.PostConstruct;

@PageTitle("Main")
@Route(value = "main", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PreserveOnRefresh
@UIScope
public class MainRoute extends Div {
    private final MainPresenter presenter;

    public MainRoute(MainPresenter presenter) {
        this.presenter = presenter;
        setSizeFull();
    }

    @PostConstruct
    public void init() {
        add(presenter.getView());
    }
}
