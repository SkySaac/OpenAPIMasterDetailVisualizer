package openapivisualizer.application.ui.presenter;

import com.vaadin.flow.component.Component;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.structuremodel.ViewGroupLV;
import openapivisualizer.application.rest.client.ClientDataService;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.ui.components.DeleteDialog;
import openapivisualizer.application.ui.components.PathParamsDialog;
import openapivisualizer.application.ui.components.PostDialog;
import openapivisualizer.application.ui.components.PutDialog;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;
import openapivisualizer.application.ui.controller.NotificationService;
import openapivisualizer.application.ui.view.ListView;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ListPresenter implements ListView.LActionListener, PostDialog.PostActionListener,
        DeleteDialog.DeleteActionListener, PutDialog.PutActionListener, PathParamsDialog.PathParamsActionListener {

    @Getter
    private final ViewGroupLV viewGroupLV;
    private final NotificationService notificationService;
    private final DetailLayout.NavigationListener navigationListener;
    private final ClientDataService clientDataService;
    private final ListView view;

    public ListPresenter(NotificationService notificationService, ClientDataService clientDataService, ViewGroupLV viewGroupLV, DetailLayout.NavigationListener navigationListener, boolean showAllPaths) {
        this.clientDataService = clientDataService;
        this.notificationService = notificationService;
        this.viewGroupLV = viewGroupLV;
        this.navigationListener = navigationListener;
        view = new ListView(showAllPaths, this, viewGroupLV.getStrucViewGroupMDVS(), viewGroupLV.getNotMatchedStrucPathMap());
    }

    public Component getView() {
        return view;
    }

    @Override
    public void openPostDialog(String path) {
        PostDialog postDialog = new PostDialog(this);
        postDialog.open(viewGroupLV.getNotMatchedStrucPathMap().get(path).get(HttpMethod.POST));
    }

    @Override
    public void openPutDialog(String path) {
        PutDialog putDialog = new PutDialog(this);
        putDialog.open(viewGroupLV.getNotMatchedStrucPathMap().get(path).get(HttpMethod.PUT));
    }

    @Override
    public void openDeleteDialog(String path) {
        DeleteDialog deleteDialog = new DeleteDialog(this);
        deleteDialog.open(viewGroupLV.getNotMatchedStrucPathMap().get(path).get(HttpMethod.DELETE));
    }

    @Override
    public void navigateFromListView(String path) {
        if (path.contains("{") && path.contains("}")) {
            openPathParamDialog(path);
        } else {

            if (!clientDataService.getServerUrl().endsWith("/") && !path.startsWith("/"))
                navigationListener.navigate(clientDataService.getServerUrl() + "/" + path);
            else if (clientDataService.getServerUrl().endsWith("/") && path.startsWith("/"))
                navigationListener.navigate(clientDataService.getServerUrl() + path.substring(1));
            else {
                navigationListener.navigate(clientDataService.getServerUrl() + path);
            }
        }
    }

    public void openPathParamDialog(String path) {
        PathParamsDialog pathParamsDialog = new PathParamsDialog(this);
        pathParamsDialog.open(path, getPathParamsFromString(path));
    }

    public List<String> getPathParamsFromString(String path) {
        List<String> pathParams = new ArrayList<>();

        String[] pathSplitted = path.split("\\{");

        for (int i = 1; i < pathSplitted.length; i++) {
            String param = pathSplitted[i].split("}",2)[0];
            pathParams.add(param);
        }

        return pathParams;
    }

    @Override
    public void postAction(String path, MultiValueMap<String, String> queryParameters, Map<String, String> pathVariables, DataSchema properties) { //TODO use queryparams
        if (viewGroupLV.getNotMatchedStrucPathMap().containsKey(path) && viewGroupLV.getNotMatchedStrucPathMap().get(path).containsKey(HttpMethod.POST)) { //TODO map passt nich
            try {
                clientDataService.postData(viewGroupLV.getNotMatchedStrucPathMap().get(path).get(HttpMethod.POST), properties, queryParameters, pathVariables);
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
            }
        }
    }

    @Override
    public void deleteAction(String path, Map<String, String> pathVariables, MultiValueMap<String, String> queryParameters) {
        if (viewGroupLV.getNotMatchedStrucPathMap().containsKey(path) && viewGroupLV.getNotMatchedStrucPathMap().get(path).containsKey(HttpMethod.DELETE)) { //TODO map passt nich
            try {
                clientDataService.deleteData(viewGroupLV.getNotMatchedStrucPathMap().get(path).get(HttpMethod.DELETE).getPath()
                        , pathVariables, queryParameters);
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
            }
        }
    }

    @Override
    public void putAction(String path, MultiValueMap<String, String> queryParameters, Map<String, String> pathParams, DataSchema properties) {
        if (viewGroupLV.getNotMatchedStrucPathMap().containsKey(path) && viewGroupLV.getNotMatchedStrucPathMap().get(path).containsKey(HttpMethod.PUT)) {
            try {
                clientDataService.putData(viewGroupLV.getNotMatchedStrucPathMap().get(path).get(HttpMethod.PUT), properties, queryParameters, pathParams);
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
            }
        }
    }

    @Override
    public void applyPathParams(String path, MultiValueMap<String, String> pathParams) {

        Map<String, Integer> multiplePathParamCounter = new HashMap<>();
        String[] replaceString = {path};
        pathParams.forEach((k, v) -> {
            if (!multiplePathParamCounter.containsKey(k))
                multiplePathParamCounter.put(k, 0);

            if (replaceString[0].contains("{" + k + "}")) {
                replaceString[0] = replaceString[0].replace("{" + k + "}", v.get(multiplePathParamCounter.get(k)));
                multiplePathParamCounter.put(k, multiplePathParamCounter.get(k) + 1);
            }
        });

        navigateFromListView(replaceString[0]);
    }
}
