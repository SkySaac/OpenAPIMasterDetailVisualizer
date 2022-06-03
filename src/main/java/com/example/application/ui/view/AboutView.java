package com.example.application.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;


public class AboutView extends VerticalLayout {

    public interface ActionListener {
        void openApiAction(String source);
        void serverAction(String server);
    }


    private final Select<String> serverListBox = new Select<>();

    private List<String> serverList = new ArrayList<>();


    public AboutView(ActionListener actionListener, String defaultSource) {
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
        serverAddButton.addClickListener(e-> {
            serverList.add(serverInput.getValue());
            setServers(serverList);
        });
        serverListBox.addValueChangeListener(e-> {
            if(e.getValue()!=null)
                actionListener.serverAction(e.getValue());
        });
        horizontalLayout.add(serverInput, serverAddButton);

        add(horizontalLayout);
        add(serverListBox);


        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    public void setServers(List<String> servers) {
        serverList = servers;
        serverListBox.setItems(serverList);
        if(servers.size()>0)
            serverListBox.setValue(serverList.get(0));
    }

}
