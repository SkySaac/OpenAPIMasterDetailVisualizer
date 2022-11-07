package openapivisualizer.application.ui.components;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucPath;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteDialog extends Dialog {

    public interface DeleteActionListener {
        void deleteAction(String path, Map<String, String> pathVariables, MultiValueMap<String, String> queryParameters);
    }

    private final DeleteActionListener actionListener;

    private final List<InputValueComponent> pathFieldComponents = new ArrayList<>();
    private final List<InputValueComponent> queryFieldComponents = new ArrayList<>();

    public DeleteDialog(DeleteActionListener actionListener) {
        this.actionListener = actionListener;
        setWidth(50, Unit.PERCENTAGE);

    }

    public void open(StrucPath strucPath) {
        this.open(strucPath, new HashMap<>());
    }

    public void open(StrucPath strucPath, Map<String, String> pathParams) {
        if (!strucPath.getQueryParams().isEmpty())
            createQueryParamFields(strucPath);
        createPathParamFields(strucPath, pathParams);

        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(e -> {
            if (areRequiredFieldsFilled())
                deleteAction(strucPath.getPath());
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(deleteButton, closePostViewButton);
        add(horizontalLayout);

        this.open();
    }

    private MultiValueMap<String, String> collectQueryParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        queryFieldComponents.forEach(component -> {
            if (!component.getComponent().isEmpty()) {
                params.add(component.getTitle(), component.getComponent().getValue().toString()); //TODO toString correct ?
            }
        });
        return params;
    }

    private boolean areRequiredFieldsFilled() {
        return pathFieldComponents.stream().noneMatch(component -> component.getComponent().isEmpty())
                && queryFieldComponents.stream().allMatch(component -> !component.getComponent().isRequiredIndicatorVisible()
                || (component.getComponent().isRequiredIndicatorVisible() && !component.getComponent().isEmpty()));
    }

    private Map<String, String> collectPathParams() {
        Map<String, String> params = new HashMap<>();
        pathFieldComponents.forEach(component -> {
            if (!component.getComponent().isEmpty()) {
                params.put(component.getTitle(), component.getComponent().getValue().toString()); //TODO toString correct ?
            }
        });
        return params;
    }

    private void deleteAction(String path) {
        MultiValueMap<String, String> queryParams = collectQueryParams();

        Map<String, String> pathParams = collectPathParams();

        if (areRequiredFieldsFilled(pathParams)) {
            actionListener.deleteAction(path, pathParams, queryParams);
            this.close();
        }
    }

    private boolean areRequiredFieldsFilled(Map<String, String> pathParams) {
        return pathParams.size() == pathFieldComponents.size()
                && queryFieldComponents.stream().allMatch(component -> !component.getComponent().isRequiredIndicatorVisible()
                || (component.getComponent().isRequiredIndicatorVisible() && !component.getComponent().isEmpty()));
    }

    private AbstractField createEditorComponent(DataPropertyType type, String format, String title, boolean isPath, boolean required) {
        AbstractField inputComponent;
        switch (type) {
            case INTEGER -> inputComponent = new IntegerField(title);
            case DOUBLE -> inputComponent = new NumberField(title);
            case BOOLEAN -> inputComponent = new Checkbox(title);
            default -> {
                TextField textField = new TextField(title);
                textField.setPlaceholder(format != null ? format : "");
                inputComponent = textField;
            }
        } //TODO Objekte & Arrays

        inputComponent.setRequiredIndicatorVisible(required);

        if (isPath)
            pathFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        else
            queryFieldComponents.add(new InputValueComponent(title, inputComponent, type));

        return inputComponent;
    }

    private void createQueryParamFields(StrucPath strucPath) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getQueryParams().forEach(queryParam -> {
                    AbstractField abstractField = createEditorComponent(queryParam.getType(), queryParam.getFormat(), queryParam.getName(), false, queryParam.isRequired());
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Query Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }

    private void createPathParamFields(StrucPath strucPath, Map<String, String> pathParams) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getPathParams().forEach(pathParam -> {
                    AbstractField abstractField = createEditorComponent(pathParam.getType(), pathParam.getFormat(), pathParam.getName(), true, true);
                    verticalLayoutContent.add(abstractField);
                    if (pathParams.containsKey(pathParam.getName()))
                        abstractField.setValue(pathParams.get(pathParam.getName()));
                }
        );

        verticalLayout.add(new Label("Path Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}
