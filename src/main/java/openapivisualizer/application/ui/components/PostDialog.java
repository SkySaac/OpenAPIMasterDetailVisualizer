package openapivisualizer.application.ui.components;

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
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucPath;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.rest.client.restdatamodel.DataValue;
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
    private final List<InputValueComponent> queryFieldComponents = new ArrayList<>();
    private final List<InputValueComponent> pathFieldComponents = new ArrayList<>();
    private final Button postButton = new Button("Post");
    private DataPropertyType bodyPropertyType = DataPropertyType.OBJECT;

    public PostDialog(PostActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void open(StrucPath strucPath) { //TODO only needs strucpath since the schema is in there
        if (!strucPath.getPathParams().isEmpty())
            createPathParamFields(strucPath);
        if (!strucPath.getQueryParams().isEmpty())
            createQueryParamFields(strucPath);
        if (strucPath.getRequestStrucSchema() != null)
            createBodyFields(strucPath.getRequestStrucSchema());

        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        postButton.addClickListener(e -> this.postAction(strucPath.getPath()));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(postButton, closePostViewButton);
        add(horizontalLayout);

        this.open();
    }

    private MultiValueMap<String, String> collectQueryParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {
        };
        queryFieldComponents.forEach(component -> {
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
        if (bodyPropertyType.equals(DataPropertyType.OBJECT)) {
            Map<String, DataSchema> dataInputMap = new HashMap<>();
            inputFieldComponents.forEach(inputValueComponent -> {
                DataValue inputFieldValue = new DataValue(inputValueComponent.getComponent().getValue().toString(), inputValueComponent.getDataPropertyType());
                DataSchema inputFieldSchema = new DataSchema(inputValueComponent.getTitle(), inputFieldValue);
                dataInputMap.put(inputValueComponent.getTitle(), inputFieldSchema);

            });
            dataValue = new DataValue(dataInputMap, DataPropertyType.OBJECT);
        } else if (bodyPropertyType.equals(DataPropertyType.ARRAY)) {
            //TODO
        } else {
            dataValue = new DataValue(inputFieldComponents.get(0).getComponent().getValue().toString(), bodyPropertyType);
        }
        DataSchema dataSchema = new DataSchema("post", dataValue);

        if (areRequiredFieldsFilled(pathParams))
            actionListener.postAction(path, queryParams, pathParams, dataSchema);
    }

    private boolean areRequiredFieldsFilled(Map<String,String> pathParams) {
        return pathParams.size() == pathFieldComponents.size()
                && queryFieldComponents.stream().allMatch(component -> !component.getComponent().isRequiredIndicatorVisible()
                || (component.getComponent().isRequiredIndicatorVisible() && !component.getComponent().isEmpty()));
    }

    private void createBodyFields(StrucSchema schema) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();
        bodyPropertyType = schema.getStrucValue().getType();

        if (schema.getStrucValue().getType().equals(DataPropertyType.OBJECT))
            schema.getStrucValue().getProperties().keySet().forEach(key -> {
                        AbstractField abstractField = createEditorComponent(schema.getStrucValue().getProperties().get(key).getStrucValue().getType(), null, key, "input",false);
                        verticalLayoutContent.add(abstractField);
                    }
            );
        else if (schema.getStrucValue().getType().equals(DataPropertyType.ARRAY)) {
            //TODO was wenn array -> textfeld oder liste mit plus
        } else if (schema.getStrucValue().getType().equals(DataPropertyType.BOOLEAN)) {
            AbstractField abstractField = createEditorComponent(DataPropertyType.BOOLEAN, null, schema.getName(), "input",false);
            verticalLayoutContent.add(abstractField);
        } else if (schema.getStrucValue().getType().equals(DataPropertyType.INTEGER)) {
            AbstractField abstractField = createEditorComponent(DataPropertyType.INTEGER, null, schema.getName(), "input",false);
            verticalLayoutContent.add(abstractField);
        } else if (schema.getStrucValue().getType().equals(DataPropertyType.DOUBLE)) {
            AbstractField abstractField = createEditorComponent(DataPropertyType.DOUBLE, null, schema.getName(), "input",false);
            verticalLayoutContent.add(abstractField);
        } else if (schema.getStrucValue().getType().equals(DataPropertyType.STRING)) {
            AbstractField abstractField = createEditorComponent(DataPropertyType.STRING, null, schema.getName(), "input",false);
            verticalLayoutContent.add(abstractField);
        } //TODO hier noch format reinholen



        verticalLayout.add(new Label("Body"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }

    private AbstractField createEditorComponent(DataPropertyType type, String format, String title, String inputType,boolean required) {
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

        if (inputType.equals("input")) //TODO maybe add it later and not in this function
            inputFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        else if (inputType.equals("query"))
            queryFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        else if (inputType.equals("path"))
            pathFieldComponents.add(new InputValueComponent(title, inputComponent, type));

        return inputComponent;
    }

    private void createQueryParamFields(StrucPath strucPath) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getQueryParams().forEach(queryParam -> {
                    AbstractField abstractField = createEditorComponent(queryParam.getType(), queryParam.getFormat(), queryParam.getName(), "query",queryParam.isRequired());
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

        strucPath.getQueryParams().forEach(pathParam -> {
                    AbstractField abstractField = createEditorComponent(pathParam.getType(), pathParam.getFormat(), pathParam.getName(), "path",true);
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Path Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}
