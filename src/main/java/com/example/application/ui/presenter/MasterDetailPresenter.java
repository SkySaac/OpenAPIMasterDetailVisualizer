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

    private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private final Map<String, MasterDetailPresenter> internalPresenters = new HashMap<>();
    private MasterDetailView view;
    public StrucViewGroupMDV strucViewGroup;

    public MasterDetailPresenter(DetailLayout.NavigationListener navigationListener, ClientDataService clientDataService, StrucViewGroupMDV strucViewGroup) {
        this.clientDataService = clientDataService;
        this.navigationListener = navigationListener;
        this.strucViewGroup = strucViewGroup;

        if (!strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.GET) || !strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.GET))
            log.error("Master Detail Presenter created with no Get path or schema: {}", strucViewGroup.getTagName());

        strucViewGroup.getInternalMDVs().forEach((key, value) -> { //TODO if id is needed put id in path
            internalPresenters.put(key, new MasterDetailPresenter(navigationListener, clientDataService, value));
        });

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
        //checking the secondary path here
        if(strucViewGroup.getSecondaryGetPath()!=null){
            String[] splittedPresenterPath = strucViewGroup.getSecondaryGetPath().split("/");

            boolean matching = true;
            for (int i = 0; i < splittedPath.length - 1; i++) {
                if (!splittedPresenterPath[i].startsWith("{") && !splittedPath[i].equals(splittedPresenterPath[i])) {
                    matching = false;
                    break;
                }
            }
            if (matching) {
                return getView();
            }
        }
        return null;
    }
}
