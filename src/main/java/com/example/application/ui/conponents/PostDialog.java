package com.example.application.ui.conponents;

import com.example.application.data.dataModel.DataSchema;
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

import java.util.HashMap;
import java.util.Map;

public class PostDialog extends Dialog {

    public interface PostActionListener {
        void postAction(Map<String, String> queryParameters, DataSchema properties);
    }

    private final PostActionListener actionListener;

    private Map<String, AbstractField> inputFieldComponents = new HashMap<>();
    private Map<String, AbstractField> querryFieldComponents = new HashMap<>();

    public PostDialog(PostActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void open(StrucSchema schema, StrucPath strucPath){
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
        //TODO collect properties
        //TODO check if valid
        actionListener.postAction(null, null);
    }

    private void createFields(StrucSchema schema) {
        VerticalLayout verticalLayout = new VerticalLayout();
        schema.getProperties().keySet().forEach(key ->
                verticalLayout.add(createEditorComponent(schema.getProperties().get(key).getType(), key))
        );
        add(verticalLayout);
    }

    private AbstractField createEditorComponent(PropertyTypeEnum type, String title) {
        AbstractField fieldComponent;
        switch (type) {
            case NUMBER:
                fieldComponent = new NumberField(title);
                break;
            case BOOLEAN:
                fieldComponent = new Checkbox(title);
                break;
            case STRING:
                fieldComponent = new TextField(title);
                break;
            case OBJECT: //TODO change, wenns n object is sindse ja ineinander verschachtelt
                fieldComponent = new TextField(title);
                break;
            default:
                fieldComponent = new TextField(title);
                break;
        }

        inputFieldComponents.put(title, fieldComponent);

        return fieldComponent;
    }

    private void createQuerryParamFields(StrucPath strucPath) {
        //TODO
    }


}
