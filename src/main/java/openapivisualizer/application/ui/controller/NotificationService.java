package openapivisualizer.application.ui.controller;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.spring.annotation.UIScope;
import org.checkerframework.checker.guieffect.qual.UI;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;

@Controller
@UIScope
public class NotificationService {

    public void postNotification(String text, boolean isError){
        Notification notification = Notification.show(text);
        if(!isError)
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        else
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

    }
}
