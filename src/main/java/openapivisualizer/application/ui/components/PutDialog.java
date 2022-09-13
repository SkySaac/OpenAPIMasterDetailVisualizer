package openapivisualizer.application.ui.components;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucPath;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.rest.client.restdatamodel.DataValue;
import openapivisualizer.application.ui.components.createComponents.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PutDialog extends Dialog {

    public interface PutActionListener {
        void putAction(String path, MultiValueMap<String, String> queryParameters, Map<String, String> pathParams, DataSchema properties);
    }

    private final PutActionListener actionListener;

    protected final List<CreateComponent> inputFieldComponents = new ArrayList<>();
    private final List<CreateComponent> queryFieldComponents = new ArrayList<>();
    private final List<CreateComponent> pathFieldComponents = new ArrayList<>();
    private final Button putButton = new Button("Put");
    private DataPropertyType bodyPropertyType = DataPropertyType.OBJECT;

    public PutDialog(PutActionListener actionListener) {
        this.actionListener = actionListener;
        setWidth(50, Unit.PERCENTAGE);

    }

    public void open(StrucPath strucPath) {
        this.open(strucPath, new HashMap<>());
    }

    public void open(StrucPath strucPath, Map<String, String> pathParams) { //TODO only needs strucpath since the schema is in there
        if (!strucPath.getPathParams().isEmpty())
            createPathParamFields(strucPath, pathParams);
        if (!strucPath.getQueryParams().isEmpty())
            createQueryParamFields(strucPath);
        if (strucPath.getRequestStrucSchema() != null) {
            bodyPropertyType = strucPath.getRequestStrucSchema().getStrucValue().getType();
            createBodyFields(strucPath.getRequestStrucSchema());
        }

        Button closePutViewButton = new Button("Close");
        closePutViewButton.addClickListener(e -> this.close());

        putButton.addClickListener(e -> this.putAction(strucPath.getPath()));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(putButton, closePutViewButton);
        add(horizontalLayout);

        this.open();
    }

    private MultiValueMap<String, String> collectQueryParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {
        };
        queryFieldComponents.forEach(component -> {
            if (!component.isEmpty()) {
                params.add(component.getLabel(), component.getValue()); //TODO toString correct ?
            }
        });
        return params;
    }

    private Map<String, String> collectPathParams() {
        Map<String, String> params = new HashMap<>() {
        };
        pathFieldComponents.forEach(component -> {
            params.put(component.getLabel(), component.getValue()); //TODO toString correct ?
        });
        return params;
    }

    private void putAction(String path) { //TODO IF IT EVEN HAS BODY CHECK
        MultiValueMap<String, String> queryParams = collectQueryParams();
        Map<String, String> pathParams = collectPathParams();

        DataSchema dataSchema;
        if (bodyPropertyType.equals(DataPropertyType.OBJECT)) {
            Map<String, DataSchema> dataInputMap = new HashMap<>();
            inputFieldComponents.forEach(inputValueComponent -> {
                DataValue inputFieldValue = new DataValue(inputValueComponent.getValue(), inputValueComponent.getDataPropertyType());
                DataSchema inputFieldSchema = new DataSchema(inputValueComponent.getLabel(), inputFieldValue);
                dataInputMap.put(inputValueComponent.getLabel(), inputFieldSchema);

            });
            DataValue dataValue = new DataValue(dataInputMap, DataPropertyType.OBJECT);
            dataSchema = new DataSchema("put", dataValue);

        } else {
            DataValue inputFieldValue = new DataValue(inputFieldComponents.get(0).getValue(), inputFieldComponents.get(0).getDataPropertyType());
            dataSchema = new DataSchema(inputFieldComponents.get(0).getLabel(), inputFieldValue);
        }

        if (areRequiredFieldsFilled(pathParams))
            actionListener.putAction(path, queryParams, pathParams, dataSchema);
    }

    private boolean areRequiredFieldsFilled(Map<String, String> pathParams) {
        return pathParams.size() == pathFieldComponents.size()
                && queryFieldComponents.stream().allMatch(component -> !component.isRequired()
                || (component.isRequired() && !component.isEmpty()));
    }

    private void createBodyFields(StrucSchema schema) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();
        verticalLayoutContent.setPadding(false);

        if (schema.getStrucValue().getType().equals(DataPropertyType.OBJECT))
            schema.getStrucValue().getProperties().keySet().forEach(key -> {
                CreateComponent createComponent = createEditorComponent(schema.getStrucValue().getProperties().get(key).getStrucValue().getType(), schema.getStrucValue().getFormat(), key);
                verticalLayoutContent.add(createComponent);
                inputFieldComponents.add(createComponent);
            });
        else {
            CreateComponent createComponent = createEditorComponent(schema.getStrucValue().getType(), schema.getStrucValue().getFormat(), schema.getName());
            verticalLayoutContent.add(createComponent);
        }

        verticalLayout.add(new Label("Body"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }

    private CreateComponent createEditorComponent(DataPropertyType type, String format, String title) {
        CreateComponent inputComponent;
        switch (type) {
            case INTEGER -> inputComponent = new IntegerfieldComponent(title);
            case DOUBLE -> inputComponent = new NumberfieldComponent(title);
            case BOOLEAN -> inputComponent = new CheckboxComponent(title);
            case ARRAY -> inputComponent = new ArrayComponent(title, DataPropertyType.STRING, null);
            default -> inputComponent = new TextfieldComponent(title, format);

        } //TODO Objekte


        return inputComponent;
    }

    private void createQueryParamFields(StrucPath strucPath) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();
        verticalLayoutContent.setPadding(false);

        strucPath.getQueryParams().forEach(queryParam -> {
                    CreateComponent createComponent = createEditorComponent(queryParam.getType(), queryParam.getFormat(), queryParam.getName());
                    verticalLayoutContent.add(createComponent);
                    queryFieldComponents.add(createComponent);
                    createComponent.setRequired(queryParam.isRequired());
                }
        );

        verticalLayout.add(new Label("Query Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }

    private void createPathParamFields(StrucPath strucPath, Map<String, String> pathParams) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();
        verticalLayoutContent.setPadding(false);

        strucPath.getPathParams().forEach(pathParam -> {
                    CreateComponent createComponent = createEditorComponent(pathParam.getType(), pathParam.getFormat(), pathParam.getName());
                    verticalLayoutContent.add(createComponent);
                    pathFieldComponents.add(createComponent);
                    createComponent.setRequired(true);

                    if (pathParams.containsKey(pathParam.getName()))
                        createComponent.setValue(pathParams.get(pathParam.getName()));
                }
        );

        verticalLayout.add(new Label("Path Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}


