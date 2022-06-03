package com.example.application.ui.presenter;

import com.example.application.data.services.StrucViewGroupConverterService;
import com.example.application.data.services.StructureProviderService;
import com.example.application.data.structureModel.StrucOpenApi;
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
    private final Map<String, MasterDetailPresenter> presenters;
    private final ClientDataService clientDataService;
    private final StructureProviderService structureProviderService;
    private final StrucViewGroupConverterService strucViewGroupConverterService;

    private StrucOpenApi strucOpenApi;

    public TagPresenter(ClientDataService clientDataService, StrucViewGroupConverterService strucViewGroupConverterService, StructureProviderService structureProviderService) {
        this.clientDataService = clientDataService;
        this.structureProviderService = structureProviderService;
        this.strucViewGroupConverterService = strucViewGroupConverterService;
        presenters = new HashMap<>();
    }

    private void clearOldPresenters() {
        presenters.forEach((key, value) -> AccessPoint.getMainLayout().removeNavigationTarget(key));
        presenters.clear();
    }

    public void registerPresenters(StrucOpenApi strucOpenApi) {
        clearOldPresenters();
        log.info("Registering presenters...");
        strucOpenApi.getStrucViewGroups().forEach(strucViewGroup -> {
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
        this.strucOpenApi = strucOpenApi;
    }

    public List<String> getServers() {
        return strucOpenApi.getServers();
    }

    public void prepareStructure(String source) {
        StrucOpenApi strucOpenApi = structureProviderService.generateApiStructure(source);
        registerPresenters(strucOpenApi);

    }

    private void registerMasterDetailPresenter(String name, StrucViewGroupMDV strucViewGroupMDV) {
        //TODO check if presenter name already exists
        log.info("Registering Master-Detail Presenter for the {} view", name);
        MasterDetailPresenter masterDetailPresenter = new MasterDetailPresenter(clientDataService, strucViewGroupMDV);
        presenters.put(name, masterDetailPresenter);
        AccessPoint.getMainLayout().addNavigationTarget(name);
    }

    public MasterDetailPresenter getPresenter(String name) {
        //TODO catch not existing presenter
        return presenters.get(name);
    }
}
