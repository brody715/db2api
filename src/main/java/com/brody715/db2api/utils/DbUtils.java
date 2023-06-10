package com.brody715.db2api.utils;

import java.util.List;
import java.util.Map;

public class DbUtils {
    public static Map<String, Object> parseQueryResult(Object obj) {
        AssertUtils.require(obj instanceof List, "query result must be a list");
        List<Map<String, Object>> result = (List<Map<String, Object>>) obj;
        return result.get(0);
    }
}
