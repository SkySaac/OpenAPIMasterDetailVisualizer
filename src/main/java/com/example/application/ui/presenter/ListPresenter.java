package com.example.application.ui.presenter;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucViewGroupLV;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.view.ListView;
import com.vaadin.flow.component.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ListPresenter implements ListView.LActionListener{
    private ListView view;

    private final Map<String, MasterDetailPresenter> internalMasterDetailPresenters = new HashMap<>();

    private final StrucViewGroupLV strucViewGroup;
    private final ClientDataService clientDataService;

    public ListPresenter(ClientDataService clientDataService,StrucViewGroupLV strucViewGroup) {
        this.clientDataService = clientDataService;
        this.strucViewGroup = strucViewGroup;

        strucViewGroup.getStrucViewGroupMDVS().forEach((key,svgmdv) -> registerMDVPresenter(svgmdv));
    }


    public Component getView() {
        view = new ListView(strucViewGroup.getTagName(),this, strucViewGroup.getNotMatchedStrucPathMap()); //übergeben: pfade
        return view;
    }

    public void registerMDVPresenter(StrucViewGroupMDV strucViewGroupMDV){
        log.info("Registering Master-Detail Presenter for the {} view", strucViewGroupMDV.getTagName());

        MasterDetailPresenter masterDetailPresenter = new MasterDetailPresenter(clientDataService, strucViewGroupMDV);

        internalMasterDetailPresenters.put(strucViewGroupMDV.getStrucPathMap().get(HttpMethod.GET).getPath(), masterDetailPresenter);
    }

    @Override
    public void openPostDialog(String path) {
        StrucPath strucPath = strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.POST);
        view.openPostDialog(strucPath.getRequestStrucSchema(),strucPath);
    }

    @Override
    public void openDeleteDialog(String path) {
        StrucPath strucPath = strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.DELETE);
        view.openDeleteDialog(strucPath);
    }

    @Override
    public void openInternalMDV(String path) {
        //TODO create MDV
        MasterDetailPresenter masterDetailPresenter = internalMasterDetailPresenters.get(path); //TODO add view
        //add MDV as view
        view.openMDVView(masterDetailPresenter.getView());
    }

    @Override
    public void postAction(String path, Map<String, String> queryParameters, DataSchema properties) {
        if (strucViewGroup.getNotMatchedStrucPathMap().containsKey(HttpMethod.POST)) {
            clientDataService.postData(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.POST), properties);
        }
    }

    @Override
    public void deleteAction(String path, Map<String, String> pathVariables) {
        if (strucViewGroup.getNotMatchedStrucPathMap().containsKey(HttpMethod.DELETE)) {
            clientDataService.deleteData(strucViewGroup.getNotMatchedStrucPathMap().get(path).get(HttpMethod.DELETE), pathVariables);
        }
    }
}
