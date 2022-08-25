package openapivisualizer.application.ui.presenter;

import openapivisualizer.application.ui.view.NotFoundView;
import org.springframework.stereotype.Controller;

@Controller
public class NotFoundPresenter{

    private final NotFoundView view = new NotFoundView();

    public NotFoundView getView(){
        return view;
    }
}
