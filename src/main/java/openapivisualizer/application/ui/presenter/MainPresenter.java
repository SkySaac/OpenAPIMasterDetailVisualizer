package openapivisualizer.application.ui.presenter;
//
//import com.example.application.data.services.Deprecated.StructureProvider;
//import com.example.application.data.structureModel.OpenApi;

import openapivisualizer.application.generation.services.StructureProviderService;
import openapivisualizer.application.rest.client.ClientDataService;
import openapivisualizer.application.ui.components.ExtractionSettingsDialog;
import openapivisualizer.application.ui.components.SettingsDialog;
import openapivisualizer.application.ui.controller.NotificationService;
import openapivisualizer.application.ui.view.MainView;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@UIScope
@Slf4j
public class MainPresenter implements MainView.ActionListener,ExtractionSettingsDialog.ActionListener {

    private final NotificationService notificationService;
    private final ClientDataService clientDataService;
    private final TagPresenter tagPresenter;

    private final List<String> serverList = new ArrayList<>();

    private String currentServerURL = "/";
    private boolean onlyListViews = false;
    private boolean showAllPaths = false;
    private final MainView view  = new MainView(this, StructureProviderService.DEFAULT_PARSE_OBJECT,currentServerURL,serverList);


    public MainPresenter(NotificationService notificationService, ClientDataService clientDataService, TagPresenter tagPresenter) {
        this.notificationService = notificationService;
        this.clientDataService = clientDataService;
        this.tagPresenter = tagPresenter;
    }

    public MainView getView() {
        view.setSelectedServer(currentServerURL);
        return view;
    }

    @Override
    public void openApiAction(String source) {
        tagPresenter.prepareStructure(source,onlyListViews, showAllPaths);
        serverList.clear();
        serverList.addAll(tagPresenter.getServers());
        view.setServers(serverList);
    }

    @Override
    public void openSettings() {
        ExtractionSettingsDialog settingsDialog = new ExtractionSettingsDialog(this);
        settingsDialog.open(onlyListViews,showAllPaths);
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

    @Override
    public void setCredential(String username, String password) {
        clientDataService.setUsername(username);
        clientDataService.setPassword(password);
        notificationService.postNotification("Credentials successfully set!",false);
    }


    @Override
    public void save(boolean onlyListViews, boolean showAllPaths) {
        this.onlyListViews = onlyListViews;
        this.showAllPaths = showAllPaths;
    }
}
