package com.example.application.rest.client;

import com.example.application.ui.controller.NotificationController;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class ClientRequestService extends DefaultResponseErrorHandler {

    private final NotificationController notificationController;
    private final RestTemplate restTemplate;

    private RequestEntity<?> recentRequestEntity = null;

    public ClientRequestService(NotificationController notificationController, RestTemplateBuilder restTemplateBuilder) {
        this.notificationController = notificationController;
        this.restTemplate = restTemplateBuilder.errorHandler(this).build();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        notificationController.postNotification(response.getStatusCode().toString(),true);
    }

    public ResponseEntity<String> request(ClientRequestWrapper requestWrapper) {
        final var requestEntity = requestWrapper.getRequestEntity();
        recentRequestEntity = requestEntity;
        return restTemplate.exchange(requestEntity, String.class);
    }
}
