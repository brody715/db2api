package com.brody715.db2api.model.api;

import com.brody715.db2api.model.api.nodes.JoinNode;
import com.brody715.db2api.model.api.nodes.ConditionNode;
import com.brody715.db2api.model.api.nodes.QueryPageNode;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class QueryNode {
    public List<String> fields = new ArrayList<>();
    public String table;

    @Nullable
    public ConditionNode where;

    @Nullable
    public List<String> group;

    @Nullable
    public ConditionNode having;

    @Nullable
    public List<String> order;
    @Nullable
    @JsonFormat( with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<JoinNode> join;

    @Nullable
    public QueryPageNode page;
}


