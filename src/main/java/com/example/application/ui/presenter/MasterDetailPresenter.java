package com.example.application.ui.presenter;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.view.MasterDetailView;
import com.vaadin.flow.component.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Slf4j
public class MasterDetailPresenter implements MasterDetailView.MDActionListener {

    private final ClientDataService clientDataService;
    private MasterDetailView view;
    public StrucViewGroupMDV strucViewGroup;

    public MasterDetailPresenter(ClientDataService clientDataService, StrucViewGroupMDV strucViewGroup) {
        this.clientDataService = clientDataService;
        this.strucViewGroup = strucViewGroup;

        if (!strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.GET) || !strucViewGroup.getStrucPathMap().containsKey(HttpMethod.GET))
            log.error("Master Detail Presenter created with no Get path or schema: {}", strucViewGroup.getTagName());
    }

    public Component getView() {
        if (strucViewGroup.isPaged()) {
            view = new MasterDetailView(this, true,
                    strucViewGroup.getBehindPagedGetSchema(),
                    strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.POST),
                    strucViewGroup.getStrucSchemaMap().get(HttpMethod.PUT),
                    strucViewGroup.getStrucPathMap().containsKey(HttpMethod.DELETE)); //übergeben: pfade
            view.setData(clientDataService.getData(strucViewGroup.getStrucPathMap().get(HttpMethod.GET), strucViewGroup.getBehindPagedGetSchema()));
        }
        else {
            view = new MasterDetailView(this, false,
                    strucViewGroup.getStrucSchemaMap().get(HttpMethod.GET),
                    strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.POST),
                    strucViewGroup.getStrucSchemaMap().get(HttpMethod.PUT),
                    strucViewGroup.getStrucPathMap().containsKey(HttpMethod.DELETE)); //übergeben: pfade
            view.setData(clientDataService.getData(strucViewGroup.getStrucPathMap().get(HttpMethod.GET), strucViewGroup.getBehindPagedGetSchema()));
        }
        return view;
    }


    @Override
    public void postAction(String path, Map<String, String> queryParameters, DataSchema properties) {
        if (strucViewGroup.getStrucPathMap().containsKey(HttpMethod.POST)) {
            clientDataService.postData(strucViewGroup.getStrucPathMap().get(HttpMethod.POST), properties);
        }
    }


    @Override
    public void openPostDialog() {
        view.openPostDialog(strucViewGroup.getStrucSchemaMap().get(HttpMethod.POST), strucViewGroup.getStrucPathMap().get(HttpMethod.POST));
    }

    @Override
    public void openDeleteDialog() {
        view.openDeleteDialog(strucViewGroup.getStrucPathMap().get(HttpMethod.DELETE));
    }

    @Override
    public void deleteAction(String path, Map<String, String> pathVariables) {
        if (strucViewGroup.getStrucPathMap().containsKey(HttpMethod.DELETE)) {
            //TODO QUERRY
            //enthaltener parameter (in pfad) raussuchen
            String firstParam = path.split("\\{")[1].split("}")[0];
            //gucken ob param in dataSchema enthalten ist
            //löschanfrage senden
            clientDataService.deleteData(strucViewGroup.getStrucPathMap().get(HttpMethod.DELETE), pathVariables);
        }
    }
}
