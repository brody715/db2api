package com.brody715.db2api.transforms;

import com.brody715.db2api.model.api.*;
import com.brody715.db2api.model.api.nodes.ConditionValueNode;
import com.brody715.db2api.model.api.nodes.JoinNode;
import com.brody715.db2api.model.api.nodes.ConditionNode;
import com.brody715.db2api.utils.SqlInjectionUtils;
import com.brody715.db2api.utils.StringPrinter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlTransformer {

    private final StringPrinter printer = new StringPrinter();
    private final List<Object> params = new ArrayList<>();

    private static final Pattern FIELD_IDENT_REG = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*$");

    private SqlTransformer() {
    }

    public static SqlResult transformOperation(OperationNode op) throws Exception {
        SqlResult result = new SqlResult();
        SqlTransformer transformer = new SqlTransformer();
        transformer.transformOperationImpl(op);

        if (op.query != null) {
            result.isQuery = true;
        }

        result.sql = transformer.printer.toString();
        result.params = transformer.params;
        return result;
    }

    public static SqlResult transformCount(QueryNode queryNode) throws Exception {
        SqlResult result = new SqlResult();
        SqlTransformer transformer = new SqlTransformer();
        transformer.transformCountImpl(queryNode);

        result.isQuery = true;

        result.sql = transformer.printer.toString();
        result.params = transformer.params;
        return result;
    }

    private void transformOperationImpl(OperationNode op) throws Exception {
        if (op.query != null) {
            transformQuery(op.query);
            return;
        }

        if (op.insert != null) {
            transformInsert(op.insert);
            return;
        }

        if (op.delete != null) {
            transformDelete(op.delete);
            return;
        }
        if (op.update != null) {
            transformUpdate(op.update);
            return;
        }

        throw new RuntimeException("no supported command");
    }

    private void transformCountImpl(QueryNode queryNode) {
        printer.printf("select count(*) as total from %s", quoteTableName(queryNode.table));

        if (queryNode.where != null) {
            printer.printf(" where ");
            transformCondition(queryNode.where);
        }

        if (queryNode.group != null) {
            printer.printf(" group by %s", quoteFields(queryNode.group));

        }
        if (queryNode.having != null) {
            printer.printf(" having ");
            transformCondition(queryNode.having);
        }

        if (queryNode.join != null) {
            for (JoinNode join : queryNode.join) {
                transformJoin(join);
            }
        }

        // we don't need to sort
    }

    void transformQuery(QueryNode queryNode) {

        if (queryNode.fields.isEmpty()) {
            throw new RuntimeException("fields is empty");
        }

        printer.printf("select %s from %s", quoteFields(queryNode.fields), quoteTableName(queryNode.table));

        if (queryNode.where != null) {
            printer.printf(" where ");
            transformCondition(queryNode.where);
        }

        if (queryNode.group != null) {
            printer.printf(" group by %s", quoteFields(queryNode.group));

        }
        if (queryNode.having != null) {
            printer.printf(" having ");
            transformCondition(queryNode.having);
        }

        if (queryNode.join != null) {
            for (JoinNode join : queryNode.join) {
                transformJoin(join);
            }
        }

        if (queryNode.order != null) {
            transformOrder(queryNode.order);
        }

        // TODO: when usePage is set, transform page
        if (queryNode.page != null) {
            int offset = queryNode.page.pageSize * (queryNode.page.currentPage - 1);
            // LIMIT 1 OFFSET 10
            printer.printf(" limit %d offset %d", queryNode.page.pageSize, offset);
        }
    }

    void transformUpdate(UpdateNode updateNode) {
        require(updateNode.set != null, "require update set");

        printer.printf("update %s set ", updateNode.table);

        String setStr = updateNode.set.entrySet().stream().map(e -> {
            params.add(e.getValue());
            return String.format("%s = ?", quoteField(e.getKey()));
        }).collect(Collectors.joining(", "));

        printer.print(setStr);

        if (updateNode.where != null) {
            printer.print(" where ");
            transformCondition(updateNode.where);
        }
    }

    void transformInsert(InsertNode insertNode) {

        String fields = quoteFields(insertNode.fields);

        printer.printf("insert into %s(%s) values", insertNode.table, fields);

        String preparedValueParams = insertNode.values.stream().map(v -> String.format("(%s)", getPreparedMarks(v.size()))).collect(Collectors.joining(", "));

        printer.print(preparedValueParams);

        for (List<Object> value : insertNode.values) {
            params.addAll(value);
        }
    }

    void transformDelete(DeleteNode deleteNode) {
        printer.printf("delete from %s", deleteNode.table);
        if (deleteNode.where != null) {
            printer.printf(" where ");
            transformCondition(deleteNode.where);
        }
    }

    void transformCondition(ConditionNode condition) {

        if (condition.eq != null) {
            transformConditionValues("=", condition.eq);
            return;
        }
        if (condition.gt != null) {
            transformConditionValues(">", condition.gt);
            return;
        }
        if (condition.lt != null) {
            transformConditionValues("<", condition.lt);
            return;
        }
        if (condition.and != null) {
            transformConditionNodes("and", condition.and);
            return;
        }

        if (condition.or != null) {
            transformConditionNodes("or", condition.or);
            return;
        }

        if (condition.like != null) {
            transformConditionValues("like", condition.like);
            return;
        }

        if (condition.in != null) {
            require(condition.in.size() == 2, "in condition size != 2");
            transformConditionValue(condition.in.get(0));
            printer.printf(" in ");
            transformConditionValue(condition.in.get(1));
            return;
        }
        if (condition.not != null) {
            printer.printf("not ");
            printer.printf("(");
            transformCondition(condition.not);
            printer.printf(")");
            return;
        }

        throw new RuntimeException("not supported condition node");
    }

    void transformConditionValue(ConditionValueNode conditionValue) {
        if (conditionValue.field != null) {
            printer.printf("%s", quoteField(conditionValue.field));
            return;
        }

        if (conditionValue.value != null) {
            printer.printf("?");
            params.add(conditionValue.value);
            return;
        }

        if (conditionValue.values != null) {
            printer.printf("(");
            for (int i = 0; i < conditionValue.values.size(); i++) {
                printer.printf("?");
                params.add(conditionValue.values.get(i));
                if (i != conditionValue.values.size() - 1) {
                    printer.printf(", ");
                }
            }
            printer.printf(")");
            return;
        }

        if (conditionValue.query != null) {
            printer.printf("(");
            transformQuery(conditionValue.query);
            printer.printf(")");
            return;
        }
    }

    void transformConditionValues(String delim, List<ConditionValueNode> nodes) {
        if (nodes.size() < 1) {
            throw new RuntimeException("where operands size < 1");
        }

        for (int i = 0; i < nodes.size(); i++) {
            ConditionValueNode node = nodes.get(i);
            // we won't add paren for field or value
            transformConditionValue(node);
            if (i != nodes.size() - 1) {
                printer.printf(" %s ", delim);
            }
        }
    }

    void transformConditionNodes(String delim, List<ConditionNode> nodes) {
        if (nodes.size() < 1) {
            throw new RuntimeException("where operands size < 1");
        }

        for (int i = 0; i < nodes.size(); i++) {
            ConditionNode node = nodes.get(i);
            printer.printf("(");
            transformCondition(node);
            printer.printf(")");
            if (i != nodes.size() - 1) {
                printer.printf(" %s ", delim);
            }
        }
    }

    void transformOrder(List<String> order) {
        printer.printf(" order by ");
        int size = order.size();
        for (int i = 0; i < size - 1; i++) {
            String s = order.get(i);
            int len = s.length();
            if (s.charAt(len - 1) == '-') {
                printer.printf(s.substring(0, len - 1));
                printer.printf(" desc");
            } else if (s.charAt(len - 1) == '+') {
                printer.printf(s.substring(0, len - 1));
            } else {
                printer.printf(s);
            }
            printer.printf(",");
        }
        String s = order.get(size - 1);
        int len = s.length();
        if (s.charAt(len - 1) == '-') {
            printer.printf(s.substring(0, len - 1));
            printer.printf(" desc");
        } else if (s.charAt(len - 1) == '+') {
            printer.printf(s.substring(0, len - 1));
        } else {
            printer.printf(s);
        }
    }

    void transformJoin(JoinNode join) {
        String joinType = join.getJoinString();

        require(join.on != null, "join on is null");

        // SELECT * FROM t1 JOIN t2 ON xx = xx
        printer.printf(" %s %s ON ", joinType, quoteTableName(join.table));

        // condition
        transformCondition(join.on);
    }


    void require(boolean condition, String fmt, Object... params) {
        if (!condition) {
            throw new RuntimeException(String.format(fmt, params));
        }
    }

    String quoteFields(List<String> fields) {
        List<String> newFields = fields.stream().map(this::quoteField).collect(Collectors.toList());
        return String.join(", ", newFields);
    }

    String quoteField(String name) {
        if (patternMatches(FIELD_IDENT_REG, name)) {
            return quoteIdent(name);
        }

        SqlInjectionUtils.checkContent(name);

        if (name.contains(".")) {
            String[] parts = name.split("\\.");
            return quoteIdent(parts[0]) + "." + quoteIdent(parts[1]);
        }
        return name;
    }

    String quoteTableName(String name) {
        SqlInjectionUtils.checkContent(name);
        return quoteIdent(name);
    }

    String quoteIdent(String name) {
        if (Objects.equals(name, "*")) {
            return name;
        }

        if (name.startsWith("`") && name.endsWith("`")) {
            return name;
        }
        // TODO: Check valid
        return "`" + name + "`";
    }

    String getPreparedMarks(int count) {
        return String.join(", ", Collections.nCopies(count, "?"));
    }

    boolean patternMatches(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
