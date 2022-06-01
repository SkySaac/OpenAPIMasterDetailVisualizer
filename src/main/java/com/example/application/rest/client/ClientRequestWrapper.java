package com.example.application.rest.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class ClientRequestWrapper {

    private final HttpMethod httpMethod;
    private final UriComponentsBuilder uriBuilder;

    private final DefaultRequestBuilder requestBuilder = new DefaultRequestBuilder();

    public ClientRequestWrapper(@NonNull HttpMethod httpMethod, @NonNull String baseUrl) {
        this.httpMethod = httpMethod;
        uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);
    }

    public RequestEntity<?> getRequestEntity() {
        uriBuilder.path(requestBuilder.path);
        requestBuilder.queryParams.forEach(uriBuilder::queryParam);

        final var builder = RequestEntity.method(httpMethod, uriBuilder.build().toUri());
        builder.headers(requestBuilder.headers);
        builder.contentType(MediaType.APPLICATION_JSON);
        builder.accept(MediaType.APPLICATION_JSON);

        return builder.body(requestBuilder.body);
    }

    public RequestBuilder requestBuilder() {
        return requestBuilder;
    }

    public interface RequestBuilder {

        RequestBuilder body(String body);

        RequestBuilder path(String path);

        RequestBuilder queryParam(String name, String value);

        RequestBuilder queryParams(MultiValueMap<String, String> params);

        RequestBuilder header(String name, String value);

        RequestBuilder header(HttpHeaders headers);

        RequestBuilder bearerAuth(String accessToken);

        RequestBuilder basicAuth(String username,String password);

    }

    private static class DefaultRequestBuilder implements RequestBuilder {

        private String body;
        private String path;
        private final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        private final HttpHeaders headers = new HttpHeaders();

        @Override
        public RequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        @Override
        public RequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        @Override
        public RequestBuilder queryParam(String name, String value) {
            if (queryParams.containsKey(name)) {
                queryParams.get(name).add(value);
            }
            else {
                queryParams.put(name, List.of(value));
            }
            return this;
        }

        @Override
        public RequestBuilder queryParams(MultiValueMap<String, String> params) {
            queryParams.putAll(params);
            return this;
        }

        @Override
        public RequestBuilder header(String name, String value) {
            headers.add(name, value);
            return this;
        }

        @Override
        public RequestBuilder bearerAuth(String accessToken) {
            headers.setBearerAuth(accessToken);
            return this;
        }

        @Override
        public RequestBuilder basicAuth(String username, String password) {
            headers.setBasicAuth(username,password);
            return this;
        }

        @Override
        public RequestBuilder header(HttpHeaders headers) {
            this.headers.addAll(headers);
            return this;
        }
    }

}
