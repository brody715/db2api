package com.brody715.db2api.model;

import java.util.List;

public class ExecuteOneSqlDebugData {

    public ExecuteOneSqlDebugData(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public String sql;
    public List<Object> params;
}
