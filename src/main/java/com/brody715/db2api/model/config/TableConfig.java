package com.brody715.db2api.model.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableConfig {
    private String name;
    private List<TableFieldConfig> fields = new ArrayList<>();
}
