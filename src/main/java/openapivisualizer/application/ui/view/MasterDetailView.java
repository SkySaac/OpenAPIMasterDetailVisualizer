package openapivisualizer.application.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.ui.components.SettingsDialog;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MasterDetailView extends View {

    public interface MDActionListener {
        void openPostDialog();

        void openPutDialog();

        void openDeleteDialog();

        void openSettingsDialog();

        void setInitialColumnSettings(List<SettingsDialog.ColumnGridElement> initialSettings);

        void refreshData();

        void dataCorupted();

    }

    private final MDActionListener mdActionListener;
    private final Grid<DataSchema> grid = new Grid<>(DataSchema.class, false);
    private final DetailLayout detailLayout;
    private final Label noDataLabel = new Label("No Data");
    private final List<Grid.Column<DataSchema>> inlineGridColumns = new ArrayList<>();

    private List<Grid.Column<DataSchema>> initialGridColumns;

    public MasterDetailView(String tag, DetailLayout.NavigationListener navigationListener, MDActionListener actionListener, StrucSchema getSchema, boolean hasPost,
                            boolean hasPut, boolean hasDelete, boolean showInline) { //change to 2 schemas 1 create 1 get
        super(tag);
        this.mdActionListener = actionListener;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        addTopButtons(hasPost, hasPut, hasDelete, showInline);

        splitLayout.addToPrimary(createGridLayout());

        if (getSchema == null)
            log.error("schema is null while creating a mdview");

        detailLayout = new DetailLayout(navigationListener, getSchema);
        splitLayout.addToSecondary(detailLayout);

        add(splitLayout);

        // Configure Grid
        configureGrid(actionListener, getSchema, hasPut, hasDelete, showInline);
    }

    public void addTopButtons(boolean hasPost, boolean hasPut, boolean hasDelete, boolean showInline) {
        //HorizontalLayout menubar = new HorizontalLayout();
        HorizontalLayout menuBar = new HorizontalLayout();
        menuBar.getStyle().set("padding-left", "10px");

        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(e -> mdActionListener.refreshData());
        menuBar.add(refreshButton);


        Button settingsButton = new Button(VaadinIcon.SLIDERS.create());
        settingsButton.addClickListener(e -> mdActionListener.openSettingsDialog());
        menuBar.add(settingsButton);


        if (hasPost) {
            Button postButton = new Button(VaadinIcon.PLUS_CIRCLE.create());
            postButton.addClickListener(e -> mdActionListener.openPostDialog());
            menuBar.add(postButton);
        }

        if (hasPut && !showInline) {
            Button putButton = new Button(VaadinIcon.EDIT.create());
            putButton.addClickListener(e -> mdActionListener.openPutDialog());
            menuBar.add(putButton);
        }

        if (hasDelete && !showInline) {
            Button putButton = new Button(VaadinIcon.TRASH.create());
            putButton.addClickListener(e -> mdActionListener.openDeleteDialog());
            menuBar.add(putButton);
        }

        add(menuBar);
    }

    public void setColumnSettings(List<SettingsDialog.ColumnGridElement> columnSettings) {
        Map<String, Grid.Column<DataSchema>> columnMap = new HashMap<>();
        columnMap.putAll(initialGridColumns.stream().filter(column -> column.getKey() != null).collect(Collectors.toMap(Grid.Column::getKey, e -> e)));
        List<Grid.Column<DataSchema>> sortedColumns = new ArrayList<>();
        columnSettings.forEach(columnElement -> {
            if (columnElement.isVisible())
                sortedColumns.add(columnMap.get(columnElement.getColumnName()));
            else if (grid.getColumns().contains(grid.getColumnByKey(columnElement.getColumnName())))
                grid.removeColumn(grid.getColumnByKey(columnElement.getColumnName()));

        });
        for (int i = 0; i < inlineGridColumns.size(); i++) {
            sortedColumns.add(i, inlineGridColumns.get(i));
        }
        grid.setColumnOrder(sortedColumns);

    }

    private void setInitialGridColumns(MDActionListener actionListener) {
        actionListener.setInitialColumnSettings(
                initialGridColumns.stream()
                        .map(column -> new SettingsDialog.ColumnGridElement(column.getKey(), true))
                        .collect(Collectors.toList())
        );
    }


    public void configureGrid(MDActionListener actionListener, StrucSchema getSchema, boolean hasput, boolean hasdelete, boolean showInline) {

        if (hasdelete && showInline) {
            Grid.Column<DataSchema> column = grid.addComponentColumn(dataSchema -> {
                final var deleteButton = new Button(VaadinIcon.TRASH.create());
                deleteButton.addClickListener(event -> mdActionListener.openDeleteDialog());
                return deleteButton;
            });
            inlineGridColumns.add(column);
        }
        if (hasput && showInline) {
            Grid.Column<DataSchema> column = grid.addComponentColumn(dataSchema -> {
                final var putButton = new Button(VaadinIcon.EDIT.create());
                putButton.addClickListener(event -> mdActionListener.openPutDialog());
                return putButton;
            });
            inlineGridColumns.add(column);
        }

        //Add all columns
        getSchema.getStrucValue().getProperties().forEach((key, value) -> {
                    //if (value.getStrucValue().getType() != DataPropertyType.OBJECT
                    //        && value.getStrucValue().getType() != DataPropertyType.ARRAY) {
                    grid.addColumn(
                            dataSchema -> {
                                if (dataSchema.getValue().getProperties().containsKey(key)) {
                                    if (dataSchema.getValue().getProperties().get(key).getValue().getPlainValue() != null)
                                        return dataSchema.getValue().getProperties().get(key).getValue().getPlainValue();
                                    else
                                        return dataSchema.getValue().getProperties().get(key).getValue().getDataPropertyType().toString();
                                } else
                                    return "-";
                                //return dataSchema.getValue().getProperties().containsKey(key)
                                //        ? dataSchema.getValue().getProperties().get(key).getValue().getPlainValue() : "-"
                            }
                    ).setHeader(key).setAutoWidth(true).setResizable(true).setSortable(true).setKey(key);
                    //}
                }
        );

        initialGridColumns = grid.getColumns().stream().filter(column -> column.getKey() != null).collect(Collectors.toList());
        setInitialGridColumns(actionListener);


        //grid.setItems();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                try {
                    detailLayout.fillDetailLayout(event.getValue());
                } catch (IndexOutOfBoundsException e) {
                    actionListener.dataCorupted();
                }
            } else {
                detailLayout.clearDetailLayout();
            }
        });
    }

    public void setData(DataSchema data) {
        if (data != null && data.getValue() != null && (!data.getValue().getDataSchemas().isEmpty() || !data.getValue().getProperties().isEmpty())) {
            noDataLabel.setVisible(false);
            if (data.getValue().getDataSchemas().isEmpty() && !data.getValue().getProperties().isEmpty()) {
                List<DataSchema> dataSchemaList = new ArrayList<>();
                dataSchemaList.add(data);
                grid.setItems(dataSchemaList);
            } else {
                grid.setItems(data.getValue().getDataSchemas());
            }
            if (!grid.getSelectedItems().isEmpty()) {
                detailLayout.fillDetailLayout(grid.getSelectedItems().iterator().next());
            } else {
                detailLayout.clearDetailLayout();
            }
        } else {
            log.info("No data found");
            noDataLabel.setVisible(true);
            grid.setItems(new ArrayList<>());
        }
    }

    private Div createGridLayout() {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        grid.setSizeFull();
        wrapper.add(grid);
        wrapper.add(noDataLabel);
        return wrapper;
    }

}
