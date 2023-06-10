package com.brody715.db2api.model.api;

import com.brody715.db2api.model.api.nodes.ConditionNode;

import java.util.Map;

public class UpdateNode {
    public String table;
    public Map<String, Object> set;
    public ConditionNode where;
}
