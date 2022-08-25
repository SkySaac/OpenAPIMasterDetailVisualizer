package com.example.application.ui.presenter;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucViewGroupLV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.components.DeleteDialog;
import com.example.application.ui.components.PostDialog;
import com.example.application.ui.components.PutDialog;
import com.example.application.ui.components.detaillayout.DetailLayout;
import com.example.application.ui.controller.NotificationService;
import com.example.application.ui.view.ListView;
import com.vaadin.flow.component.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

@Slf4j
public class ListPresenter implements ListView.LActionListener, PostDialog.PostActionListener,
        DeleteDialog.DeleteActionListener, PutDialog.PutActionListener, Presenter {

    private final StrucViewGroupLV strucViewGroup;
    private final NotificationService notificationService;
    private final DetailLayout.NavigationListener navigationListener;
    private final ClientDataService clientDataService;
    private final ListView view;

    public ListPresenter(NotificationService notificationService, ClientDataService clientDataService, StrucViewGroupLV strucViewGroup, DetailLayout.NavigationListener navigationListener) {
        this.clientDataService = clientDataService;
        this.notificationService = notificationService;
        this.strucViewGroup = strucViewGroup;
        this.navigationListener = navigationListener;
        view = new ListView(strucViewGroup.getTagName(), this, strucViewGroup.getStrucViewGroupMDVS(), strucViewGroup.getNotMatchedStrucPathMap());
    }

    public Component getView() {
        return view;
    }

    @Override
    public void openPostDialog(String path) {
        PostDialog postDialog = new PostDialog(this);
        postDialog.open(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.POST));
    }

    @Override
    public void openPutDialog(String path) {
        //TODO
        PutDialog putDialog = new PutDialog(this);
        putDialog.open(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.PUT));
    }

    @Override
    public void navigateFromListView(String path) {
        if (!clientDataService.getServerUrl().endsWith("/") && !path.startsWith("/"))
            navigationListener.navigate(clientDataService.getServerUrl() + "/" + path);
        else if (clientDataService.getServerUrl().endsWith("/") && path.startsWith("/"))
            navigationListener.navigate(clientDataService.getServerUrl() + path.substring(1));
        else {
            navigationListener.navigate(clientDataService.getServerUrl() + path);
        }
    }

    @Override
    public void openDeleteDialog(String path) {
        DeleteDialog deleteDialog = new DeleteDialog(this);
        deleteDialog.open(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.DELETE));
    }

    @Override
    public void postAction(String path, MultiValueMap<String, String> queryParameters,Map<String, String> pathVariables, DataSchema properties) { //TODO use queryparams
        if (strucViewGroup.getNotMatchedStrucPathMap().containsKey(path)) { //TODO map passt nich
            try {
                clientDataService.postData(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.POST), properties, queryParameters,pathVariables);
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
            }
        }
    }

    @Override
    public void deleteAction(String path, Map<String, String> pathVariables, MultiValueMap<String, String> queryParameters) {
        if (strucViewGroup.getNotMatchedStrucPathMap().containsKey(path)) { //TODO map passt nich
            try {
                clientDataService.deleteData(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.DELETE).getPath()
                        , pathVariables, queryParameters);
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
            }
        }
    }

    @Override
    public void putAction(String path, MultiValueMap<String, String> queryParameters,Map<String,String> pathParams, DataSchema properties) {
        //TODO
    }
}
