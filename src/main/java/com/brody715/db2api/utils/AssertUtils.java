package com.brody715.db2api.utils;

import com.brody715.db2api.exceptions.AppException;

public class AssertUtils {
    public static void require(boolean condition, String fmt, Object... params) {
        if (!condition) {
            throw new AppException(400, String.format(fmt, params));
        }
    }
}
