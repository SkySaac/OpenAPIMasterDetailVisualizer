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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDialog extends Dialog {

    public interface PostActionListener {
        void postAction(String path, Map<String, String> queryParameters, DataSchema properties);
    }

    private final PostActionListener actionListener;

    private final List<InputValueComponent> inputFieldComponents = new ArrayList<>();
    private List<InputValueComponent> querryFieldComponents = new ArrayList<>();

    public PostDialog(PostActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void open(StrucSchema schema, StrucPath strucPath) { //TODO only needs strucpath since the schema is in there
        if (!strucPath.getQueryParams().isEmpty())
            createQueryParamFields(strucPath);
        if(schema!=null)
            createFields(schema);

        Button postButton = new Button("Post");
        postButton.addClickListener(e -> this.postAction(strucPath.getPath()));

        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(postButton, closePostViewButton);
        add(horizontalLayout);

        this.open();
    }

    private Map<String,String> collectQueryParams(){
        Map<String,String> params = new HashMap<>();
        querryFieldComponents.forEach(component -> {
            if(!component.getComponent().isEmpty()){
                params.put(component.getTitle(),component.getComponent().getValue().toString()); //TODO toString correct ?
            }
        });
        return params;
    }

    private void postAction(String path) {
        Map<String,String> queryParams = collectQueryParams();
        //TODO collect query params

        Map<String, DataSchema> dataInputMap = new HashMap<>();
        inputFieldComponents.forEach(inputValueComponent -> {
            DataValue inputFieldValue = new DataValue(inputValueComponent.getComponent().getValue().toString(), inputValueComponent.getPropertyTypeEnum());
            DataSchema inputFieldSchema = new DataSchema(inputValueComponent.getTitle(), inputFieldValue);
            dataInputMap.put(inputValueComponent.getTitle(), inputFieldSchema);

        });
        DataValue dataValue = new DataValue(dataInputMap, PropertyTypeEnum.OBJECT);
        DataSchema dataSchema = new DataSchema("post", dataValue);

        //TODO check if valid
        actionListener.postAction(path, queryParams, dataSchema);
    }

    private void createFields(StrucSchema schema) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        schema.getStrucValue().getProperties().keySet().forEach(key -> {
            AbstractField abstractField = createEditorComponent(schema.getStrucValue().getProperties().get(key).getStrucValue().getType(), key,true);
            verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Body"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }

    private AbstractField createEditorComponent(PropertyTypeEnum type, String title, boolean input) {
        AbstractField inputComponent = switch (type) {
            case NUMBER -> new NumberField(title);
            case BOOLEAN -> new Checkbox(title);
            case STRING -> new TextField(title);
            case OBJECT -> //TODO change, wenns n object is sindse ja ineinander verschachtelt
                    new TextField(title);
            default -> new TextField(title);
        };

        if(input) //TODO maybe add it later and not in this function
            inputFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        else
            querryFieldComponents.add( new InputValueComponent(title, inputComponent, type));

        return inputComponent;
    }

    private void createQueryParamFields(StrucPath strucPath) {
        //TODO
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getQueryParams().forEach(queryParam -> { //TODO check if required
            AbstractField abstractField = createEditorComponent(queryParam.getType(), queryParam.getName(),false);
            verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Query Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}
