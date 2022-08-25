package openapivisualizer.application.ui.components.detaillayout;

import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.ui.components.detaillayout.detailcomponents.ObjectComponent;
import openapivisualizer.application.ui.components.detaillayout.detailcomponents.DetailComponent;
import com.vaadin.flow.component.html.Div;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DetailLayout extends Div implements DetailSwitchListener, PathComponent.PathSwitchListener {

    public interface NavigationListener {
        void navigate(String path);
    }


    private final NavigationListener navigationListener;
    private DetailComponent currentDetailComponent;
    private final DetailComponent basicDetailComponent;

    private final PathComponent pathComponent;

    public DetailLayout(NavigationListener navigationListener, StrucSchema schema) {
        this.navigationListener = navigationListener;

        //Div editorLayoutDiv = new Div();
        this.setClassName("editor-layout"); //TODO css
        this.getStyle().set("padding-left", "10px");
        this.getStyle().set("padding-bottom", "10px");
        this.getStyle().set("padding-right", "10px");

        //this.setClassName("editor");
        //editorLayoutDiv.add(editorDiv);
        //TODO if is object
        basicDetailComponent = new ObjectComponent(schema != null ? schema.getName() : "Object", schema, this);
        currentDetailComponent = basicDetailComponent;

        //Creating base path element
        pathComponent = new PathComponent(this, schema.getName(), basicDetailComponent);

        add(pathComponent);
        add(currentDetailComponent);
    }


    public void clearDetailLayout() {
        basicDetailComponent.clearDetailLayout();
        pathComponent.clearPathElements();
        switchView(basicDetailComponent);
    }

    public void fillDetailLayout(DataSchema dataSchema) {
        switchView(basicDetailComponent);
        pathComponent.clearPathElements();
        currentDetailComponent.fillDetailLayout(dataSchema.getValue());
    }

    @Override
    public void switchToObject(DetailComponent source, String title, DetailComponent target) {
        log.info("(Object) Switched from {} to {} : {}", source, title, target);
        switchView(target);
        pathComponent.createPathElement(target.getComponentTitle(), target);
    }

    @Override
    public void switchToArray(DetailComponent source, String title, DetailComponent target) {
        log.info("(Array) Switched from {} to {} : {}", source, title, target);
        switchView(target);
        pathComponent.createPathElement(target.getComponentTitle(), target);
    }

    @Override
    public void switchView(DetailComponent targetComponent) {
        remove(currentDetailComponent);
        currentDetailComponent = targetComponent;
        add(currentDetailComponent);
    }

    @Override
    public void navigate(String path) {
        navigationListener.navigate(path);
    }
}
