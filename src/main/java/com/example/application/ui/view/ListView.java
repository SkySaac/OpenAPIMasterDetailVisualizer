package com.example.application.ui.view;

import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.components.DeleteDialog;
import com.example.application.ui.components.PostDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

public class ListView extends Div {

    public interface LActionListener extends PostDialog.PostActionListener, DeleteDialog.DeleteActionListener {
        void openPostDialog(String path);

        void openDeleteDialog(String path);

        void openInternalMDV(String path);
    }

    private final LActionListener actionListener;
    private final VerticalLayout listDrawer; //contains the list
    private final String tagName;

    private Component currentMasterDetailView = null;
    private Component currentNavigateButton = null;

    public ListView(String tagName, LActionListener actionListener, Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        this.actionListener = actionListener;
        this.tagName = tagName;

        listDrawer = createListDrawer(strucPathMap);
        add(listDrawer);
    }

    private Button createNavigateButton(){
        Button navigateBackButton = new Button(new Icon(VaadinIcon.BACKWARDS));
        navigateBackButton.addClickListener(e -> this.closeMDVView());
        return navigateBackButton;
    }

    private VerticalLayout createListDrawer(Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        VerticalLayout listDrawer = new VerticalLayout();

        listDrawer.add(createListContent(strucPathMap));

        return listDrawer;
    }

    private VerticalLayout createListContent(Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        VerticalLayout verticalLayout = new VerticalLayout();

        strucPathMap.forEach((path, v) -> {

            v.forEach((httpMethod, strucPath) -> {
                switch (httpMethod) {
                    case POST -> verticalLayout.add(createPostListComponent("POST: " + path, path));
                    case DELETE -> verticalLayout.add(createDeleteComponent("DELETE: " + path, path));
                    case GET -> verticalLayout.add(createMDVComponent("GET: " + path, path));
                    default -> {
                        HorizontalLayout horizontalLayout = new HorizontalLayout();
                        horizontalLayout.add(path);
                        horizontalLayout.add(new Label(httpMethod.toString()));
                        verticalLayout.add(horizontalLayout);

                    }
                }

            });
        });
        return verticalLayout;
    }

    public void openMDVView(Component masterDetailView){
        this.remove(listDrawer); //TODO instead make totally view in presenter
        this.currentMasterDetailView = masterDetailView;
        this.currentNavigateButton = createNavigateButton();
        this.add(currentNavigateButton);
        this.add(masterDetailView);
    }

    public void closeMDVView(){
        this.remove(currentMasterDetailView);
        this.remove(currentNavigateButton);
        this.add(listDrawer);
    }

    private Component createMDVComponent(String text, String path) {
        Button componentClicker = new Button(text);
        //TODO was wenn externalSchema
        componentClicker.addClickListener(event -> actionListener.openInternalMDV(path));
        return componentClicker;
    }

    private Component createPostListComponent(String text, String path) {
        Button componentClicker = new Button(text);
        //TODO was wenn externalSchema
        componentClicker.addClickListener(event -> actionListener.openPostDialog(path));
        return componentClicker;
    }

    private Component createDeleteComponent(String text, String path) {
        Button componentClicker = new Button(text);
        //TODO was wenn externalSchema
        componentClicker.addClickListener(event -> actionListener.openDeleteDialog(path));
        return componentClicker;
    }

    public void openPostDialog(StrucSchema schema, StrucPath strucPath) {
        PostDialog postDialog = new PostDialog(actionListener);
        postDialog.open(schema, strucPath);
    }

    public void openDeleteDialog(StrucPath strucPath) {
        DeleteDialog deleteDialog = new DeleteDialog(actionListener);
        deleteDialog.open(strucPath);
    }
}
