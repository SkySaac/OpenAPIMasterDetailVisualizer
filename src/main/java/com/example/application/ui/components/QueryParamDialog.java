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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class QueryParamDialog extends Dialog {

    public interface QueryActionListener {
        void setQueryParams(MultiValueMap<String, String> queryParams);
    }

    private final QueryActionListener actionListener;

    private List<InputValueComponent> querryFieldComponents = new ArrayList<>();

    public QueryParamDialog(QueryActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void open(StrucPath strucPath) {
        if (!strucPath.getQueryParams().isEmpty())
            createQueryParamFields(strucPath);

        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        Button postButton = new Button("Save");
        postButton.addClickListener(e -> this.queryAction());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(postButton, closePostViewButton);
        add(horizontalLayout);

        this.open();
    }

    private MultiValueMap<String, String> collectQueryParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        querryFieldComponents.forEach(component -> {
            if (!component.getComponent().isEmpty()) {
                params.add(component.getTitle(), component.getComponent().getValue().toString()); //TODO toString correct ?
            }
        });
        return params;
    }

    private void queryAction() {
        MultiValueMap<String, String> queryParams = collectQueryParams();

        //TODO check if valid
        actionListener.setQueryParams(queryParams);
        this.close();
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

        querryFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        return inputComponent;
    }

    private void createQueryParamFields(StrucPath strucPath) { //TODO formlayout
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getQueryParams().forEach(queryParam -> { //TODO check if required
                    AbstractField abstractField = createEditorComponent(queryParam.getType(), queryParam.getName());
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Query Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}

