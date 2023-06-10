package com.brody715.db2api.transforms;

import com.brody715.db2api.model.api.nodes.JoinNode;
import com.brody715.db2api.model.api.OperationNode;
import com.brody715.db2api.model.api.QueryNode;
import com.brody715.db2api.model.api.nodes.ConditionNode;
import com.brody715.db2api.model.api.nodes.ConditionValueNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermTransformer {

    Map<String, TableRequestPerm> requestPermMap = new HashMap<>();

    public static PermResult transform(OperationNode node) {
        PermTransformer transformer = new PermTransformer();
        transformer.transformImpl(node);

        PermResult res = new PermResult();
        res.requestPermMap = transformer.requestPermMap;

        return res;
    }

    void transformImpl(OperationNode node) {
        if (node.batch != null) {
            for (OperationNode subNode: node.batch) {
                transformImpl(subNode);
            }
            return;
        }

        if (node.query != null) {
            transformQuery(node.query);
            return;
        }

        if (node.update != null) {
            TableRequestPerm perm = getTableUsedPerm(node.update.table);
            perm.permission.query = true;
            perm.permission.update = true;
            transformCondition(node.update.where);
            return;
        }

        if (node.delete != null) {
            TableRequestPerm perm = getTableUsedPerm(node.delete.table);
            perm.permission.query = true;
            perm.permission.delete = true;
            transformCondition(node.delete.where);
            return;
        }

        if (node.insert != null) {
            TableRequestPerm perm = getTableUsedPerm(node.insert.table);
            perm.permission.insert = true;
            return;
        }

        throw new RuntimeException("cmd type not support");
    }

    void transformQuery(QueryNode node) {
        TableRequestPerm perm = getTableUsedPerm(node.table);
        perm.permission.query = true;

        // transform join
        if (node.join != null) {
            for (JoinNode joinNode: node.join) {
                perm = getTableUsedPerm(joinNode.table);
                perm.permission.query = true;
                transformCondition(joinNode.on);
            }
        }
    }

    // if has sub query
    void transformCondition(ConditionNode node) {
        if (node == null) {
            return;
        }

        if (node.eq != null) {
            transformConditionValueList(node.eq);
            return;
        }

        if (node.gt != null) {
            transformConditionValueList(node.gt);
            return;
        }

        if (node.lt != null) {
            transformConditionValueList(node.lt);
            return;
        }

        if (node.in != null) {
            transformConditionValueList(node.in);
            return;
        }

        if (node.like != null) {
            transformConditionValueList(node.like);
            return;
        }

        if (node.and != null) {
            transformConditionList(node.and);
            return;
        }

        if (node.or != null) {
            transformConditionList(node.or);
        }

        if (node.not != null) {
            transformCondition(node.not);
        }
    }

    void transformConditionList(List<ConditionNode> nodes) {
        for (ConditionNode node: nodes) {
            transformCondition(node);
        }
    }

    void transformConditionValueList(List<ConditionValueNode> nodes) {
        for (ConditionValueNode node: nodes) {
            transformConditionValue(node);
        }
    }

    void transformConditionValue(ConditionValueNode node) {
        // only need subquery
        if (node.query != null) {
            transformQuery(node.query);
        }
    }

    TableRequestPerm getTableUsedPerm(String table) {
        TableRequestPerm perm = requestPermMap.get(table);
        if (perm == null) {
            perm = new TableRequestPerm();
            perm.table = table;
            requestPermMap.put(table, perm);
        }
        return perm;
    }
}
