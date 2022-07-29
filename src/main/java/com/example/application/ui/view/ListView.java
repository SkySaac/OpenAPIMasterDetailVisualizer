package com.example.application.ui.view;

import com.example.application.data.structureModel.StrucPath;
import com.example.application.ui.components.DeleteDialog;
import com.example.application.ui.components.PostDialog;
import com.example.application.ui.components.PutDialog;
import com.example.application.ui.components.detaillayout.DetailLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class ListView extends Div {

    public interface LActionListener extends PostDialog.PostActionListener, DeleteDialog.DeleteActionListener, PutDialog.PutActionListener {
        void openPostDialog(String path);

        void openDeleteDialog(String path);

        void openPutDialog(String path);

        void navigateFromListView(String path);
    }

    private final LActionListener actionListener;

    public ListView(String tagName, LActionListener actionListener, Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        this.actionListener = actionListener;

        add(createListContent(strucPathMap));
    }

    private VerticalLayout createListContent(Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        VerticalLayout verticalLayout = new VerticalLayout();

        strucPathMap.forEach((path, v) -> v.forEach((httpMethod, strucPath) -> {
            switch (httpMethod) {
                case POST -> verticalLayout.add(createPostListComponent("POST: " + path, path));
                case DELETE -> verticalLayout.add(createDeleteComponent("DELETE: " + path, path));
                case GET -> verticalLayout.add(createMDVComponent("GET: " + path, path));
                case PUT -> verticalLayout.add(createPostListComponent("PUT: " + path, path));
                default -> {
                    HorizontalLayout horizontalLayout = new HorizontalLayout();
                    horizontalLayout.add(path);
                    horizontalLayout.add(new Label(httpMethod.toString()));
                    verticalLayout.add(horizontalLayout);

                }
            }

        }));
        return verticalLayout;
    }

    private Component createMDVComponent(String text, String path) {
        Button componentClicker = new Button(text);
        //TODO was wenn externalSchema
        componentClicker.addClickListener(event -> actionListener.navigateFromListView(path));
        return componentClicker;
    }

    private Component createPostListComponent(String text, String path) {
        Button componentClicker = new Button(text);
        //TODO was wenn externalSchema
        componentClicker.addClickListener(event -> actionListener.openPostDialog(path));
        return componentClicker;
    }

    private Component createPutListComponent(String text, String path) {
        Button componentClicker = new Button(text);
        //TODO was wenn externalSchema
        componentClicker.addClickListener(event -> actionListener.openPutDialog(path));
        return componentClicker;
    }

    private Component createDeleteComponent(String text, String path) {
        Button componentClicker = new Button(text);
        //TODO was wenn externalSchema
        componentClicker.addClickListener(event -> actionListener.openDeleteDialog(path));
        return componentClicker;
    }

    public void openPostDialog(StrucPath strucPath) {
        PostDialog postDialog = new PostDialog(actionListener);
        postDialog.open(strucPath);
    }

    public void openPutDialog(StrucPath strucPath) {
        PutDialog putDialog = new PutDialog(actionListener);
        putDialog.open(strucPath);
    }

    public void openDeleteDialog(StrucPath strucPath) {
        DeleteDialog deleteDialog = new DeleteDialog(actionListener);
        deleteDialog.open(strucPath);
    }
}
