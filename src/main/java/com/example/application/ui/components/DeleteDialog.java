package com.example.application.ui.components;

import com.example.application.data.structureModel.PropertyTypeEnum;
import com.example.application.data.structureModel.StrucPath;
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

public class DeleteDialog extends Dialog {

    public interface DeleteActionListener {
        void deleteAction(String path, Map<String,String> pathVariables, MultiValueMap<String,String> queryParameters);
    }

    private final DeleteActionListener actionListener;

    private final List<InputValueComponent> pathFieldComponents = new ArrayList<>();
    private final List<InputValueComponent> queryFieldComponents = new ArrayList<>();

    public DeleteDialog(DeleteActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void open(StrucPath strucPath) {
        if (!strucPath.getQueryParams().isEmpty())
            createQueryParamFields(strucPath);
        createPathParamFields(strucPath);


        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(e -> {
            if(areRequiredFieldsFilled())
                deleteAction(strucPath.getPath());
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(deleteButton, closePostViewButton);
        add(horizontalLayout);

        this.open();
    }

    private MultiValueMap<String,String> collectQueryParams(){
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        queryFieldComponents.forEach(component -> {
            if(!component.getComponent().isEmpty()){
                params.add(component.getTitle(),component.getComponent().getValue().toString()); //TODO toString correct ?
            }
        });
        return params;
    }

    private boolean areRequiredFieldsFilled(){
        return pathFieldComponents.stream().noneMatch(component -> component.getComponent().isEmpty());
    }

    private Map<String,String> collectPathParams(){
        Map<String,String> params = new HashMap<>();
        pathFieldComponents.forEach(component -> {
            if(!component.getComponent().isEmpty()){
                params.put(component.getTitle(),component.getComponent().getValue().toString()); //TODO toString correct ?
            }
        });
        return params;
    }

    private void deleteAction(String path) {
        //TODO use em later
        MultiValueMap<String,String> queryParams = collectQueryParams();

        Map<String,String> pathParams = collectPathParams();

        actionListener.deleteAction(path,pathParams,queryParams);
    }

    private AbstractField createEditorComponent(PropertyTypeEnum type, String title, boolean isPath) {
        AbstractField inputComponent = switch (type) {
            case INTEGER -> new IntegerField(title);
            case DOUBLE -> new NumberField(title);
            case BOOLEAN -> new Checkbox(title);
            case STRING -> new TextField(title);
            case OBJECT -> //TODO change, wenns n object is sindse ja ineinander verschachtelt
                    new TextField(title);
            default -> new TextField(title);
        };

        if(isPath) //TODO maybe add it later and not in this function
            pathFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        else
            queryFieldComponents.add( new InputValueComponent(title, inputComponent, type));

        return inputComponent;
    }

    private void createQueryParamFields(StrucPath strucPath) {
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

    private void createPathParamFields(StrucPath strucPath) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getPathParams().forEach(pathParam -> {
                    AbstractField abstractField = createEditorComponent(pathParam.getType(), pathParam.getName(),true);
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Path Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}
