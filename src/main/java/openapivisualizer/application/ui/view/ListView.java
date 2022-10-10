package openapivisualizer.application.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import openapivisualizer.application.generation.structuremodel.StrucPath;
import openapivisualizer.application.generation.structuremodel.TagGroupMD;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class ListView extends View {

    public interface LActionListener {
        void openPostDialog(String path);

        void openDeleteDialog(String path);

        void openPutDialog(String path);

        void navigateFromListView(String path);
    }

    private final LActionListener actionListener;

    public ListView(String tag, boolean showAllPaths, LActionListener actionListener, Map<String, TagGroupMD> mdvGroups, Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        super(tag);
        this.actionListener = actionListener;

        add(createListContent(showAllPaths, mdvGroups, strucPathMap));
    }

    private VerticalLayout createListContent(boolean showAllPaths, Map<String, TagGroupMD> mdvGroups, Map<String, Map<HttpMethod, StrucPath>> strucPathMap) {
        VerticalLayout verticalLayout = new VerticalLayout();

        if (!showAllPaths)
            mdvGroups.forEach((path, v) -> verticalLayout.add(createMDVComponent("GET: " + path, path)));

        strucPathMap.forEach((path, v) -> v.forEach((httpMethod, strucPath) -> {
            switch (httpMethod) {
                case POST -> verticalLayout.add(createPostListComponent("POST: " + path, path));
                case DELETE -> verticalLayout.add(createDeleteComponent("DELETE: " + path, path));
                case GET -> verticalLayout.add(createMDVComponent("GET: " + path, path));
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
        componentClicker.addClickListener(event -> actionListener.navigateFromListView(path));
        return componentClicker;
    }

    private Component createPostListComponent(String text, String path) {
        Button componentClicker = new Button(text);
        componentClicker.addClickListener(event -> actionListener.openPostDialog(path));
        return componentClicker;
    }

    private Component createPutListComponent(String text, String path) {
        Button componentClicker = new Button(text);
        componentClicker.addClickListener(event -> actionListener.openPutDialog(path));
        return componentClicker;
    }

    private Component createDeleteComponent(String text, String path) {
        Button componentClicker = new Button(text);
        componentClicker.addClickListener(event -> actionListener.openDeleteDialog(path));
        return componentClicker;
    }
}
