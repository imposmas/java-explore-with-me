package ru.practicum.stats.server.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String errorCode;
    private String message;
    private Map<String, String> errors;

    public ErrorResponse(String status, String errorCode, String message) {
        this(status, errorCode, message, null);
    }
}
