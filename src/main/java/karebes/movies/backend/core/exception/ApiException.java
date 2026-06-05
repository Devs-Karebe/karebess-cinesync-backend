package karebes.movies.backend.core.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;
    private final String userMessage;

    protected ApiException(String message, String userMessage, HttpStatus status, String errorCode) {
        super(message);
        this.userMessage = userMessage;
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }
}