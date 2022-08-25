package com.example.application.ui.view;

import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucViewGroupMDV;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class ListView extends Div {

    public interface LActionListener {
        void openPostDialog(String path);

        void openDeleteDialog(String path);

        void openPutDialog(String path);

        void navigateFromListView(String path);
    }

    private final LActionListener actionListener;

    public ListView(String tagName, LActionListener actionListener, Map<String, StrucViewGroupMDV> mdvGroups, Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        this.actionListener = actionListener;

        add(createListContent(mdvGroups, strucPathMap));
    }

    private VerticalLayout createListContent(Map<String, StrucViewGroupMDV> mdvGroups, Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        VerticalLayout verticalLayout = new VerticalLayout();

        mdvGroups.forEach((path, v) -> verticalLayout.add(createMDVComponent("GET: " + path, path)));

        strucPathMap.forEach((path, v) -> v.forEach((httpMethod, strucPath) -> {
            switch (httpMethod) {
                case POST -> verticalLayout.add(createPostListComponent("POST: " + path, path));
                case DELETE -> verticalLayout.add(createDeleteComponent("DELETE: " + path, path));
                case GET -> verticalLayout.add(createMDVComponent("GET: " + path, path)); //TODO überhaupt nötig?
                case PUT -> verticalLayout.add(createPutListComponent("PUT: " + path, path));
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
}
