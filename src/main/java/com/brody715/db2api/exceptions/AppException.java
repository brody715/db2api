package com.brody715.db2api.exceptions;

public class AppException extends RuntimeException {

    private Exception cause;
    private final Integer code;
    private final String message;

    AppException(Exception cause, Integer code, String message) {
        this.cause = cause;
        this.code = code;
        this.message = message;
    }

    public AppException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Exception getCause() {
        return cause;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
