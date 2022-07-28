package com.example.application.ui.components.detaillayout.detailcomponents;

import com.example.application.data.dataModel.DataValue;
import com.helger.commons.url.URLValidator;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class TextComponent extends DetailComponent {

    public interface UrlNavigationListener {
        void navigate(String path);
    }

    private final TextField textField;
    private final Div listenerDiv = new Div();

    private Registration listenerRegistration = null;

    private final UrlNavigationListener urlNavigationListener;

    public TextComponent(UrlNavigationListener actionListener, String label) { //TODO needs actionlistener for navigating
        super(label);
        this.urlNavigationListener = actionListener;

        textField = new TextField(label);
        textField.setReadOnly(true);
        textField.setSizeFull();

        listenerDiv.add(textField);
        add(listenerDiv);
    }

    public TextComponent(UrlNavigationListener actionListener) {
        super("TODO");
        urlNavigationListener = actionListener;
        textField = new TextField();
        textField.setReadOnly(true);
        add(textField);
    }

    @Override
    public void fillDetailLayout(DataValue dataValue) {
        //TODO if is application link (starting with server url and going to a path that we know -> has GET) or else just normal url
        textField.setValue(String.valueOf(dataValue.getPlainValue()));

        if (URLValidator.isValid(textField.getValue())) {
            listenerRegistration = listenerDiv.addClickListener(e -> urlNavigationListener.navigate(textField.getValue()));
        } else if (URLValidator.isValid(textField.getValue().split("\\{\\?")[0])) {
            listenerRegistration = listenerDiv.addClickListener(e -> urlNavigationListener.navigate(textField.getValue().split("\\{\\?")[0]));
        }
        //TODO -> check if URL has serverName that we use -> if yes -> navigate -> if no open as new page
    }

    @Override
    public void clearDetailLayout() {
        textField.clear();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
            textField.getStyle().remove("color");
        }
    }
}
