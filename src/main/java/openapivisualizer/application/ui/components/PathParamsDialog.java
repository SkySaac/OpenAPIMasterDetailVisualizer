package openapivisualizer.application.ui.components;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class PathParamsDialog extends Dialog {

    public interface PathParamsActionListener {
        void applyPathParams(String path, MultiValueMap<String, String> pathParams);
    }

    private final PathParamsActionListener pathParamsActionListener;

    public PathParamsDialog(PathParamsActionListener pathParamsActionListener) {
        this.pathParamsActionListener = pathParamsActionListener;
        setWidth(50, Unit.PERCENTAGE);

    }

    public void open(String path, List<String> pathParams) {
        Button saveButton = new Button("Save");
        MultiValueMap<String, TextField> pathParamTextfields = createPathParams(pathParams);

        Button closePostViewButton = new Button("Close");
        closePostViewButton.addClickListener(e -> this.close());

        saveButton.addClickListener(e -> this.save(path, pathParamTextfields));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(saveButton, closePostViewButton);
        add(horizontalLayout);

        this.open();
    }

    private void save(String path, MultiValueMap<String, TextField> pathParamTextfields) {
        if (pathParamTextfields.values().stream().allMatch(textFields -> textFields.stream().noneMatch(AbstractField::isEmpty))) {
            MultiValueMap<String, String> collectedPathParams = collectPathParams(pathParamTextfields);

            pathParamsActionListener.applyPathParams(path, collectedPathParams);
            this.close();
        }
    }

    private MultiValueMap<String, String> collectPathParams(MultiValueMap<String, TextField> pathParamTextfields) {
        MultiValueMap<String, String> pathParams = new LinkedMultiValueMap<>();
        pathParamTextfields.forEach((key, value) -> {
            List<String> params = value.stream().map(TextField::getValue).toList();
            pathParams.put(key, params);
        });
        return pathParams;
    }

    private MultiValueMap<String, TextField> createPathParams(List<String> pathParams) {
        MultiValueMap<String, TextField> pathParamTextfields = new LinkedMultiValueMap<>();

        pathParams.forEach(pathParam -> {
            TextField textField = new TextField(pathParam);
            textField.setRequired(true);
            this.add(textField);
            pathParamTextfields.add(pathParam, textField);
        });

        return pathParamTextfields;
    }
}
