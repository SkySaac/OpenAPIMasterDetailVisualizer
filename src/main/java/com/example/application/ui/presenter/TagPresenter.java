package com.example.application.ui.presenter;

import com.example.application.data.services.StrucViewGroupConverterService;
import com.example.application.data.services.StructureProviderService;
import com.example.application.data.structureModel.StrucOpenApi;
import com.example.application.data.structureModel.StrucViewGroupLV;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.components.detaillayout.DetailLayout;
import com.example.application.ui.other.AccessPoint;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.PreserveOnRefresh;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@PreserveOnRefresh
@Slf4j
@SessionScope
public class TagPresenter implements DetailLayout.NavigationListener {

    @Getter
    private final Map<String, String> primaryPathToTagNameMap = new HashMap<>(); //TODO remove, see the map below
    private final Map<String, MasterDetailPresenter> masterDetailPresenters = new HashMap<>(); //TODO change to have path directly instead of tag as key
    private final Map<String, ListPresenter> listPresenters = new HashMap<>();
    private final ClientDataService clientDataService;
    private final StructureProviderService structureProviderService;
    private final StrucViewGroupConverterService strucViewGroupConverterService;

    @Getter
    private StrucOpenApi strucOpenApi;

    public TagPresenter(ClientDataService clientDataService, StrucViewGroupConverterService strucViewGroupConverterService, StructureProviderService structureProviderService) {
        this.clientDataService = clientDataService;
        this.structureProviderService = structureProviderService;
        this.strucViewGroupConverterService = strucViewGroupConverterService;
    }

    public List<String> getServers() {
        return strucOpenApi.getServers();
    }

    private void clearOldPresenters() {
        listPresenters.forEach((key, value) -> AccessPoint.getMainLayout().removeNavigationTarget(key));
        listPresenters.clear();
        masterDetailPresenters.forEach((key, value) -> AccessPoint.getMainLayout().removeNavigationTarget(key));
        masterDetailPresenters.clear();
    }

    public void prepareStructure(String source) {
        StrucOpenApi strucOpenApi = structureProviderService.generateApiStructure(source);
        registerPresenters(strucOpenApi);
    }

    public void registerPresenters(StrucOpenApi strucOpenApi) {
        clearOldPresenters();
        log.info("Registering presenters...");
        strucOpenApi.getStrucViewGroups().forEach(strucViewGroup -> {
            if (strucViewGroupConverterService.isMDVStructure(strucViewGroup)) {
                StrucViewGroupMDV strucViewGroupMDV = strucViewGroupConverterService.createStrucViewGroupMDV(strucViewGroup);
                registerMasterDetailPresenter(strucViewGroupMDV, true);
            } else {
                StrucViewGroupLV strucViewGroupLV = strucViewGroupConverterService.createStrucViewGroupLV(strucViewGroup);
                registerListPresenter(strucViewGroupLV);
            }
        });
        this.strucOpenApi = strucOpenApi;
    }

    private void registerMasterDetailPresenter(StrucViewGroupMDV strucViewGroupMDV, boolean menuNavigationable) {
        //TODO check if presenter name already exists
        log.info("Registering Master-Detail Presenter for the {} view", strucViewGroupMDV.getTagName());

        MasterDetailPresenter masterDetailPresenter = new MasterDetailPresenter(this, clientDataService, strucViewGroupMDV);

        masterDetailPresenters.put(strucViewGroupMDV.getTagName(), masterDetailPresenter);

        primaryPathToTagNameMap.put(strucViewGroupMDV.getPrimaryStrucPathMap().get(HttpMethod.GET).getPath(), strucViewGroupMDV.getTagName());
        if (menuNavigationable)
            AccessPoint.getMainLayout().addNavigationTarget(strucViewGroupMDV.getTagName(), true
                    , strucViewGroupMDV.getPrimaryStrucPathMap().get(HttpMethod.GET).getPath());
    }

    private void registerListPresenter(StrucViewGroupLV strucViewGroupLV) {
        //TODO check if presenter name already exists
        log.info("Registering List Presenter for the {} view", strucViewGroupLV.getTagName());

        strucViewGroupLV.getStrucViewGroupMDVS().forEach(
                (k, v) -> registerMasterDetailPresenter(v, false));


        ListPresenter listPresenter = new ListPresenter(clientDataService, strucViewGroupLV, this);

        listPresenters.put(strucViewGroupLV.getTagName(), listPresenter);

        AccessPoint.getMainLayout().addNavigationTarget(strucViewGroupLV.getTagName(), false
                , strucViewGroupLV.getTagName());
    }

    public MasterDetailPresenter getMasterDetailPresenter(String name) {
        //TODO catch not existing presenter
        return masterDetailPresenters.get(name);
    }

    public ListPresenter getListPresenter(String name) {
        //TODO catch not existing presenter
        return listPresenters.get(name);
    }

    @Override
    public void navigate(String path) {
        log.info("Navigation to {}", path);
        if (path.startsWith(clientDataService.getServerUrl())) {
            //TODO kann es zu kollisionen kommen ? (glaube nich außer wenn path param = nem pfadteil ist)

            String pathWithoutServerURL = path.substring(clientDataService.getServerUrl().length());

            //is MDV not listview
            UI.getCurrent().navigate("/masterDetail" + pathWithoutServerURL);

        } else {
            //path not within program
            UI.getCurrent().navigate(path);
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
}
