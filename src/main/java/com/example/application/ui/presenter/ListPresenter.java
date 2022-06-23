package com.example.application.ui.presenter;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucViewGroupLV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.view.ListView;
import com.vaadin.flow.component.Component;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class ListPresenter implements ListView.LActionListener{
    private ListView view;
    private final StrucViewGroupLV strucViewGroup;
    private final ClientDataService clientDataService;

    public ListPresenter(ClientDataService clientDataService,StrucViewGroupLV strucViewGroup) {
        this.clientDataService = clientDataService;
        this.strucViewGroup = strucViewGroup;
    }


    public Component getView() {
        view = new ListView(this, strucViewGroup.getTagName(), strucViewGroup.getPrimaryPaths(), strucViewGroup.getSecondaryPaths(), strucViewGroup.getStrucSchemaMap(), strucViewGroup.getStrucPathMap()); //übergeben: pfade
        view.setData();
        return view;
    }

    @Override
    public void openPostDialog(String path) {
        StrucPath strucPath = strucViewGroup.getStrucPathMap().get(path).get(HttpMethod.POST);
        view.openPostDialog(strucPath.getRequestStrucSchema(),strucPath);
    }

    @Override
    public void postAction(String path, Map<String, String> queryParameters, DataSchema properties) {
        if (strucViewGroup.getStrucPathMap().containsKey(HttpMethod.POST)) {
            clientDataService.postData(strucViewGroup.getStrucPathMap().get(path).get(HttpMethod.POST), properties);
        }
    }
}
