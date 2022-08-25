package openapivisualizer.application.ui.components;

import openapivisualizer.application.rest.client.restdatamodel.DataSchema;
import openapivisualizer.application.rest.client.restdatamodel.DataValue;
import openapivisualizer.application.generation.structuremodel.DataPropertyType;
import openapivisualizer.application.generation.structuremodel.StrucPath;
import openapivisualizer.application.generation.structuremodel.StrucSchema;
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
        void putAction(String path, MultiValueMap<String, String> queryParameters, Map<String, String> pathParams, DataSchema properties);
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
            createBodyFields(strucPath.getRequestStrucSchema());

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
        pathFieldComponents.forEach(component ->
                params.put(component.getTitle(), component.getComponent().getValue().toString()));
        return params;
    }

    private void putAction(String path) {
        MultiValueMap<String, String> queryParams = collectQueryParams();
        Map<String, String> pathParams = collectPathParams();

        Map<String, DataSchema> dataInputMap = new HashMap<>();
        inputFieldComponents.forEach(inputValueComponent -> {
            DataValue inputFieldValue = new DataValue(inputValueComponent.getComponent().getValue().toString(), inputValueComponent.getDataPropertyType());
            DataSchema inputFieldSchema = new DataSchema(inputValueComponent.getTitle(), inputFieldValue);
            dataInputMap.put(inputValueComponent.getTitle(), inputFieldSchema);

        });
        DataValue dataValue = new DataValue(dataInputMap, DataPropertyType.OBJECT);
        DataSchema dataSchema = new DataSchema("post", dataValue);

        //TODO check if valid
        if (pathParams.size() == pathFieldComponents.size())
            actionListener.putAction(path, queryParams, pathParams, dataSchema);
    }

    private void createBodyFields(StrucSchema schema) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        schema.getStrucValue().getProperties().keySet().forEach(key -> {
                    AbstractField abstractField = createEditorComponent(schema.getStrucValue().getProperties().get(key).getStrucValue().getType(), null, key, "input");
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Body"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }

    private AbstractField createEditorComponent(DataPropertyType type, String format, String title, String inputType) {
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

        switch (inputType) {
            case "input" ->
                    //TODO maybe add it later and not in this function
                    inputFieldComponents.add(new InputValueComponent(title, inputComponent, type));
            case "query" -> querryFieldComponents.add(new InputValueComponent(title, inputComponent, type));
            case "path" -> pathFieldComponents.add(new InputValueComponent(title, inputComponent, type));
        }

        return inputComponent;
    }

    private void createQueryParamFields(StrucPath strucPath) {
        VerticalLayout verticalLayout = new VerticalLayout();
        VerticalLayout verticalLayoutContent = new VerticalLayout();

        strucPath.getQueryParams().forEach(queryParam -> { //TODO check if required
                    AbstractField abstractField = createEditorComponent(queryParam.getType(), queryParam.getFormat(), queryParam.getName(), "query");
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
                    AbstractField abstractField = createEditorComponent(pathParam.getType(), pathParam.getFormat(), pathParam.getName(), "path");
                    verticalLayoutContent.add(abstractField);
                }
        );

        verticalLayout.add(new Label("Path Parameters"));
        verticalLayout.add(verticalLayoutContent);
        add(verticalLayout);
    }


}


