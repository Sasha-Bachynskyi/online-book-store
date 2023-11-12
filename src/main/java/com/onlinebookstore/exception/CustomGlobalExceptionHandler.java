package com.onlinebookstore.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String STATUS = "Status";
    public static final String TIME = "Time";
    public static final String ERRORS = "Errors";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistration(RegistrationException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    private String getErrorMessage(ObjectError error) {
        String message = error.getDefaultMessage();
        if (error instanceof FieldError) {
            String field = ((FieldError) error).getField();
            message = field + " " + message;
        }
        return message;
    }

    private Map<String, Object> createErrorBody(HttpStatus status, List<String> errors) {
        Map<String, Object> body = new HashMap<>();
        body.put(TIME, LocalDateTime.now());
        body.put(STATUS, status);
        body.put(ERRORS, errors);
        return body;
    }
}
