package openapivisualizer.application.ui.presenter;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.services.StructureProviderService;
import openapivisualizer.application.generation.services.TagGroupConverterService;
import openapivisualizer.application.generation.structuremodel.OpenApiStructure;
import openapivisualizer.application.generation.structuremodel.TagGroupLV;
import openapivisualizer.application.generation.structuremodel.TagGroupMD;
import openapivisualizer.application.rest.client.ClientDataService;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;
import openapivisualizer.application.ui.service.NotificationService;
import openapivisualizer.application.ui.other.AccessPoint;
import openapivisualizer.application.ui.view.View;
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
    private final Map<String, MasterDetailPresenter> masterDetailPresenters = new HashMap<>();
    private final Map<String, ListPresenter> listPresenters = new HashMap<>();
    private final ClientDataService clientDataService;
    private final StructureProviderService structureProviderService;
    private final TagGroupConverterService tagGroupConverterService;
    private final NotificationService notificationService;


    @Getter
    private OpenApiStructure openApiStructure;

    public TagPresenter(ClientDataService clientDataService, StructureProviderService structureProviderService, TagGroupConverterService tagGroupConverterService, NotificationService notificationService) {
        this.clientDataService = clientDataService;
        this.structureProviderService = structureProviderService;
        this.tagGroupConverterService = tagGroupConverterService;
        this.notificationService = notificationService;
    }

    public List<String> getServers() {
        return openApiStructure.getServers();
    }

    private void clearOldPresenters() {
        AccessPoint.getMainLayout().removeAll();
        listPresenters.clear();
        masterDetailPresenters.clear();
    }

    public void prepareStructure(String source, boolean onlyListViews, boolean showAllPaths) {
        OpenApiStructure openApiStructure = structureProviderService.generateApiStructure(source);
        registerPresenters(openApiStructure, onlyListViews, showAllPaths);
    }

    public void registerPresenters(OpenApiStructure openApiStructure, boolean onlyListViews, boolean showAllPaths) {
        clearOldPresenters();
        log.info("Registering presenters...");
        openApiStructure.getTagGroups().forEach(tagGroup -> {
            if (tagGroupConverterService.isMDVStructure(tagGroup) && !onlyListViews) {
                TagGroupMD TagGroupMD = tagGroupConverterService.createMDTagGroup(tagGroup);
                registerMasterDetailPresenter(TagGroupMD, true);
            } else {
                TagGroupLV strucTagGroupLV = tagGroupConverterService.createTagGroupLV(tagGroup, showAllPaths);
                registerListPresenter(strucTagGroupLV, showAllPaths);
            }
        });
        AccessPoint.getMainLayout().applyNavigationTargets();
        this.openApiStructure = openApiStructure;
    }

    private void registerMasterDetailPresenter(TagGroupMD TagGroupMD, boolean menuNavigationable) {
        log.info("Registering Master-Detail Presenter for the {} view", TagGroupMD.getTagName());

        if(TagGroupMD.getApiPathMap().get(HttpMethod.GET).getResponseStrucSchema()!=null) {
            MasterDetailPresenter masterDetailPresenter = new MasterDetailPresenter(notificationService, this, clientDataService, TagGroupMD);

            masterDetailPresenters.put(TagGroupMD.getApiPathMap().get(HttpMethod.GET).getPath(), masterDetailPresenter);

            if (menuNavigationable)
                AccessPoint.getMainLayout().addNavigationTarget(TagGroupMD.getTagName(), true
                        , TagGroupMD.getApiPathMap().get(HttpMethod.GET).getPath());
        }else{
            log.warn("{} has no valid response", TagGroupMD.getApiPathMap().get(HttpMethod.GET).getPath());
        }
    }

    private void registerListPresenter(TagGroupLV tagGroupLV, boolean showAllPaths) {
        log.info("Registering List Presenter for the {} view", tagGroupLV.getTagName());

        tagGroupLV.getStrucViewGroupMDVS().forEach((k, v) -> registerMasterDetailPresenter(v, false));

        ListPresenter listPresenter = new ListPresenter(notificationService, clientDataService, tagGroupLV, this, showAllPaths);

        listPresenters.put(tagGroupLV.getTagName(), listPresenter);

        AccessPoint.getMainLayout().addNavigationTarget(tagGroupLV.getTagName(), false
                , tagGroupLV.getTagName());
    }

    public MasterDetailPresenter getMasterDetailPresenter(String path) {
        if (masterDetailPresenters.containsKey(path))
            return masterDetailPresenters.get(path);
        else
            return masterDetailPresenters.get(path + "/");
    }

    public ListPresenter getListPresenter(String name) {
        return listPresenters.get(name);
    }

    @Override
    public void navigate(String path) {
        log.info("Navigation to {}", path);
        log.info("CurrentServerURl: {}", clientDataService.getServerUrl());
        if (path.startsWith(clientDataService.getServerUrl())) {

            String pathWithoutServerURL = path.substring(clientDataService.getServerUrl().length());

            //is MDV not listview
            UI.getCurrent().navigate("/masterDetail" + pathWithoutServerURL);

        } else {
            //path not within program
            UI.getCurrent().getPage().open(path);
        }
    }

    public View getMDVNavigationView(String path) {
        List<View> foundMDVPresenters = masterDetailPresenters.values().stream()
                .map(masterDetailPresenter -> masterDetailPresenter.getIfHasTargetView(path))
                .filter(Objects::nonNull).toList();
        if (foundMDVPresenters.size() > 0) {
            return foundMDVPresenters.get(0);
        }
        return null;
    }

    public boolean hasMasterDetailPresenter(String path) {
        return masterDetailPresenters.containsKey(path) || masterDetailPresenters.containsKey(path + "/");
    }
}
