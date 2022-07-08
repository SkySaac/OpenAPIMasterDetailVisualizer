package com.example.application.ui.view;

import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.components.PostDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

public class ListView extends Div {

    public interface LActionListener extends PostDialog.PostActionListener {
        void openPostDialog(String path);
    }

    private final LActionListener actionListener;

    public ListView(LActionListener actionListener, String name, List<String> primaryPaths, Map<String, String> secondaryPaths,
                    Map<String, StrucSchema> strucSchemaMap, Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        this.actionListener = actionListener;
        add(new Label("Tag: " + name));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add("PrimaryPaths");
        primaryPaths.forEach(prim -> horizontalLayout.add(new Label(prim)));
        add(horizontalLayout);

        HorizontalLayout horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.add("SecondaryPaths");
        secondaryPaths.forEach((k, v) -> horizontalLayout1.add(new Label(k + "," + v)));
        add(horizontalLayout1);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.add("strucSchemaMap");
        strucSchemaMap.forEach((k, v) -> horizontalLayout2.add(new Label(k)));
        add(horizontalLayout2);


        add(createContent(strucPathMap));
    }

    private VerticalLayout createContent(Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        VerticalLayout verticalLayout = new VerticalLayout();

        strucPathMap.forEach((path, v) -> {

            v.forEach((httpMethod, strucPath) -> {

                switch (httpMethod) {
                    case POST ->
                        verticalLayout.add(createPostListComponent("POST: " + path, path));

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

    private Component createPostListComponent(String text, String path) {
        Button componentClicker = new Button(text);
        //TODO was wenn externalSchema
        componentClicker.addClickListener(event -> actionListener.openPostDialog(path));
        return componentClicker;
    }

    public void setData() {

    }

    public void openPostDialog(StrucSchema schema, StrucPath strucPath) {
        PostDialog postDialog = new PostDialog(actionListener);
        postDialog.open(schema, strucPath);
    }
}
