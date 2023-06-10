package com.brody715.db2api.utils;

import com.brody715.db2api.exceptions.AppException;

public class SqlInjectionUtils {
    private static final String XSS_STR = "'|and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|,";

    public static void checkContent(String value) {
        if (value == null || "".equals(value)) {
            return;
        }
        value = value.toLowerCase();
        String[] xssArr = XSS_STR.split("\\|");
        for (String s : xssArr) {
            if (value.contains(s)) {
                throw new AppException(10002, "Value may cause sql injection error: " + value);
            }
        }
    }
}