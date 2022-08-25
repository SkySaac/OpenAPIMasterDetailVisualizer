package com.example.application.ui.presenter;

import com.example.application.ui.view.NotFoundView;
import org.springframework.stereotype.Controller;

@Controller
public class NotFoundPresenter implements Presenter{

    private final NotFoundView view = new NotFoundView();

    public NotFoundView getView(){
        return view;
    }
}
