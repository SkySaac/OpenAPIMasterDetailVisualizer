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

public class PostDialog extends Dialog {

    public interface PostActionListener {
        void postAction(String path, MultiValueMap<String, String> queryParameters, Map<String, String> pathVariables, DataSchema properties);
    }

    private final PostActionListener actionListener;

    protected final List<InputValueComponent> inputFieldComponents = new ArrayList<>();
    private final List<InputValueComponent> querryFieldComponents = new ArrayList<>();
    private final List<InputValueComponent> pathFieldComponents = new ArrayList<>();

    private PropertyTypeEnum bodyPropertyType = PropertyTypeEnum.OBJECT;

    public PostDialog(PostActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void open(StrucPath strucPath) { //TODO only needs strucpath since the schema is in there
        if (!strucPath.getPathParams().isEmpty())
            createPathParamFields(strucPath);
        if (!strucPath.getQueryParams().isEmpty())
            createQueryParamFields(strucPath);
        if (strucPath.getRequestStrucSchema() != null)
            createFields(strucPath.getRequestStrucSchema());

        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        Button postButton = new Button("Post");
        postButton.addClickListener(e -> this.postAction(strucPath.getPath()));

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
                params.add(component.getTitle(), component.getComponent().getValue().toString()); //TODO toString correct ?
            }
        });
        return params;
    }

    private Map<String, String> collectPathParams() {
        Map<String, String> params = new HashMap<>() {
        };
        pathFieldComponents.forEach(component -> {

            params.put(component.getTitle(), component.getComponent().getValue().toString()); //TODO toString correct ?

        });
        return params;
    }

    private void postAction(String path) { //TODO IF IT EVEN HAS BODY CHECK
        MultiValueMap<String, String> queryParams = collectQueryParams();
        Map<String, String> pathParams = collectPathParams();

        DataValue dataValue = null;
        if (bodyPropertyType.equals(PropertyTypeEnum.OBJECT)) {
            Map<String, DataSchema> dataInputMap = new HashMap<>();
            inputFieldComponents.forEach(inputValueComponent -> {
                DataValue inputFieldValue = new DataValue(inputValueComponent.getComponent().getValue().toString(), inputValueComponent.getPropertyTypeEnum());
                DataSchema inputFieldSchema = new DataSchema(inputValueComponent.getTitle(), inputFieldValue);
                dataInputMap.put(inputValueComponent.getTitle(), inputFieldSchema);

            });
            dataValue = new DataValue(dataInputMap, PropertyTypeEnum.OBJECT);
        } else if (bodyPropertyType.equals(PropertyTypeEnum.ARRAY)) {
            //TODO
        } else {
            dataValue = new DataValue(inputFieldComponents.get(0).getComponent().getValue().toString(), bodyPropertyType);
        }
        DataSchema dataSchema = new DataSchema("post", dataValue);

        //TODO check if valid
        actionListener.postAction(path, queryParams, pathParams, dataSchema);
    }

    private void createFields(StrucSchema schema) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();
        bodyPropertyType = schema.getStrucValue().getType();

        if (schema.getStrucValue().getType().equals(PropertyTypeEnum.OBJECT))
            schema.getStrucValue().getProperties().keySet().forEach(key -> {
                        AbstractField abstractField = createEditorComponent(schema.getStrucValue().getProperties().get(key).getStrucValue().getType(), key, "input");
                        verticalLayoutContent.add(abstractField);
                    }
            );
        else if (schema.getStrucValue().getType().equals(PropertyTypeEnum.ARRAY)) {
        } //TODO
        else if (schema.getStrucValue().getType().equals(PropertyTypeEnum.BOOLEAN)) {
            AbstractField abstractField = createEditorComponent(PropertyTypeEnum.BOOLEAN, schema.getName(), "input");
            verticalLayoutContent.add(abstractField);
        } //TODO
        else if (schema.getStrucValue().getType().equals(PropertyTypeEnum.INTEGER)) {
            AbstractField abstractField = createEditorComponent(PropertyTypeEnum.INTEGER, schema.getName(), "input");
            verticalLayoutContent.add(abstractField);
        } //TODO
        else if (schema.getStrucValue().getType().equals(PropertyTypeEnum.DOUBLE)) {
            AbstractField abstractField = createEditorComponent(PropertyTypeEnum.DOUBLE, schema.getName(), "input");
            verticalLayoutContent.add(abstractField);
        } //TODO
        else if (schema.getStrucValue().getType().equals(PropertyTypeEnum.STRING)) {
            AbstractField abstractField = createEditorComponent(PropertyTypeEnum.STRING, schema.getName(), "input");
            verticalLayoutContent.add(abstractField);
        } //TODO


        //TODO was wenn array -> textfeld oder liste mit plus

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

        strucPath.getQueryParams().forEach(queryParam -> {
                    AbstractField abstractField = createEditorComponent(queryParam.getType(), queryParam.getName(), "path");
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Path Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}
