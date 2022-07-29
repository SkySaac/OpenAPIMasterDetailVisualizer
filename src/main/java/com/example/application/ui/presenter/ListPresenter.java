package com.example.application.ui.presenter;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucViewGroupLV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.components.detaillayout.DetailLayout;
import com.example.application.ui.view.ListView;
import com.vaadin.flow.component.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Slf4j
public class ListPresenter implements ListView.LActionListener {

    private final StrucViewGroupLV strucViewGroup;
    private final DetailLayout.NavigationListener navigationListener;
    private final ClientDataService clientDataService;
    private ListView view;

    public ListPresenter(ClientDataService clientDataService, StrucViewGroupLV strucViewGroup, DetailLayout.NavigationListener navigationListener) {
        this.clientDataService = clientDataService;
        this.strucViewGroup = strucViewGroup;
        this.navigationListener = navigationListener;
    }

    public Component getView() {
        view = new ListView(strucViewGroup.getTagName(), this, strucViewGroup.getNotMatchedStrucPathMap()); //übergeben: pfade
        return view;
    }

    @Override
    public void openPostDialog(String path) {
        StrucPath strucPath = strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.POST);
        view.openPostDialog(strucPath);
    }

    @Override
    public void openPutDialog(String path) {
        StrucPath strucPath = strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.PUT);
        view.openPutDialog(strucPath);
    }

    @Override
    public void navigateFromListView(String path) {
        if (!clientDataService.getServerUrl().endsWith("/") && !path.startsWith("/"))
            navigationListener.navigate(clientDataService.getServerUrl() + "/" + path);
        else if (clientDataService.getServerUrl().endsWith("/") && path.startsWith("/"))
            navigationListener.navigate(clientDataService.getServerUrl()+ path.substring(1));
        else{
            navigationListener.navigate(clientDataService.getServerUrl()+ path);
        }
    }

    @Override
    public void openDeleteDialog(String path) {
        StrucPath strucPath = strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.DELETE);
        view.openDeleteDialog(strucPath);
    }

    @Override
    public void postAction(String path, MultiValueMap<String, String> queryParameters, DataSchema properties) { //TODO use queryparams
        if (strucViewGroup.getNotMatchedStrucPathMap().containsKey(path)) { //TODO map passt nich
            clientDataService.postData(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.POST), properties, queryParameters);
        }
    }

    @Override
    public void deleteAction(String path, Map<String, String> pathVariables, MultiValueMap<String, String> queryParameters) {
        if (strucViewGroup.getNotMatchedStrucPathMap().containsKey(path)) { //TODO map passt nich
            clientDataService.deleteData(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.DELETE), pathVariables, queryParameters);
        }
    }

    @Override
    public void putAction(String path, MultiValueMap<String, String> queryParameters, DataSchema properties) {
        //TODO
    }
}
