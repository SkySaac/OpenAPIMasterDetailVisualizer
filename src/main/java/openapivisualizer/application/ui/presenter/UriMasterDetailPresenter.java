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

@Slf4j
public class UriMasterDetailPresenter extends MDPresenter {


    public UriMasterDetailPresenter(NotificationService notificationService, DetailLayout.NavigationListener navigationListener, ClientDataService clientDataService, TagGroupMD viewGroup) {
        super(notificationService,navigationListener,clientDataService,viewGroup);


    }




}

