package com.brody715.db2api.utils;

public class StringPrinter {
    StringBuilder sb = new StringBuilder();

    public void print(String data) {
        sb.append(data);
    }

    public void printf(String fmt, Object... params) {
        sb.append(String.format(fmt, params));
    }

    public String toString() {
        return sb.toString();
    }
}
