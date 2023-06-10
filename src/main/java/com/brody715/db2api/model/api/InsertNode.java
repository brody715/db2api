package com.brody715.db2api.model.api;

import java.util.List;
import java.util.ArrayList;

public class InsertNode {
    public String table;
    public List<String> fields = new ArrayList<>();
    public List<List<Object>> values = new ArrayList<>();
}
