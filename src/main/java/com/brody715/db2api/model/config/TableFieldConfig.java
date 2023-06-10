package com.brody715.db2api.model.config;

import lombok.Data;

@Data
public class TableFieldConfig {
    private String name;
    private String type;
    private int maxLength;
    private String pattern;
    private String description;
    public String mdString(){
        return String.format("%s|%s|%d|%s", name,type,maxLength,description);
    }
}
