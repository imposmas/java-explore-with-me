package ru.practicum.stats.server.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * 404 ERROR CODE
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("NOT_FOUND", "ENTITY_NOT_FOUND", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * 400 ERROR CODE
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("BAD_REQUEST", "VALIDATION_ERROR", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * 500 ERROR CODE
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(
                new ErrorResponse("BAD_REQUEST", "VALIDATION_ERROR", "Validation failed", fieldErrors),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * 409 ERROR CODE FOR ARGUMENTS
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Internal server error", ex);
        return new ResponseEntity<>(
                new ErrorResponse("INTERNAL_SERVER_ERROR", "SERVER_ERROR", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
