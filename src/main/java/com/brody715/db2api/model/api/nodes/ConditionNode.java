package com.brody715.db2api.model.api.nodes;

import java.util.List;

public class ConditionNode {
    public List<ConditionValueNode> eq;
    public List<ConditionValueNode> gt;
    public List<ConditionValueNode> lt;
    public List<ConditionNode> and;
    public List<ConditionNode> or;

    public List<ConditionValueNode> like;

    public List<ConditionValueNode> in;

    public ConditionNode not;
}
