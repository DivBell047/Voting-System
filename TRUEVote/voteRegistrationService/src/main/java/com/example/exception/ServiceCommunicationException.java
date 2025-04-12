package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) // Or INTERNAL_SERVER_ERROR
public class ServiceCommunicationException extends RuntimeException {
    public ServiceCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
    public ServiceCommunicationException(String message) {
        super(message);
    }
}
