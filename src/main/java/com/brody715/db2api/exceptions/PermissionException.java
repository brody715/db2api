package com.brody715.db2api.exceptions;

public class PermissionException extends AppException {
    public PermissionException(String message, Object... params) {
        super(403, String.format(message, params));
    }
}
