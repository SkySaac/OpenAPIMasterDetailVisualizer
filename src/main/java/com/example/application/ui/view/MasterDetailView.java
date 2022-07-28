package com.example.application.ui.view;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.example.application.ui.components.DeleteDialog;
import com.example.application.ui.components.PostDialog;
import com.example.application.ui.components.PutDialog;
import com.example.application.ui.components.QueryParamDialog;
import com.example.application.ui.components.detaillayout.DetailLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MasterDetailView extends Div {

    public interface MDActionListener extends PostDialog.PostActionListener, DeleteDialog.DeleteActionListener,
            QueryParamDialog.QueryActionListener, PutDialog.PutActionListener {
        void openPostDialog();

        void openDeleteDialog();

        void openQueryDialog();

        void refreshData();

    }

    private final MDActionListener mdActionListener;
    private final DetailLayout.NavigationListener navigationListener;
    private final Grid<DataSchema> grid = new Grid<>(DataSchema.class, false);
    private final DetailLayout detailLayout;

    public MasterDetailView(DetailLayout.NavigationListener navigationListener, MDActionListener actionListener, boolean isPaged, StrucSchema getSchema, boolean hasPost,
                            StrucSchema putSchema, boolean hasDelete, boolean hasQueryParams) { //change to 2 schemas 1 create 1 get
        this.mdActionListener = actionListener;
        this.navigationListener = navigationListener;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        addTopButtons(isPaged, hasPost, hasQueryParams);

        splitLayout.addToPrimary(createGridLayout());

        if (getSchema == null)
            log.warn("schema in null");

        detailLayout = new DetailLayout(navigationListener, getSchema);
        splitLayout.addToSecondary(detailLayout);

        add(splitLayout);

        // Configure Grid
        configureGrid(getSchema, hasDelete);
    }

    public void addTopButtons(boolean isPaged, boolean hasPost, boolean hasQueryParams) {
        //HorizontalLayout menubar = new HorizontalLayout();
        HorizontalLayout menuBar = new HorizontalLayout();
        menuBar.getStyle().set("padding-left", "10px");

        if (isPaged) {
            Button backwards = new Button(VaadinIcon.ARROW_LEFT.create());
            Button forwards = new Button(VaadinIcon.ARROW_RIGHT.create());
            menuBar.add(new Div(backwards, forwards));
        }

        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(e -> mdActionListener.refreshData());
        menuBar.add(refreshButton);

        if (hasQueryParams) {
            Button queryButton = new Button(VaadinIcon.SLIDERS.create());
            queryButton.addClickListener(e -> mdActionListener.openQueryDialog());
            menuBar.add(queryButton);
        }

        if (hasPost) {
            Button postButton = new Button(VaadinIcon.PLUS_CIRCLE.create());
            postButton.addClickListener(e -> mdActionListener.openPostDialog());
            menuBar.add(postButton);
        }

        add(menuBar);
    }

    public void openPostDialog(StrucPath strucPath) {
        PostDialog postDialog = new PostDialog(mdActionListener);
        postDialog.open(strucPath);
    }

    //TODO PUT Dialog

    public void openPutDialog(StrucPath strucPath) {
        PutDialog putDialog = new PutDialog(mdActionListener);
        putDialog.open(strucPath);
    }

    public void openDeleteDialog(StrucPath strucPath) {
        DeleteDialog deleteDialog = new DeleteDialog(mdActionListener);
        deleteDialog.open(strucPath);
    }

    public void openQueryParamDialog(StrucPath strucPath) {
        QueryParamDialog queryParamDialog = new QueryParamDialog(mdActionListener);
        queryParamDialog.open(strucPath);
    }


    public void configureGrid(StrucSchema getSchema, boolean showDelete) {
        if (showDelete) {
            grid.addComponentColumn(dataSchema -> {
                final var button = new Button(new Icon(VaadinIcon.TRASH));
                button.addClickListener(event -> mdActionListener.openDeleteDialog());
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
        if (data != null) {
            if (data.getValue().getDataSchemas() == null) {
                grid.setItems(List.of(data));
            } else {
                grid.setItems(data.getValue().getDataSchemas());
            }
        } else {
            log.info("No data found");
        }
    }

    private Div createGridLayout() {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        grid.setSizeFull();
        wrapper.add(grid);
        return wrapper;
    }


}
