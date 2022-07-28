package com.example.application.ui.presenter;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.components.detaillayout.DetailLayout;
import com.example.application.ui.view.MasterDetailView;
import com.vaadin.flow.component.Component;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MasterDetailPresenter implements MasterDetailView.MDActionListener {

    private final ClientDataService clientDataService;
    private final DetailLayout.NavigationListener navigationListener;
    @Getter
    private final String navigationRoute;

    private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private final Map<String, MasterDetailPresenter> internalPresenters = new HashMap<>();
    private MasterDetailView view;
    public StrucViewGroupMDV strucViewGroup;

    public MasterDetailPresenter(String navigationRoute, DetailLayout.NavigationListener navigationListener, ClientDataService clientDataService, StrucViewGroupMDV strucViewGroup) {
        this.clientDataService = clientDataService;
        this.navigationRoute = navigationRoute;
        this.navigationListener = navigationListener;
        this.strucViewGroup = strucViewGroup;

        if (!strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.GET) || !strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.GET))
            log.error("Master Detail Presenter created with no Get path or schema: {}", strucViewGroup.getTagName());

        //TODO create master detail presenters for internal primary views

        strucViewGroup.getInternalMDVs().entrySet().forEach(entry -> { //TODO if id is needed put id in path
            String newNavigationRoute = navigationRoute + entry.getValue().getTagName();
            internalPresenters.put(entry.getKey(), new MasterDetailPresenter(newNavigationRoute, navigationListener, clientDataService, entry.getValue()));
        });

    }

    @Deprecated
    public Component getInternalView(List<String> pathParams) { //TODO REMOVE
        log.info("We have this path: " + pathParams.get(0) + " and have to match it to one of these: " + internalPresenters.keySet());

        if (pathParams.size() == 2) {
            //first part is id -> second part is what we match
            List<String> selectedPath = internalPresenters.keySet().stream().filter(presenterPath ->
                    presenterPath.split("/")[presenterPath.split("/").length - 1].equals(pathParams.get(1))).toList();
            //should only have 1 entry now
            if (selectedPath.size() == 1) {
                HashMap<String, String> pathParamMap = new HashMap<>();
                pathParamMap.put(selectedPath.get(0).split("/")[selectedPath.get(0).split("/").length - 2], pathParams.get(0));
                internalPresenters.get(selectedPath).getView(pathParamMap);
            } else {
                //TODO
            }
        } else {
            //is either no id in path (maybe using queryparams?) or just wrong
            //TODO
        }
        //TODO -> see if last parameter matches to any internal primary view
        //TODO (optional) -> if yes send data request to that url -> to check if it actually works
        //TODO call getView on correct internalPrimary Presenter
        return getView();
    }

    public Component getView() {
        return getView(new HashMap<>());
    }

    public Component getView(Map<String, String> pathParams) {
        StrucSchema shownGetSchema;

        if (strucViewGroup.isPaged()) {
            shownGetSchema = strucViewGroup.getBehindPagedGetSchema(); //übergeben: pfade
        } else {
            shownGetSchema = strucViewGroup.getStrucSchemaMap().get(HttpMethod.GET);
        }

        this.pathParams = pathParams;

        view = new MasterDetailView(navigationListener, this, strucViewGroup.isPaged(),
                shownGetSchema,
                strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.POST),
                strucViewGroup.getStrucSchemaMap().get(HttpMethod.PUT),
                strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.DELETE),
                !strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET).getQueryParams().isEmpty());

        view.setData(clientDataService.getData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET),
                strucViewGroup.getBehindPagedGetSchema(), pathParams, queryParams));
        return view;
    }

    @Override
    public void openPostDialog() {
        view.openPostDialog(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.POST));
    }

    @Override
    public void openDeleteDialog() {
        view.openDeleteDialog(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.DELETE));
    }

    @Override
    public void openQueryDialog() {
        view.openQueryParamDialog(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET));
    }

    @Override
    public void refreshData() {
        view.setData(clientDataService.getData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET),
                strucViewGroup.getBehindPagedGetSchema(), pathParams, queryParams));
    }

    @Override
    public void postAction(String path, MultiValueMap<String, String> queryParameters, DataSchema properties) {
        if (strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.POST)) {
            clientDataService.postData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.POST), properties, queryParameters);
        }
        refreshData();
    }

    @Override
    public void deleteAction(String path, Map<String, String> pathVariables, MultiValueMap<String, String> queryParameters) {
        if (strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.DELETE)) {
            //enthaltener parameter (in pfad) raussuchen
            String firstParam = path.split("\\{")[1].split("}")[0];
            //gucken ob param in dataSchema enthalten ist
            //löschanfrage senden
            clientDataService.deleteData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.DELETE), pathVariables, queryParameters);
        }
        refreshData();
    }

    @Override
    public void setQueryParams(MultiValueMap<String, String> queryParams) {
        this.queryParams = queryParams;
        refreshData();
    }

    @Override
    public void putAction(String path, MultiValueMap<String, String> queryParameters, DataSchema properties) {
        //TODO
    }

    public Component getIfHasInternalTargetView(String path) {
        String[] splittedPath = path.split("/");
        Map<String, String> pathParams = new HashMap<>();
        for (Map.Entry<String, MasterDetailPresenter> presenterEntry : internalPresenters.entrySet()) {
            String[] splittedPresenterPath = presenterEntry.getKey().split("/");
            if (splittedPath.length == splittedPresenterPath.length) {
                boolean matching = true;
                for (int i = 0; i < splittedPath.length - 1; i++) {
                    if (!splittedPresenterPath[i].startsWith("{") && !splittedPath[i].equals(splittedPresenterPath[i])) {
                        matching = false;
                        break;
                    } else if (splittedPresenterPath[i].startsWith("{")) {
                        pathParams.put(splittedPresenterPath[i].substring(1, splittedPresenterPath[i].length() - 1), splittedPath[i]);
                    }
                }
                if (matching) {
                    return presenterEntry.getValue().getView(pathParams);
                } else {
                    pathParams.clear();
                }
            }
        }
        return null;
    }
}
