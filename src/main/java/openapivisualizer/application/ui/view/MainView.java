package openapivisualizer.application.ui.view;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;


public class MainView extends HorizontalLayout {

    public interface ActionListener {
        void openApiAction(String source);

        void openSettings();

        void serverSelected(String server);

        void addServerToSelection(String server);

        void setCredential(String username, String password);
    }

    private final Select<String> serverListBox = new Select<>();

    public MainView(ActionListener actionListener, String defaultSource, String selectedServerURL, List<String> serverURLs) {
        setSizeFull();
        add(generationLayout(actionListener, defaultSource, selectedServerURL, serverURLs));
        add(credentialLayout(actionListener));

    }

    public VerticalLayout generationLayout(ActionListener actionListener, String defaultSource, String selectedServerURL, List<String> serverURLs) {
        VerticalLayout generationLayout = new VerticalLayout();
        generationLayout.setAlignItems(Alignment.CENTER);

        Label title = new Label("OpenAPI Generierung");
        title.getStyle().set("font-size", "large");
        generationLayout.add(title);

        TextField textField = new TextField("OpenAPI Doc");
        textField.setValue(defaultSource);
        textField.setPlaceholder("Path to your openApi.yaml");
        textField.setWidth(40, Unit.PERCENTAGE);
        generationLayout.add(textField);

        Button extractButton = new Button("Extract OpenAPI Structure!");
        extractButton.addClickListener(e -> actionListener.openApiAction(textField.getValue()));
        Button settingsButton = new Button(VaadinIcon.SLIDERS.create());
        settingsButton.addClickListener(event -> actionListener.openSettings());
        HorizontalLayout buttons = new HorizontalLayout(extractButton, settingsButton);
        generationLayout.add(buttons);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        TextField serverInput = new TextField();
        Button serverAddButton = new Button("Hinzufügen");
        serverAddButton.addClickListener(e -> actionListener.addServerToSelection(serverInput.getValue()));
        serverListBox.setItems(serverURLs);
        serverListBox.setValue(selectedServerURL);
        serverListBox.setWidth(40, Unit.PERCENTAGE);
        serverListBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                actionListener.serverSelected(e.getValue());
            }
        });
        horizontalLayout.add(serverInput, serverAddButton);
        horizontalLayout.setWidth(40, Unit.PERCENTAGE);

        generationLayout.add(serverListBox);
        generationLayout.add(horizontalLayout);

        return generationLayout;
    }

    public VerticalLayout credentialLayout(ActionListener actionListener) {
        VerticalLayout credentialLayout = new VerticalLayout();
        credentialLayout.setAlignItems(Alignment.CENTER);

        credentialLayout.setSpacing(false);

        Label title = new Label("Credentials");
        title.getStyle().set("font-size", "large");
        credentialLayout.add(title);

        TextField usernameField = new TextField("Username");
        credentialLayout.add(usernameField);

        PasswordField passwordField = new PasswordField("Password");
        credentialLayout.add(passwordField);

        Button applyButton = new Button("Speichern");
        applyButton.addClickListener(click -> actionListener.setCredential(usernameField.getValue(), passwordField.getValue()));
        credentialLayout.add(applyButton);
        Button clearButton = new Button("Löschen");
        clearButton.addClickListener(click -> {
            usernameField.clear();
            passwordField.clear();
            actionListener.setCredential(null, null);
        });
        credentialLayout.add(clearButton);

        return credentialLayout;
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
