package com.example.application.ui.presenter;
//
//import com.example.application.data.services.Deprecated.StructureProvider;
//import com.example.application.data.structureModel.OpenApi;

import com.example.application.data.services.StructureProviderService;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.view.AboutView;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Controller
@UIScope
@Slf4j
public class AboutPresenter implements AboutView.ActionListener {

    private final ClientDataService clientDataService;
    private final TagPresenter tagPresenter;

    private final List<String> serverList = new ArrayList<>();

    private String currentServerURL = "/";
    private final AboutView view  = new AboutView(this, StructureProviderService.PARSE_OBJECT,currentServerURL,serverList);


    public AboutPresenter(ClientDataService clientDataService, TagPresenter tagPresenter) {
        this.clientDataService = clientDataService;
        this.tagPresenter = tagPresenter;
    }

    public AboutView getView() {
        view.setSelectedServer(currentServerURL);
        return view;
    }

    @Override
    public void openApiAction(String source) {
        tagPresenter.prepareStructure(source);
        serverList.clear();
        serverList.addAll(tagPresenter.getServers());
        view.setServers(serverList);
    }

    @Override
    public void serverSelected(String selectedServerURL) {
        clientDataService.setServerUrl(selectedServerURL);
        currentServerURL = selectedServerURL;
        log.info("New Server selected: {}",selectedServerURL);
    }

    @Override
    public void addServerToSelection(String server) {
        serverList.add(server);
        view.setServers(serverList);
        view.setSelectedServer(server);
    }


}
