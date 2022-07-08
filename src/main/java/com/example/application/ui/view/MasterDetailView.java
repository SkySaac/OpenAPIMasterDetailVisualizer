package com.example.application.ui.view;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.components.detaillayout.DetailLayout;
import com.example.application.ui.components.PostDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MasterDetailView extends Div {

    public interface MDActionListener extends PostDialog.PostActionListener {
        void openPostDialog();

        void deleteData(DataSchema dataSchema);
    }

    private final MDActionListener mdActionListener;
    private final Grid<DataSchema> grid = new Grid<>(DataSchema.class, false);
    private final DetailLayout detailLayout;

    public MasterDetailView(MDActionListener actionListener, boolean isPaged, StrucSchema getSchema, StrucSchema postSchema, StrucSchema putSchema, boolean hasDelete) { //change to 2 schemas 1 create 1 get
        this.mdActionListener = actionListener;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        addPageButtons(isPaged, postSchema);

        splitLayout.addToPrimary(createGridLayout());

        if(getSchema==null)
            log.warn("schema in null");

        detailLayout = new DetailLayout(getSchema);
        splitLayout.addToSecondary(detailLayout);

        add(splitLayout);

        // Configure Grid
        configureGrid(getSchema, hasDelete);
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

    public void openPostDialog(StrucSchema schema, StrucPath strucPath) {
        PostDialog postDialog = new PostDialog(mdActionListener);
        postDialog.open(schema, strucPath);
    }

    private void deleteData(DataSchema dataSchema) {
        mdActionListener.deleteData(dataSchema);
    }

    public void configureGrid(StrucSchema getSchema, boolean showDelete) {
        if (showDelete) {
            grid.addComponentColumn(dataSchema -> {
                final var button = new Button(new Icon(VaadinIcon.TRASH));
                button.addClickListener(event -> deleteData(dataSchema));
                return button;
            });
        }

        //Add all columns
        getSchema.getStrucValue().getProperties().keySet().forEach(property ->
                grid.addColumn(
                        dataSchema -> dataSchema.getValue().getProperties().get(property) != null
                                ? dataSchema.getValue().getProperties().get(property).getValue().getPlainValue() : "-"
                ).setHeader(property).setAutoWidth(true)
        );


        //grid.setItems();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                detailLayout.fillDetailLayout(event.getValue());
            } else {
                detailLayout.clearDetailLayout();
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


    private Div createGridLayout() {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.add(grid);
        return wrapper;
    }


}
