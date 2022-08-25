package openapivisualizer.application.ui.presenter;

import lombok.Getter;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.rest.client.ClientDataService;
import openapivisualizer.application.ui.components.DeleteDialog;
import openapivisualizer.application.ui.components.PostDialog;
import openapivisualizer.application.ui.components.PutDialog;
import openapivisualizer.application.ui.components.QueryParamDialog;
import openapivisualizer.application.ui.controller.NotificationService;
import openapivisualizer.application.generation.structuremodel.ViewGroupMDV;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;
import openapivisualizer.application.ui.view.MasterDetailView;
import com.vaadin.flow.component.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MasterDetailPresenter implements MasterDetailView.MDActionListener, QueryParamDialog.QueryActionListener,
        PostDialog.PostActionListener, DeleteDialog.DeleteActionListener, PutDialog.PutActionListener {

    private final ClientDataService clientDataService;
    private final NotificationService notificationService;
    private final DetailLayout.NavigationListener navigationListener;
    private final Map<String, MasterDetailPresenter> internalPresenters = new HashMap<>();
    private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private MasterDetailView view;
    @Getter
    private final ViewGroupMDV strucViewGroup;

    public MasterDetailPresenter(NotificationService notificationService, DetailLayout.NavigationListener navigationListener, ClientDataService clientDataService, ViewGroupMDV strucViewGroup) {
        this.clientDataService = clientDataService;
        this.notificationService = notificationService;
        this.navigationListener = navigationListener;
        this.strucViewGroup = strucViewGroup;

        if (!strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.GET) || !strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.GET))
            log.error("Master Detail Presenter created with no Get path or schema: {}", strucViewGroup.getTagName());

        strucViewGroup.getInternalMDVs().forEach((key, value) -> { //TODO if id is needed put id in path
            internalPresenters.put(key, new MasterDetailPresenter(notificationService, navigationListener, clientDataService, value));
        });

    }

    public Component getView() {
        return getView(new HashMap<>());
    }

    public Component getView(Map<String, String> pathParams) {
        StrucSchema shownGetSchema;

        if (strucViewGroup.isWrapped()) {
            shownGetSchema = strucViewGroup.getWrappedGetSchema(); //übergeben: pfade
        } else {
            shownGetSchema = strucViewGroup.getStrucSchemaMap().get(HttpMethod.GET);
        }

        this.pathParams = pathParams;

        view = new MasterDetailView(navigationListener, this, strucViewGroup.isWrapped(),
                shownGetSchema,
                strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.POST),
                strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.PUT),
                strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.DELETE),
                !strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET).getQueryParams().isEmpty());
        try {
            view.setData(clientDataService.getData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET),
                    strucViewGroup.getWrappedGetSchema(), pathParams, queryParams));
        } catch (ResourceAccessException e) {
            log.error("Error trying to access: {}", e.getMessage());
            e.printStackTrace();
            notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
        }
        return view;
    }


    @Override
    public void openPostDialog() {
        PostDialog postDialog = new PostDialog(this);
        postDialog.open(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.POST));
    }
    @Override
    public void openPutDialog() {
        PutDialog putDialog = new PutDialog(this);
        putDialog.open(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.PUT));
    }

    @Override
    public void openDeleteDialog() {
        DeleteDialog deleteDialog = new DeleteDialog(this);
        deleteDialog.open(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.DELETE));
    }

    @Override
    public void openQueryDialog() {
        QueryParamDialog queryParamDialog = new QueryParamDialog(this);
        queryParamDialog.open(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET));
    }

    @Override
    public void refreshData() {
        try {
            view.setData(clientDataService.getData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET),
                    strucViewGroup.getWrappedGetSchema(), pathParams, queryParams));
        } catch (ResourceAccessException e) {
            log.error("Error trying to access: {}", e.getMessage());
            e.printStackTrace();
            notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
        }
    }

    @Override
    public void postAction(String path, MultiValueMap<String, String> queryParameters,Map<String,String> pathVariables, DataSchema properties) {
        if (strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.POST)) {
            try {
                clientDataService.postData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.POST), properties, queryParameters,pathVariables);
                refreshData();
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
            }
        }
    }

    @Override
    public void deleteAction(String path, Map<String, String> pathVariables, MultiValueMap<String, String> queryParameters) {
        if (strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.DELETE)) {
            //enthaltener parameter (in pfad) raussuchen //TODO was das
            String firstParam = path.split("\\{")[1].split("}")[0];
            //gucken ob param in dataSchema enthalten ist
            //löschanfrage senden
            try {
                clientDataService.deleteData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.DELETE).getPath(),
                        pathVariables, queryParameters);
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
            }
        }
        refreshData();
    }

    @Override
    public void putAction(String path, MultiValueMap<String, String> queryParameters,Map<String,String> pathParams, DataSchema properties) {
        //TODO put functional

        if (strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.PUT)) {
            try { //TODO
                clientDataService.putData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.PUT), properties, queryParameters,pathParams);
                refreshData();
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Es konnte keine Verbindung zum Server hergestellt werden.", true);
            }
        }
    }

    @Override
    public void setQueryParams(MultiValueMap<String, String> queryParams) {
        this.queryParams = queryParams;
        refreshData();
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
        if (strucViewGroup.getSecondaryGetPath() != null) {
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
