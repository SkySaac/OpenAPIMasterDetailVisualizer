package com.example.application.ui.components.detaillayout;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.components.detaillayout.detailcomponents.DetailComponent;
import com.example.application.ui.components.detaillayout.detailcomponents.ObjectComponent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DetailLayout extends Div implements DetailSwitchListener {

    private DetailComponent currentDetailComponent;
    private final DetailComponent basicDetailComponent;

    public DetailLayout(StrucSchema schema) {

        //Div editorLayoutDiv = new Div();
        this.setClassName("editor-layout"); //TODO css
        this.getStyle().set("padding-left","10px");
        this.getStyle().set("padding-right","10px");

        //this.setClassName("editor");
        //editorLayoutDiv.add(editorDiv);
        //TODO if is object
        basicDetailComponent = new ObjectComponent(schema!=null?schema.getName():"Object7", schema, this);
        currentDetailComponent = basicDetailComponent;

        add(createPath());

        add(currentDetailComponent);
    }

    public Label createPath() {
        return new Label("TODO");
    }

    public void clearDetailLayout() {
        basicDetailComponent.clearDetailLayout();
        switchView(basicDetailComponent);
    }

    public void fillDetailLayout(DataSchema dataSchema) {
        switchView(basicDetailComponent);
        currentDetailComponent.fillDetailLayout(dataSchema.getValue());
    }

    @Override
    public void switchToObject(DetailComponent source, String title, DetailComponent target) {
        log.info("(Object) Switched from {} to {} : {}", source, title, target);
        switchView(target);
    }

    @Override
    public void switchToArray(DetailComponent source, String title, DetailComponent target) {
        log.info("(Array) Switched from {} to {} : {}", source, title, target);
        switchView(target);
    }

    private void switchView(DetailComponent component) {
        remove(currentDetailComponent);
        currentDetailComponent = component;
        add(currentDetailComponent);
    }
}
