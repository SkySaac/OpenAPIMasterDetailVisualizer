package com.example.application.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;


public class AboutView extends VerticalLayout {

    public interface ActionListener {
        void openApiAction(String source);

        void serverSelected(String server);

        void addServerToSelection(String server);
    }


    private final Select<String> serverListBox = new Select<>();


    public AboutView(ActionListener actionListener, String defaultSource, String selectedServerURL, List<String> serverURLs) {
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        //TODO uploadButton

        TextField textField = new TextField("OpenAPI Doc");
        textField.setValue(defaultSource);
        add(textField);

        Button button = new Button("Extract OpenAPI Structure!");
        button.addClickListener(e -> actionListener.openApiAction(textField.getValue()));
        add(button);


        HorizontalLayout horizontalLayout = new HorizontalLayout();
        TextField serverInput = new TextField();
        Button serverAddButton = new Button("Add Server");
        serverAddButton.addClickListener(e -> actionListener.addServerToSelection(serverInput.getValue()));
        serverListBox.setItems(serverURLs);
        serverListBox.setValue(selectedServerURL);
        serverListBox.addValueChangeListener(e -> {
            if (e.getValue() != null)
                actionListener.serverSelected(e.getValue());
        });
        horizontalLayout.add(serverInput, serverAddButton);

        add(serverListBox);
        add(horizontalLayout);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    public void setServers(List<String> servers) {
        serverListBox.setItems(servers);
        if (servers.size() > 0)
            serverListBox.setValue(servers.get(0));
    }

    public void setSelectedServer(String server) {
        serverListBox.setValue(server);
    }

}
