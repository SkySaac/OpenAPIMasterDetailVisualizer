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
        void postAction(Map<String, String> queryParameters, DataSchema properties);
    }

    private final PostActionListener actionListener;

    private final List<InputValueComponent> inputFieldComponents = new ArrayList<>();
    private Map<String, AbstractField> querryFieldComponents = new HashMap<>();

    public PostDialog(PostActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void open(StrucSchema schema, StrucPath strucPath) {
        createFields(schema);
        createQuerryParamFields(strucPath);

        Button postButton = new Button("Post");
        postButton.addClickListener(e -> this.postAction());

        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(postButton, closePostViewButton);
        add(horizontalLayout);

        this.open();
    }

    private void postAction() {
        //TODO collect query params

        Map<String, DataSchema> dataInputMap = new HashMap<>();
        inputFieldComponents.forEach(inputValueComponent -> {
            DataValue inputFieldValue = new DataValue(inputValueComponent.getComponent().getValue().toString(),inputValueComponent.getPropertyTypeEnum());
            DataSchema inputFieldSchema = new DataSchema(inputValueComponent.getTitle(),inputFieldValue);
            dataInputMap.put(inputValueComponent.getTitle(),inputFieldSchema);

        });
        DataValue dataValue = new DataValue(dataInputMap,PropertyTypeEnum.OBJECT);
        DataSchema dataSchema = new DataSchema("post", dataValue);

        //TODO check if valid
        actionListener.postAction(null, dataSchema);
    }

    private void createFields(StrucSchema schema) {
        VerticalLayout verticalLayout = new VerticalLayout();
        schema.getStrucValue().getProperties().keySet().forEach(key -> {
                    AbstractField abstractField = createEditorComponent(schema.getStrucValue().getProperties().get(key).getStrucValue().getType(), key);
                    verticalLayout.add(abstractField);
                }
        );
        add(verticalLayout);
    }

    private AbstractField createEditorComponent(PropertyTypeEnum type, String title) {
        AbstractField inputComponent = switch (type) {
            case NUMBER -> new NumberField(title);
            case BOOLEAN -> new Checkbox(title);
            case STRING -> new TextField(title);
            case OBJECT -> //TODO change, wenns n object is sindse ja ineinander verschachtelt
                    new TextField(title);
            default -> new TextField(title);
        };

        inputFieldComponents.add(new InputValueComponent(title,inputComponent,type));

        return inputComponent;
    }

    private void createQuerryParamFields(StrucPath strucPath) {
        //TODO
    }


}
