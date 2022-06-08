package com.example.application.ui.view;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.conponents.PostDialog;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterDetailView extends Div {

    public interface MDActionListener extends PostDialog.PostActionListener {
        void openPostDialog();

        void deleteData();
    }

    private final MDActionListener mdActionListener;

    private final Map<String, AbstractField> detailLayoutComponents = new HashMap<>();
    private final Grid<DataSchema> grid = new Grid<>(DataSchema.class, false);

    private PostDialog postDialog;

    public MasterDetailView(MDActionListener actionListener, boolean isPaged, StrucSchema getSchema, StrucSchema postSchema, StrucSchema putSchema) { //change to 2 schemas 1 create 1 get
        this.mdActionListener = actionListener;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        addPageButtons(isPaged, postSchema);

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout, getSchema);

        add(splitLayout);

        // Configure Grid
        configureGrid(getSchema);
    }

    public void addPageButtons(boolean isPaged, StrucSchema postSchema) {
        Div div = new Div();

        if (isPaged) {
            Button backwards = new Button(VaadinIcon.ARROW_LEFT.create());
            Button forwards = new Button(VaadinIcon.ARROW_RIGHT.create());
            div.add(backwards, forwards);
        }

        if (postSchema != null) {
            Button postButton = new Button("Create");
            postButton.addClickListener(e -> mdActionListener.openPostDialog());
            div.add(postButton);
        }

        add(div);
    }

    public void openDialog(StrucSchema schema, StrucPath strucPath) {
        postDialog = new PostDialog(mdActionListener);
        postDialog.open(schema, strucPath);
    }

    public void configureGrid(StrucSchema getSchema) {
        getSchema.getProperties().keySet().forEach(property ->
                grid.addColumn(
                        dataSchema -> dataSchema.getValue().getProperties().get(property) != null
                                ? dataSchema.getValue().getProperties().get(property).getValue().getValue() : "-"
                ).setHeader(property).setAutoWidth(true)
        );

        //grid.setItems();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                fillDetailLayout(event.getValue());
            } else {
                clearDetailLayout();
            }
        });
    }

    public void setData(DataSchema data) {
        if (data.getValue().getDataSchemas() == null) {
            grid.setItems(List.of(data));
        } else {
            grid.setItems(data.getValue().getDataSchemas());
        }
    }

    private void createEditorLayout(SplitLayout splitLayout, StrucSchema getSchema) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        getSchema.getProperties().keySet().forEach(key ->
                formLayout.add(createEditorComponent(getSchema.getProperties().get(key).getType(), key))
        );

        editorDiv.add(formLayout);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private AbstractField createEditorComponent(PropertyTypeEnum type, String title) {
        AbstractField editorComponent = switch (type) {
            case NUMBER -> new NumberField(title);
            case BOOLEAN -> new Checkbox(title);
            case STRING -> new TextField(title);
            case OBJECT -> new TextField(title); //TODO change, wenns n object is sindse ja ineinander verschachtelt
            default -> new TextField(title);
        };

        editorComponent.setReadOnly(true);

        detailLayoutComponents.put(title, editorComponent);

        return editorComponent;
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void fillDetailLayout(DataSchema dataSchema) {
        dataSchema.getValue().getProperties().keySet().forEach(key -> {
            if (detailLayoutComponents.get(key) != null) {
                switch (dataSchema.getValue().getProperties().get(key).getValue().getPropertyTypeEnum()) {
                    case NUMBER ->
                            detailLayoutComponents.get(key).setValue(Double.parseDouble(dataSchema.getValue().getProperties().get(key).getValue().getValue()));
                    case BOOLEAN ->
                            detailLayoutComponents.get(key).setValue(Boolean.parseBoolean(dataSchema.getValue().getProperties().get(key).getValue().getValue()));
                    case STRING ->
                            detailLayoutComponents.get(key).setValue(dataSchema.getValue().getProperties().get(key).getValue().getValue());
                    case OBJECT ->
                            detailLayoutComponents.get(key).setValue(dataSchema.getValue().getProperties().get(key).getValue().toString());
                    default ->
                            detailLayoutComponents.get(key).setValue(dataSchema.getValue().getProperties().get(key).getValue().toString());
                }
            }
        });
    }

    private void clearDetailLayout() {
        detailLayoutComponents.values().forEach(HasValue::clear);
    }

}
