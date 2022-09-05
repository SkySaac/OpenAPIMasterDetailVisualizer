package openapivisualizer.application.ui.presenter;

import openapivisualizer.application.generation.structuremodel.StrucOpenApi;
import openapivisualizer.application.generation.structuremodel.ViewGroupLV;
import openapivisualizer.application.rest.client.ClientDataService;
import openapivisualizer.application.generation.services.ViewGroupConverterService;
import openapivisualizer.application.generation.services.StructureProviderService;
import openapivisualizer.application.generation.structuremodel.ViewGroupMDV;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;
import openapivisualizer.application.ui.controller.NotificationService;
import openapivisualizer.application.ui.other.AccessPoint;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@PreserveOnRefresh
@Slf4j
@UIScope
public class TagPresenter implements DetailLayout.NavigationListener {
    private final Map<String, MasterDetailPresenter> masterDetailPresenters = new HashMap<>(); //TODO change to have path directly instead of tag as key
    private final Map<String, ListPresenter> listPresenters = new HashMap<>();
    private final ClientDataService clientDataService;
    private final NotificationService notificationService;

    @Getter
    private StrucOpenApi strucOpenApi;

    public TagPresenter(ClientDataService clientDataService, NotificationService notificationService) {
        this.clientDataService = clientDataService;
        this.notificationService = notificationService;
    }

    public List<String> getServers() {
        return strucOpenApi.getServers();
    }

    private void clearOldPresenters() {
        AccessPoint.getMainLayout().removeAll();
        listPresenters.clear();
        masterDetailPresenters.clear();
    }

    public void prepareStructure(String source, boolean onlyListViews, boolean showAllPaths) {
        StrucOpenApi strucOpenApi = StructureProviderService.generateApiStructure(source);
        registerPresenters(strucOpenApi,onlyListViews,showAllPaths);
    }

    public void registerPresenters(StrucOpenApi strucOpenApi, boolean onlyListViews, boolean showAllPaths) {
        clearOldPresenters();
        log.info("Registering presenters...");
        strucOpenApi.getViewGroups().forEach(strucViewGroup -> {
            if (ViewGroupConverterService.isMDVStructure(strucViewGroup) && !onlyListViews) {
                ViewGroupMDV viewGroupMDV = ViewGroupConverterService.createStrucViewGroupMDV(strucViewGroup);
                registerMasterDetailPresenter(viewGroupMDV, true);
            } else {
                ViewGroupLV strucViewGroupLV = ViewGroupConverterService.createViewGroupLV(strucViewGroup,showAllPaths);
                registerListPresenter(strucViewGroupLV, showAllPaths);
            }
        });
        AccessPoint.getMainLayout().applyNavigationTargets();
        this.strucOpenApi = strucOpenApi;
    }

    private void registerMasterDetailPresenter(ViewGroupMDV viewGroupMDV, boolean menuNavigationable) {
        log.info("Registering Master-Detail Presenter for the {} view", viewGroupMDV.getTagName());

        MasterDetailPresenter masterDetailPresenter = new MasterDetailPresenter(notificationService,this, clientDataService, viewGroupMDV);

        masterDetailPresenters.put(viewGroupMDV.getPrimaryStrucPathMap().get(HttpMethod.GET).getPath(), masterDetailPresenter);

        if (menuNavigationable)
            AccessPoint.getMainLayout().addNavigationTarget(viewGroupMDV.getTagName(), true
                    , viewGroupMDV.getPrimaryStrucPathMap().get(HttpMethod.GET).getPath());
    }

    private void registerListPresenter(ViewGroupLV viewGroupLV,boolean showAllPaths) {
        log.info("Registering List Presenter for the {} view", viewGroupLV.getTagName());

        viewGroupLV.getStrucViewGroupMDVS().forEach(
                (k, v) -> registerMasterDetailPresenter(v, false));


        ListPresenter listPresenter = new ListPresenter(notificationService,clientDataService, viewGroupLV, this,showAllPaths);

        listPresenters.put(viewGroupLV.getTagName(), listPresenter);

        AccessPoint.getMainLayout().addNavigationTarget(viewGroupLV.getTagName(), false
                , viewGroupLV.getTagName());
    }

    public MasterDetailPresenter getMasterDetailPresenter(String path) {
        if(masterDetailPresenters.containsKey(path))
            return masterDetailPresenters.get(path);
        else
            return masterDetailPresenters.get(path+"/");
    }

    public ListPresenter getListPresenter(String name) {
        return listPresenters.get(name);
    }

    @Override
    public void navigate(String path) {
        log.info("Navigation to {}", path);
        log.info("CurrentServerURl: {}",clientDataService.getServerUrl());
        if (path.startsWith(clientDataService.getServerUrl())) {
            //TODO kann es zu kollisionen kommen ? (glaube nich außer wenn path param = nem pfadteil ist)

            String pathWithoutServerURL = path.substring(clientDataService.getServerUrl().length());

            //is MDV not listview
            UI.getCurrent().navigate("/masterDetail" + pathWithoutServerURL);

        } else {
            //path not within program
            UI.getCurrent().getPage().open(path);
        }
    }

    public Component getMDVInternalNavigationTargetFromPath(String path) {
        List<Component> foundMDVPresenters = masterDetailPresenters.values().stream()
                .map(masterDetailPresenter -> masterDetailPresenter.getIfHasInternalTargetView(path))
                .filter(Objects::nonNull).toList();

        if (foundMDVPresenters.size() > 0) {
            return foundMDVPresenters.get(0);
        }
        return null;
    }

    public boolean hasMasterDetailPresenter(String path){
        return masterDetailPresenters.containsKey(path) || masterDetailPresenters.containsKey(path+"/") ;
    }
}
