package com.brody715.db2api.model.api.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;

enum JoinType {
    // inner join
    @JsonProperty("inner")
    INNER,

    // left join
    @JsonProperty("left")
    LEFT,

    // right join
    @JsonProperty("right")
    RIGHT,

}

public class JoinNode {
    public JoinType type = JoinType.INNER;

    public String table;
    public ConditionNode on;

    public String getJoinString() {
        switch (this.type) {
            case INNER:
                return "INNER JOIN";
            case LEFT:
                return "LEFT JOIN";
            case RIGHT:
                return "RIGHT JOIN";
            default:
                throw new RuntimeException(String.format("invalid join type %s", this.type.name()));
        }
    }
}
