package com.brody715.db2api.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R<T> {
    private Integer code;
    private String msg;
    private T data;
    private Map extra = new HashMap();

    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.data = object;
        r.code = 200;
        return r;
    }

    public static <T> R<T> error(Integer code, String msg) {
        R<T> r = new R<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.extra.put(key, value);
        return this;
    }
}
