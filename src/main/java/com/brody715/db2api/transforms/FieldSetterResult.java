package com.brody715.db2api.transforms;

import lombok.Data;

import java.util.List;

@Data
public class FieldSetterResult {
    String table;
    List<String> fields;
    List<List<Object>> values;
}
