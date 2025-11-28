package ru.practicum.stats.client.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(RestTemplate restTemplate, String serverUrl) {
        this.rest = restTemplate;
        this.serverUrl = serverUrl;
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(serverUrl));
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return rest.postForEntity(path, body, Object.class);
    }

    protected <T> ResponseEntity<T> get(String path, Map<String, Object> params, Class<T> responseType) {
        return rest.getForEntity(path, responseType, params);
    }
}
