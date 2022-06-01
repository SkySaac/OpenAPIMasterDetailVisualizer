package com.example.application.ui.presenter;
//
//import com.example.application.data.services.Deprecated.StructureProvider;
//import com.example.application.data.structureModel.OpenApi;

import com.example.application.data.services.StructureProviderService;
import com.example.application.ui.view.AboutView;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@UIScope
@Slf4j
public class AboutPresenter implements AboutView.ActionListener {

    private AboutView view;
    private final StructureProviderService structureProviderService;
    private final TagPresenter tagPresenter;

    public AboutPresenter(StructureProviderService structureProviderService, TagPresenter tagPresenter) {

        this.structureProviderService = structureProviderService;
        this.tagPresenter = tagPresenter;
    }

    public AboutView getView() {
        view = new AboutView(this,StructureProviderService.PARSE_OBJECT);
        return view;
    }

    @Override
    public void action(String source) {
        tagPresenter.prepareStructure(source);
        tagPresenter.registerPresenters();
    }
}
