package com.example.application.ui.view;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.finalStructure.Component;
import com.example.application.data.structureModel.finalStructure.PropertyTypeEnum;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

public class MasterDetailView extends Div {

    private Grid<DataSchema> grid = new Grid<>(DataSchema.class, false);

    private Component component;

    public MasterDetailView(Component component) { //change to 2 schemas 1 create 1 get
        this.component = component;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        configureGrid();
    }

    public void configureGrid() {
        component.getSchema().getProperties().keySet().forEach(key ->
                grid.addColumn(dataSchema -> dataSchema.getProperties().get(key)).setHeader(key).setAutoWidth(true)
        );

        //grid.setItems();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                //TODO
            } else {
                //TODO clear form
            }
        });
    }

    public void setData(List<DataSchema> data){
        grid.setItems(data);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        component.getSchema().getProperties().keySet().forEach(key ->
                formLayout.add(createEditorComponent(component.getSchema().getProperties().get(key).getType(), key))
        );

        editorDiv.add(formLayout);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private AbstractField createEditorComponent(PropertyTypeEnum type, String title) {
        AbstractField editorComponent;
        switch (type) {
            case NUMBER:
                editorComponent = new NumberField(title);
                break;
            case BOOLEAN:
                editorComponent = new Checkbox(title);
                break;
            case STRING:
                editorComponent = new TextField(title);
                break;
            default:
                editorComponent = new TextField(title);
                break;
        }

        editorComponent.setReadOnly(true);

        //TODO set component disable

        return editorComponent;
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

}
