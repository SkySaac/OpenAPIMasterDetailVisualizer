package com.example.application.ui.presenter;

import com.example.application.data.services.SampleDataProviderService;
import com.example.application.data.services.StructureProviderService;
import com.example.application.data.services.StrucViewGroupConverterService;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.accesspoint.AccessPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class TagPresenter {
    private final Map<String, MasterDetailPresenter> presenters;
    private final ClientDataService clientDataService;
    private final SampleDataProviderService sampleDataProviderService;
    private final StructureProviderService structureProviderService;

    private final StrucViewGroupConverterService strucViewGroupConverterService;

    public TagPresenter(ClientDataService clientDataService, StrucViewGroupConverterService strucViewGroupConverterService, StructureProviderService structureProviderService, SampleDataProviderService sampleDataProviderService) {
        this.clientDataService = clientDataService;
        this.sampleDataProviderService = sampleDataProviderService;
        this.structureProviderService = structureProviderService;
        this.strucViewGroupConverterService = strucViewGroupConverterService;
        presenters = new HashMap<>();
    }

    private void clearOldPresenters() {
        presenters.entrySet().forEach(presenterEntry -> AccessPoint.getMainLayout().removeNavigationTarget(presenterEntry.getKey()));
        presenters.clear();
    }

    public void registerPresenters() {
        clearOldPresenters();
        log.info("Registering presenters...");
        structureProviderService.getStrucViewGroups().forEach(strucViewGroup -> {
            if (strucViewGroupConverterService.isMDVStructure(strucViewGroup)) {
                StrucViewGroupMDV strucViewGroupMDV = strucViewGroupConverterService.createStrucViewGroupMDV(strucViewGroup);
                registerMasterDetailPresenter(strucViewGroup.getTagName(), strucViewGroupMDV);
            } else {
                //TODO non Master Detail View
                log.info("Registering List Presenter for the {} view", strucViewGroup.getTagName());
                //Multiple primary view object -> List View
                //StrucViewGroupLV strucViewGroupLV = createStrucViewGroupLV(strucViewGroup);
                //registerListViewPresenter(strucViewGroup.getTagName(), strucViewGroupLV);
            }
        });

    }

    public void prepareStructure(String source) {
        structureProviderService.generateApiStructure(source);
    }

    private void registerMasterDetailPresenter(String name, StrucViewGroupMDV strucViewGroupMDV) {
        //TODO check if presenter name already exists
        log.info("Registering Master-Detail Presenter for the {} view", name);
        MasterDetailPresenter masterDetailPresenter = new MasterDetailPresenter(sampleDataProviderService, clientDataService, strucViewGroupMDV);
        presenters.put(name, masterDetailPresenter);
        AccessPoint.getMainLayout().addNavigationTarget(name);
    }

    public MasterDetailPresenter getPresenter(String name) {
        //TODO catch not existing presenter
        return presenters.get(name);
    }
}
