package com.example.application.ui.components;

import com.example.application.data.dataModel.DataSchema;
import com.example.application.data.dataModel.DataValue;
import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucPath;
import com.example.application.data.structureModel.StrucSchema;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PutDialog extends Dialog {

    public interface PutActionListener {
        void putAction(String path, MultiValueMap<String, String> queryParameters,Map<String,String> pathParams, DataSchema properties);
    }

    private final PutActionListener actionListener;

    private final List<InputValueComponent> inputFieldComponents = new ArrayList<>();
    private final List<InputValueComponent> querryFieldComponents = new ArrayList<>();
    private final List<InputValueComponent> pathFieldComponents = new ArrayList<>();

    public PutDialog(PutActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void open(StrucPath strucPath) {
        if (!strucPath.getPathParams().isEmpty()) //TODO check if all required params are in it instead
            createPathParamFields(strucPath); //TODO
        if (!strucPath.getQueryParams().isEmpty())
            createQueryParamFields(strucPath);
        if (strucPath.getRequestStrucSchema() != null)
            createFields(strucPath.getRequestStrucSchema());

        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        Button postButton = new Button("Put");
        postButton.addClickListener(e -> this.putAction(strucPath.getPath()));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(postButton, closePostViewButton);
        add(horizontalLayout);

        this.open();
    }

    private MultiValueMap<String, String> collectQueryParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {
        };
        querryFieldComponents.forEach(component -> {
            if (!component.getComponent().isEmpty()) {
                params.add(component.getTitle(), component.getComponent().getValue().toString());
            }
        });
        return params;
    }

    private Map<String, String> collectPathParams() {
        Map<String, String> params = new HashMap<>() {
        };
        pathFieldComponents.forEach(component -> {
            params.put(component.getTitle(), component.getComponent().getValue().toString());
        });
        return params;
    }

    private void putAction(String path) {
        MultiValueMap<String, String> queryParams = collectQueryParams();
        Map<String, String> pathParams = collectPathParams();

        Map<String, DataSchema> dataInputMap = new HashMap<>();
        inputFieldComponents.forEach(inputValueComponent -> {
            DataValue inputFieldValue = new DataValue(inputValueComponent.getComponent().getValue().toString(), inputValueComponent.getPropertyTypeEnum());
            DataSchema inputFieldSchema = new DataSchema(inputValueComponent.getTitle(), inputFieldValue);
            dataInputMap.put(inputValueComponent.getTitle(), inputFieldSchema);

        });
        DataValue dataValue = new DataValue(dataInputMap, PropertyTypeEnum.OBJECT);
        DataSchema dataSchema = new DataSchema("post", dataValue);

        //TODO check if valid
        actionListener.putAction(path, queryParams,pathParams, dataSchema);
    }

    private void createFields(StrucSchema schema) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        schema.getStrucValue().getProperties().keySet().forEach(key -> {
                    AbstractField abstractField = createEditorComponent(schema.getStrucValue().getProperties().get(key).getStrucValue().getType(), key, "input");
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Body"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }

    private AbstractField createEditorComponent(PropertyTypeEnum type, String title, String inputType) {
        AbstractField inputComponent = switch (type) {
            case INTEGER -> new IntegerField(title);
            case DOUBLE -> new NumberField(title);
            case BOOLEAN -> new Checkbox(title);
            case STRING -> new TextField(title);
            case OBJECT -> //TODO change, wenns n object is sindse ja ineinander verschachtelt
                    new TextField(title);
            default -> new TextField(title);
        };

        if (inputType.equals("input")) //TODO maybe add it later and not in this function
            inputFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        else if (inputType.equals("query"))
            querryFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        else if (inputType.equals("path"))
            pathFieldComponents.add(new InputValueComponent(title, inputComponent, type));

        return inputComponent;
    }

    private void createQueryParamFields(StrucPath strucPath) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getQueryParams().forEach(queryParam -> { //TODO check if required
                    AbstractField abstractField = createEditorComponent(queryParam.getType(), queryParam.getName(), "query");
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Query Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }

    private void createPathParamFields(StrucPath strucPath) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getPathParams().forEach(pathParam -> {
                    AbstractField abstractField = createEditorComponent(pathParam.getType(), pathParam.getName(), "path");
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Path Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}


