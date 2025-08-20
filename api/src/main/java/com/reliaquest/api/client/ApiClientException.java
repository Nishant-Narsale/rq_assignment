package com.reliaquest.api.client;

public class ApiClientException extends RuntimeException {

    private final int statusCode;

    public ApiClientException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
