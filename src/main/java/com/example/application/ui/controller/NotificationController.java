package com.example.application.ui.controller;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    public void postNotification(String text, boolean isError){
        Notification notification = Notification.show(text);
        if(!isError)
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        else
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

    }
}
