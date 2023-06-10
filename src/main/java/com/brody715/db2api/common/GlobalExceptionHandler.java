package com.brody715.db2api.common;

import com.brody715.db2api.exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public R<Object> handleException(AppException exception) {
        var r = R.error(exception.getCode(), exception.getMessage());
        if (exception.getCause() != null) {
            r.add("cause", exception.getCause().getMessage());
        }
        return r;
    }

    @ExceptionHandler(Exception.class)
    public R<Object> handleException(Exception exception) {
        if (exception instanceof AppException) {
            return handleException((AppException) exception);
        }

        return R.error(500, String.format("internal error: %s", exception.getMessage()));
    }
}
