package ru.practicum.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Slf4j
public class BaseClient {

    protected final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(RestTemplate restTemplate, String serverUrl) {
        this.rest = restTemplate;
        this.serverUrl = serverUrl;
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(serverUrl));
    }

    protected <T> ResponseEntity<T> makeAndSendRequest(
            HttpMethod method,
            String path,
            @Nullable Map<String, Object> parameters,
            @Nullable Object body,
            Class<T> responseType
    ) {
        try {
            log.debug("HTTP {} request to {} with params={} and body={}",
                    method, path, parameters, body);

            HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders());

            ResponseEntity<T> response = (parameters != null && !parameters.isEmpty())
                    ? rest.exchange(path, method, requestEntity, responseType, parameters)
                    : rest.exchange(path, method, requestEntity, responseType);

            log.debug("Response: status={}, body={}", response.getStatusCode(), response.getBody());
            return response;

        } catch (HttpStatusCodeException e) {
            log.error("Stats server error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(null);
        } catch (Exception e) {
            log.error("Unexpected error during request: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    protected <T> ResponseEntity<T> get(String path, Map<String, Object> params, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, params, null, responseType);
    }

    protected <T> ResponseEntity<T> post(String path, Object body, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body, responseType);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}