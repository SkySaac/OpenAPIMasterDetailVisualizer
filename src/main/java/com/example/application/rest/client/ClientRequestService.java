package com.example.application.rest.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClientRequestService {
    private final RestTemplate restTemplate;


    public ClientRequestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<String> request(ClientRequestWrapper requestWrapper) {
        final var requestEntity = requestWrapper.getRequestEntity();
        return restTemplate.exchange(requestEntity, String.class);
    }
}
