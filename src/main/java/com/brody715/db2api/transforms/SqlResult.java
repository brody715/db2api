package com.brody715.db2api.transforms;

import java.util.ArrayList;
import java.util.List;

/**
 * @author brody
 * SQL Transform Result
 */
public class SqlResult {
    public boolean isQuery = false;
    public String sql;
    public List<Object> params = new ArrayList<>();

    static SqlResult create() {
        return new SqlResult();
    }
}
