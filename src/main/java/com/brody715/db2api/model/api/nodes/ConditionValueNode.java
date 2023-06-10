package com.brody715.db2api.model.api.nodes;

import com.brody715.db2api.model.api.QueryNode;

import javax.annotation.Nullable;
import java.util.List;

public class ConditionValueNode {
    @Nullable
    public String field;

    @Nullable
    public Object value;

    @Nullable
    public List<Object> values;

    public QueryNode query;
}
