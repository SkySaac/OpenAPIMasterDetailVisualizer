package openapivisualizer.application.ui.presenter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.generation.structuremodel.ViewGroupMDV;
import openapivisualizer.application.rest.client.ClientDataService;
import openapivisualizer.application.rest.client.RequestException;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.ui.components.DeleteDialog;
import openapivisualizer.application.ui.components.PostDialog;
import openapivisualizer.application.ui.components.PutDialog;
import openapivisualizer.application.ui.components.SettingsDialog;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;
import openapivisualizer.application.ui.controller.NotificationService;
import openapivisualizer.application.ui.view.MasterDetailView;
import openapivisualizer.application.ui.view.View;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MasterDetailSecondaryPresenter implements MasterDetailView.MDActionListener, SettingsDialog.SettingsActionListener,DeleteDialog.DeleteActionListener, PutDialog.PutActionListener {

    private final ClientDataService clientDataService;
    private final NotificationService notificationService;
    private final DetailLayout.NavigationListener navigationListener;
    @Getter
    private final ViewGroupMDV strucViewGroup;
    private final String tag;

    private MasterDetailView view;
    private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private List<SettingsDialog.ColumnGridElement> columnsSettings = null;
    private String currentWrappedPath = null;


    public MasterDetailSecondaryPresenter(String tag,NotificationService notificationService, DetailLayout.NavigationListener navigationListener, ClientDataService clientDataService, ViewGroupMDV viewGroup) {
        this.clientDataService = clientDataService;
        this.navigationListener = navigationListener;
        this.notificationService = notificationService;
        this.strucViewGroup = viewGroup;
        this.tag = tag;

        if (!viewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.GET))
            log.error("Secondary Master Detail Presenter created with no Get path or schema: {}", viewGroup.getTagName());

        createNewView();

    }

    private void createNewView() {
        StrucSchema shownGetSchema = strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET).getResponseStrucSchema();

        if (currentWrappedPath != null && currentWrappedPath.split(",").length != 0) {
            String[] wrappedPath = currentWrappedPath.split(",");

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
                    shownGetSchema = tempSchema.getStrucValue().getArrayElements().get(0); //TODO is ja oneOf
                else
                    shownGetSchema = tempSchema;
            }

        }
        view = new MasterDetailView(strucViewGroup.getTagName(),navigationListener, this,
                shownGetSchema,
                false, strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.PUT),
                strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.DELETE),true);
    }

    public View getView(Map<String, String> pathParams) {
        this.pathParams = pathParams;

        refreshData();
        return view;
    }

    @Override
    public void openPostDialog() {
        //NOTHING AS IT CANT EXIST HERE
    }

    @Override
    public void openPutDialog() {
        PutDialog putDialog = new PutDialog(this);
        putDialog.open(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.PUT), pathParams);
    }

    @Override
    public void openDeleteDialog() {
        DeleteDialog deleteDialog = new DeleteDialog(this);
        deleteDialog.open(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.DELETE), pathParams);
    }

    @Override
    public void openSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.open(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET), columnsSettings, currentWrappedPath);
    }

    @Override
    public void setInitialColumnSettings(List<SettingsDialog.ColumnGridElement> initialColumnSettings) {
        this.columnsSettings = initialColumnSettings;
    }

    @Override
    public void refreshData() {
        try {
            view.setData(clientDataService.getData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.GET),
                    currentWrappedPath, pathParams, queryParams));
            if (columnsSettings != null)
                setGridColumnSettings(columnsSettings);
        } catch (RequestException | ResourceAccessException e) {
            log.error("Error trying to access: {}", e.getMessage());
            e.printStackTrace();
            notificationService.postNotification("Datenabruf fehlgeschlagen: " + e.getMessage(), true);
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
    public void putAction(String path, MultiValueMap<String, String> queryParameters, Map<String, String> pathParams, DataSchema properties) {
        if (strucViewGroup.getPrimaryStrucPathMap().containsKey(HttpMethod.PUT)) {
            try {
                clientDataService.putData(strucViewGroup.getPrimaryStrucPathMap().get(HttpMethod.PUT), properties, queryParameters, pathParams);
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
    }

    @Override
    public void setGridColumnSettings(List<SettingsDialog.ColumnGridElement> columnSetting) {
        this.columnsSettings = columnSetting;
        view.setColumnSettings(columnSetting);
    }

    @Override
    public void setWrappedSchemaPath(String pathToSchema) {
        if(pathToSchema!=null) {
            currentWrappedPath = pathToSchema;
            columnsSettings = null;
            MasterDetailView oldView = view;
            createNewView();

            if (oldView.getParent().isPresent()) {
                Div masterDetailRoute = (Div) oldView.getParent().get();
                masterDetailRoute.replace(oldView, view);
            }
        }
    }

    @Override
    public void refreshFromSettings() {
        refreshData();
    }
}

