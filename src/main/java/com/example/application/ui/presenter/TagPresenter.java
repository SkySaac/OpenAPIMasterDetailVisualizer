package com.example.application.ui.presenter;

import com.example.application.data.services.StrucViewGroupConverterService;
import com.example.application.data.services.StructureProviderService;
import com.example.application.data.structureModel.StrucOpenApi;
import com.example.application.data.structureModel.StrucViewGroupLV;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.accesspoint.AccessPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class TagPresenter {
    private final Map<String, MasterDetailPresenter> masterDetailPresenters = new HashMap<>();
    private final Map<String, ListPresenter> listPresenters = new HashMap<>();
    private final ClientDataService clientDataService;
    private final StructureProviderService structureProviderService;
    private final StrucViewGroupConverterService strucViewGroupConverterService;

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
                registerMasterDetailPresenter(strucViewGroupMDV);
            } else {
                StrucViewGroupLV strucViewGroupLV = strucViewGroupConverterService.createStrucViewGroupLV(strucViewGroup);
                registerListPresenter(strucViewGroupLV);
            }
        });
        this.strucOpenApi = strucOpenApi;
    }

    private void registerMasterDetailPresenter(StrucViewGroupMDV strucViewGroupMDV) {
        //TODO check if presenter name already exists
        log.info("Registering Master-Detail Presenter for the {} view", strucViewGroupMDV.getTagName());

        MasterDetailPresenter masterDetailPresenter = new MasterDetailPresenter(clientDataService, strucViewGroupMDV);

        masterDetailPresenters.put(strucViewGroupMDV.getTagName(), masterDetailPresenter);

        AccessPoint.getMainLayout().addNavigationTarget(strucViewGroupMDV.getTagName(),true);
    }

    private void registerListPresenter(StrucViewGroupLV strucViewGroupLV) {
        //TODO check if presenter name already exists
        log.info("Registering List Presenter for the {} view", strucViewGroupLV.getTagName());

        //TODO feed presenter the internal MDVs

        ListPresenter listPresenter = new ListPresenter(clientDataService,strucViewGroupLV);

        listPresenters.put(strucViewGroupLV.getTagName(), listPresenter);

        AccessPoint.getMainLayout().addNavigationTarget(strucViewGroupLV.getTagName(),false);
    }

    public MasterDetailPresenter getMasterDetailPresenter(String name) {
        //TODO catch not existing presenter
        return masterDetailPresenters.get(name);
    }

    public ListPresenter getListPresenter(String name) {
        //TODO catch not existing presenter
        return listPresenters.get(name);
    }
}
