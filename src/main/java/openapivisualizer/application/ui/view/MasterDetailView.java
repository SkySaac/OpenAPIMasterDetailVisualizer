package openapivisualizer.application.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import lombok.extern.slf4j.Slf4j;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.ui.components.detaillayout.DetailLayout;

import java.util.List;

@Slf4j
public class MasterDetailView extends Div {

    public interface MDActionListener {
        void openPostDialog();

        void openPutDialog();

        void openDeleteDialog();

        void openQueryDialog();

        void refreshData();

    }

    private final MDActionListener mdActionListener;
    private final Grid<DataSchema> grid = new Grid<>(DataSchema.class, false);
    private final DetailLayout detailLayout;

    private final Label noDataLabel = new Label("Keine Daten erhalten");

    public MasterDetailView(DetailLayout.NavigationListener navigationListener, MDActionListener actionListener, boolean isPaged, StrucSchema getSchema, boolean hasPost,
                            boolean hasPut, boolean hasDelete, boolean hasQueryParams) { //change to 2 schemas 1 create 1 get
        this.mdActionListener = actionListener;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        addTopButtons(hasPost, hasPut, hasQueryParams);

        splitLayout.addToPrimary(createGridLayout());

        if (getSchema == null)
            log.error("schema is null while creating a mdview");

        detailLayout = new DetailLayout(navigationListener, getSchema);
        splitLayout.addToSecondary(detailLayout);

        add(splitLayout);

        // Configure Grid
        configureGrid(getSchema, hasDelete);
    }

    public void addTopButtons(boolean hasPost, boolean hasPut, boolean hasQueryParams) {
        //HorizontalLayout menubar = new HorizontalLayout();
        HorizontalLayout menuBar = new HorizontalLayout();
        menuBar.getStyle().set("padding-left", "10px");

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

        if (hasPut) {
            Button putButton = new Button(VaadinIcon.EDIT.create());
            putButton.addClickListener(e -> mdActionListener.openPutDialog());
            menuBar.add(putButton);
        }

        add(menuBar);
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
        getSchema.getStrucValue().getProperties().forEach((key,value) -> {
                    if (value.getStrucValue().getType()!= DataPropertyType.OBJECT
                    &&value.getStrucValue().getType()!= DataPropertyType.ARRAY) {
                        grid.addColumn(
                                dataSchema -> dataSchema.getValue().getProperties().containsKey(key)
                                        ? dataSchema.getValue().getProperties().get(key).getValue().getPlainValue() : "-"
                        ).setHeader(key).setAutoWidth(true).setResizable(true).setSortable(true);
                    }
                }
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
        if (data != null && data.getValue() != null && (!data.getValue().getDataSchemas().isEmpty() || !data.getValue().getProperties().isEmpty())) {
            noDataLabel.setVisible(false);
            if (data.getValue().getDataSchemas().isEmpty() && !data.getValue().getProperties().isEmpty()) {
                grid.setItems(List.of(data));
            } else {
                grid.setItems(data.getValue().getDataSchemas());
            }
        } else {
            log.info("No data found");
            noDataLabel.setVisible(true);
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
