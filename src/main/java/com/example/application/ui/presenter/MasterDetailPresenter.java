package com.example.application.ui.presenter;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import com.example.application.rest.client.ClientDataService;
import com.example.application.ui.view.MasterDetailView;
import com.vaadin.flow.component.Component;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Slf4j
public class MasterDetailPresenter implements MasterDetailView.MDActionListener {

    private final ClientDataService clientDataService;
    private MasterDetailView view;
    @Getter
    public StrucViewGroupMDV strucViewGroup;

    public MasterDetailPresenter(ClientDataService clientDataService, StrucViewGroupMDV strucViewGroup) {
        this.clientDataService = clientDataService;
        this.strucViewGroup = strucViewGroup;

        if (!strucViewGroup.getStrucSchemaMap().containsKey(HttpMethod.GET) || !strucViewGroup.getStrucPathMap().containsKey(HttpMethod.GET))
            log.error("Master Detail Presenter created with no Get path or schema: {}", strucViewGroup.getTagName());
    }

    public Component getView() {
        if (strucViewGroup.isPaged())
            view = new MasterDetailView(this, true, strucViewGroup.getBehindPagedGetSchema(), strucViewGroup.getStrucSchemaMap().get(HttpMethod.POST), strucViewGroup.getStrucSchemaMap().get(HttpMethod.PUT)); //übergeben: pfade
        else
            view = new MasterDetailView(this, false, strucViewGroup.getStrucSchemaMap().get(HttpMethod.GET), strucViewGroup.getStrucSchemaMap().get(HttpMethod.POST), strucViewGroup.getStrucSchemaMap().get(HttpMethod.PUT)); //übergeben: pfade

        view.setData(clientDataService.getData(strucViewGroup.getStrucPathMap().get(HttpMethod.GET), strucViewGroup.getBehindPagedGetSchema()));
        return view;
    }


    @Override
    public void postAction(Map<String, String> queryParameters, DataSchema properties) {
        //TODO convert from DataSchema to json thing with jackson
        if(strucViewGroup.getStrucPathMap().containsKey(HttpMethod.POST)){
            clientDataService.postData(strucViewGroup.getStrucPathMap().get(HttpMethod.POST),properties);
        }
    }


    @Override
    public void openPostDialog() {
        view.openDialog(strucViewGroup.getStrucSchemaMap().get(HttpMethod.POST), strucViewGroup.getStrucPathMap().get(HttpMethod.POST));
    }

    @Override
    public void deleteData() {
        //TODO
    }
}
