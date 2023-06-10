package com.brody715.db2api.managers;

import com.brody715.db2api.exceptions.AppException;
import com.brody715.db2api.model.config.TableConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidateStore {
    TableConfig config;
    Map<String, Pattern> fieldPatternMap = new HashMap<>();

    public ValidateStore(TableConfig config) {
        this.config = config;
        config.getFields().forEach(fieldConfig -> {
            if (fieldConfig.getPattern() != null) {
                fieldPatternMap.put(fieldConfig.getName(), Pattern.compile(fieldConfig.getPattern()));
            }
        });
    }

    public void check(List<String> fields, List<List<Object>> values) {
        var fieldPatterns = fields.stream().map(f -> fieldPatternMap.get(f)).toList();

        for (var row : values) {
            for (int i = 0; i < row.size(); i++) {
                var value = row.get(i);
                var pattern = fieldPatterns.get(i);
                // pattern may be null
                if (pattern == null) {
                    continue;
                }
                if (value != null && !pattern.matcher(value.toString()).matches()) {
                    throw new AppException(400, String.format("Value %d, %s does not match pattern %s", i, value, pattern));
                }
            }
        }
    }

}

