package openapivisualizer.application.ui.service;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Controller;

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
