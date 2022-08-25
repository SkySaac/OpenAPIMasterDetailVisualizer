package openapivisualizer.application.ui.route;

import openapivisualizer.application.ui.MainLayout;
import openapivisualizer.application.ui.presenter.NotFoundPresenter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;

import javax.annotation.PostConstruct;

@Route(value = "404", layout = MainLayout.class)
public class NotFoundRoute extends Div {

    private final NotFoundPresenter presenter;
    public NotFoundRoute(NotFoundPresenter presenter) {
        this.presenter = presenter;
        setSizeFull();
    }

    @PostConstruct
    public void init() {
        add(presenter.getView());
    }
}