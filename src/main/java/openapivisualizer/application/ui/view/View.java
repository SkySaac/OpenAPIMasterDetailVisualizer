package openapivisualizer.application.ui.view;

import com.vaadin.flow.component.html.Div;
import lombok.Getter;

public class View extends Div {
    @Getter
    private final String tag;

    public View(String tag) {
        this.tag = tag;
    }
}
