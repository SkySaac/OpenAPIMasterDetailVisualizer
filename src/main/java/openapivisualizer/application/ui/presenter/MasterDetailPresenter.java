package openapivisualizer.application.ui.presenter;

import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.structuremodel.TagGroupMD;
import openapivisualizer.application.rest.client.ClientDataService;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;
import openapivisualizer.application.ui.service.NotificationService;
import openapivisualizer.application.ui.view.View;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MasterDetailPresenter extends MDPresenter {


    private final Map<String, MasterDetailPresenter> relationPresenter = new HashMap<>();
    private UriMasterDetailPresenter uriMasterDetailPresenter = null;


    public MasterDetailPresenter(NotificationService notificationService, DetailLayout.NavigationListener navigationListener, ClientDataService clientDataService, TagGroupMD tagGroup) {
        super(notificationService, navigationListener, clientDataService, tagGroup);

        if (tagGroup.getUriTagGroup() != null)
            uriMasterDetailPresenter = new UriMasterDetailPresenter(notificationService, navigationListener, clientDataService, tagGroup.getUriTagGroup());

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
}
