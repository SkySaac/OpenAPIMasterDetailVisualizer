package openapivisualizer.application.ui.presenter;

import com.vaadin.flow.component.html.Div;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.generation.structuremodel.TagGroupMD;
import openapivisualizer.application.rest.client.ClientDataService;
import openapivisualizer.application.rest.client.RequestException;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.ui.components.DeleteDialog;
import openapivisualizer.application.ui.components.PostDialog;
import openapivisualizer.application.ui.components.PutDialog;
import openapivisualizer.application.ui.components.SettingsDialog;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;
import openapivisualizer.application.ui.service.NotificationService;
import openapivisualizer.application.ui.view.MasterDetailView;
import openapivisualizer.application.ui.view.View;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class MasterDetailPresenter implements MasterDetailView.MDActionListener, SettingsDialog.SettingsActionListener,
        PostDialog.PostActionListener, DeleteDialog.DeleteActionListener, PutDialog.PutActionListener {

    private MasterDetailView view;
    private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private List<SettingsDialog.ColumnGridElement> columnsSettings = null;
    private String currentWrappedPath = "";

    private final ClientDataService clientDataService;
    private final NotificationService notificationService;
    private final DetailLayout.NavigationListener navigationListener;
    @Getter
    private final TagGroupMD tagGroupMD;

    private final Map<String, MasterDetailPresenter> relationPresenter = new HashMap<>();
    private MasterDetailPresenter uriMasterDetailPresenter = null;


    public MasterDetailPresenter(NotificationService notificationService, DetailLayout.NavigationListener navigationListener, ClientDataService clientDataService, TagGroupMD tagGroup) {
        this.clientDataService = clientDataService;
        this.navigationListener = navigationListener;
        this.notificationService = notificationService;
        this.tagGroupMD = tagGroup;

        if (!tagGroup.getApiPathMap().containsKey(HttpMethod.GET))
            log.error("Master Detail Presenter created with no Get path or schema: {}", tagGroup.getTagName());

        createNewView();

        if (tagGroup.getUriTagGroup() != null)
            uriMasterDetailPresenter = new MasterDetailPresenter(notificationService, navigationListener, clientDataService, tagGroup.getUriTagGroup());

        tagGroup.getRelationTagGroup().forEach((key, value) -> {
            relationPresenter.put(key, new MasterDetailPresenter(notificationService, navigationListener, clientDataService, value));
        });

    }


    public View getView() {
        return getView(new HashMap<>());
    }


    public View getIfHasTargetView(String path) {
        String[] splittedPath = path.split("/");

        //what if part of internal presenter
        Map<String, String> pathParams = new HashMap<>();
        for (Map.Entry<String, MasterDetailPresenter> presenterEntry : relationPresenter.entrySet()) {
            String[] splittedPresenterPath = presenterEntry.getKey().split("/");
            if (splittedPath.length == splittedPresenterPath.length) {
                boolean matching = true;
                for (int i = 0; i < splittedPath.length; i++) {
                    if (!splittedPresenterPath[i].startsWith("{") && !splittedPath[i].equals(splittedPresenterPath[i])
                    ) {
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

        //what if secondary path
        if (tagGroupMD.getUriTagGroup() != null) {
            String secondaryPath = tagGroupMD.getUriTagGroup().getApiPathMap().get(HttpMethod.GET).getPath();
            //checking the secondary path here
            if (secondaryPath != null) {
                String[] splittedPresenterPath = secondaryPath.split("/");
                if (splittedPath.length == splittedPresenterPath.length) {
                    boolean matching = true;
                    for (int i = 0; i < splittedPath.length; i++) {
                        if (!splittedPresenterPath[i].startsWith("{") && !splittedPath[i].equals(splittedPresenterPath[i])
                        ) {
                            matching = false;
                            break;
                        } else if (splittedPresenterPath[i].startsWith("{")) {
                            pathParams.put(splittedPresenterPath[i].substring(1, splittedPresenterPath[i].length() - 1), splittedPath[i]);
                        }
                    }
                    if (matching) {
                        return uriMasterDetailPresenter.getView(pathParams);
                    } else {
                        pathParams.clear();
                    }
                }
            }
        }

        //what if primaryPath with {}
        if (tagGroupMD.getApiPathMap().get(HttpMethod.GET).getPath().contains("{")) {
            String primaryPath = tagGroupMD.getApiPathMap().get(HttpMethod.GET).getPath();
            //checking the secondary path here
            if (primaryPath != null) {
                String[] splittedPresenterPath = primaryPath.split("/");
                if (splittedPath.length == splittedPresenterPath.length) {
                    boolean matching = true;
                    for (int i = 0; i < splittedPath.length; i++) {
                        if (!splittedPresenterPath[i].startsWith("{") && !splittedPath[i].equals(splittedPresenterPath[i])
                        ) {
                            matching = false;
                            break;
                        } else if (splittedPresenterPath[i].startsWith("{")) {
                            pathParams.put(splittedPresenterPath[i].substring(1, splittedPresenterPath[i].length() - 1), splittedPath[i]);
                        }
                    }
                    if (matching) {
                        return getView(pathParams);
                    } else {
                        pathParams.clear();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void openPostDialog() {
        PostDialog postDialog = new PostDialog(this);
        postDialog.open(tagGroupMD.getApiPathMap().get(HttpMethod.POST), pathParams);
    }

    @Override
    public void openPutDialog() {
        PutDialog putDialog = new PutDialog(this);
        putDialog.open(tagGroupMD.getApiPathMap().get(HttpMethod.PUT), pathParams);
    }

    @Override
    public void openDeleteDialog() {
        DeleteDialog deleteDialog = new DeleteDialog(this);
        deleteDialog.open(tagGroupMD.getApiPathMap().get(HttpMethod.DELETE), pathParams);
    }

    @Override
    public void openSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.open(tagGroupMD.getApiPathMap().get(HttpMethod.GET), columnsSettings, currentWrappedPath);
    }

    @Override
    public void setInitialColumnSettings(List<SettingsDialog.ColumnGridElement> initialColumnSettings) {
        this.columnsSettings = initialColumnSettings;
    }

    @Override
    public void refreshData() {
        try {
            view.setData(clientDataService.getData(tagGroupMD.getApiPathMap().get(HttpMethod.GET),
                    currentWrappedPath, pathParams, queryParams));
            if (columnsSettings != null)
                setGridColumnSettings(columnsSettings);
        } catch (RequestException | ResourceAccessException e) {
            log.error("Error trying to access: {}", e.getMessage());
            e.printStackTrace();
            notificationService.postNotification("Requesting data failed: " + e.getMessage(), true);
        }
    }

    @Override
    public void dataCorupted() {
        notificationService.postNotification("Data not equaling OpenAPI Specification",true);
    }

    @Override
    public void postAction(String path, MultiValueMap<String, String> queryParameters, Map<String, String> pathVariables, DataSchema body) {
        if (tagGroupMD.getApiPathMap().containsKey(HttpMethod.POST)) {
            try {
                clientDataService.postData(tagGroupMD.getApiPathMap().get(HttpMethod.POST), body, queryParameters, pathVariables);
                refreshData();
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Error trying to connect to server.", true);
            }
        }
    }

    @Override
    public void deleteAction(String path, Map<String, String> pathVariables, MultiValueMap<String, String> queryParameters) {
        if (tagGroupMD.getApiPathMap().containsKey(HttpMethod.DELETE)) {
            //enthaltener parameter (in pfad) raussuchen
            String firstParam = path.split("\\{")[1].split("}")[0];
            //gucken ob param in dataSchema enthalten ist
            //löschanfrage senden
            try {
                clientDataService.deleteData(tagGroupMD.getApiPathMap().get(HttpMethod.DELETE).getPath(),
                        pathVariables, queryParameters);
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Error trying to connect to server.", true);
            }
        }
        refreshData();
    }

    @Override
    public void putAction(String path, MultiValueMap<String, String> queryParameters, Map<String, String> pathParams, DataSchema properties) {
        if (tagGroupMD.getApiPathMap().containsKey(HttpMethod.PUT)) {
            try {
                clientDataService.putData(tagGroupMD.getApiPathMap().get(HttpMethod.PUT), properties, queryParameters, pathParams);
                refreshData();
            } catch (ResourceAccessException e) {
                log.error("Error trying to access: {}", e.getMessage());
                e.printStackTrace();
                notificationService.postNotification("Error trying to connect to server.", true);
            }
        }
    }

    @Override
    public void setQueryParams(MultiValueMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public void setGridColumnSettings(List<SettingsDialog.ColumnGridElement> columnSetting) {
        this.columnsSettings = columnSetting;
        view.setColumnSettings(columnSetting);
    }

    @Override
    public void setWrappedSchemaPath(String pathToSchema) {

        currentWrappedPath = pathToSchema;
        columnsSettings = null;
        MasterDetailView oldView = view;
        createNewView();

        if (oldView.getParent().isPresent()) {
            Div masterDetailRoute = (Div) oldView.getParent().get();
            masterDetailRoute.replace(oldView, view);
        }

    }

    @Override
    public void refreshFromSettings() {
        refreshData();
    }

    public View getView(Map<String, String> pathParams) {
        this.pathParams = pathParams;

        refreshData();
        return view;
    }

    public void createNewView() {
        StrucSchema shownGetSchema = tagGroupMD.getApiPathMap().get(HttpMethod.GET).getResponseStrucSchema();

        if (!Objects.equals(currentWrappedPath, "") && currentWrappedPath.split("/").length != 0) {
            String[] wrappedPath = currentWrappedPath.split("/");

            StrucSchema tempSchema = shownGetSchema;
            for (String attributeName : wrappedPath) {
                if (tempSchema.getStrucValue().getProperties().containsKey(attributeName)) {
                    tempSchema = tempSchema.getStrucValue().getProperties().get(attributeName);
                } else {
                    tempSchema = null;
                    notificationService.postNotification("WrappedSchemaPath is not valid", true);
                    break;
                }
            }
            if (tempSchema != null) {
                if (tempSchema.getStrucValue().getType().equals(DataPropertyType.ARRAY))
                    shownGetSchema = tempSchema.getStrucValue().getArrayElements().get(0);
                else
                    shownGetSchema = tempSchema;
            }

        }
        view = new MasterDetailView(tagGroupMD.getTagName(), navigationListener, this,
                shownGetSchema,
                tagGroupMD.getApiPathMap().containsKey(HttpMethod.POST),
                tagGroupMD.getApiPathMap().containsKey(HttpMethod.PUT),
                tagGroupMD.getApiPathMap().containsKey(HttpMethod.DELETE), false);
    }
}
