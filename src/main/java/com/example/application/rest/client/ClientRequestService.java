package com.example.application.rest.client;

import com.example.application.ui.controller.NotificationService;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@Slf4j
@UIScope
public class ClientRequestService extends DefaultResponseErrorHandler {

    private final NotificationService notificationService;
    private final RestTemplate restTemplate;

    public ClientRequestService(NotificationService notificationService, RestTemplateBuilder restTemplateBuilder) {
        this.notificationService = notificationService;
        this.restTemplate = restTemplateBuilder.errorHandler(this).build();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        notificationService.postNotification(response.getStatusCode().toString(),true);
    }

    protected ResponseEntity<String> request(ClientRequestWrapper requestWrapper) {
        final var requestEntity = requestWrapper.getRequestEntity();
        log.info("Sending {} request to: {} ",requestEntity.getMethod().toString(), requestEntity.getUrl());
        return restTemplate.exchange(requestEntity, String.class);
    }
}
