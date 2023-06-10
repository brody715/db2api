package com.brody715.db2api.service;

import cn.hutool.db.Db;
import com.brody715.db2api.exceptions.AppException;
import com.brody715.db2api.managers.AuthManager;
import com.brody715.db2api.model.*;
import com.brody715.db2api.transforms.PermResult;
import com.brody715.db2api.transforms.PermTransformer;
import com.brody715.db2api.model.api.OperationNode;
import com.brody715.db2api.model.api.QueryNode;
import com.brody715.db2api.transforms.SqlResult;
import com.brody715.db2api.transforms.SqlTransformer;
import com.brody715.db2api.utils.AssertUtils;
import com.brody715.db2api.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RootService {

    @Autowired
    private AuthManager authManager;

    @Autowired
    private Db rootDb;

    public ExecuteResponse execute(ExecuteRequest input) throws Exception {
        OperationNode node = input.getOp();

        // check permission
        PermResult permResult = PermTransformer.transform(node);
        authManager.check(permResult, input.getUser());

        ExecuteResponse resp = new ExecuteResponse();
        resp.debug = new ExecuteDebugData();

        //批处理
        if (node.batch != null) {
            rootDb.tx((db1 -> {
                List<OperationNode> batch = node.batch;
                List<Object> datas = new ArrayList<>();

                for (int i = 0; i < batch.size(); i++) {
                    OperationNode one = batch.get(i);
                    ExecuteOneSqlResponse oneSqlResponse = executeOneSql(db1, one);
                    resp.debug.datas.add(new ExecuteOneSqlDebugData(oneSqlResponse.sql, oneSqlResponse.params));
                    datas.add(oneSqlResponse.result);
                }

                resp.data = datas;
            }));

            return resp;
        }

        // not batch
        ExecuteOneSqlResponse oneSqlResponse = executeOneSql(rootDb, node);
        resp.data = oneSqlResponse.result;
        resp.debug.datas.add(new ExecuteOneSqlDebugData(oneSqlResponse.sql, oneSqlResponse.params));
        return resp;
    }

    ExecuteOneSqlResponse executeOneSql(Db db, OperationNode node) throws Exception {
        SqlResult sqlRes = SqlTransformer.transformOperation(node);
        log.info("execute sql: {}", sqlRes.sql);

        ExecuteOneSqlResponse sqlResponse = new ExecuteOneSqlResponse();

        sqlResponse.sql = sqlRes.sql;
        sqlResponse.params = sqlRes.params;

        if (node.query != null && node.query.page != null) {
            sqlResponse.result = executePage(db, sqlRes, node);
            return sqlResponse;
        }

        if (node.query != null) {
            sqlResponse.result = executeQuery(db, sqlRes, node);
            return sqlResponse;
        }

        // Mutation
        sqlResponse.result = executeMutation(db, sqlRes, node);

        return sqlResponse;
    }

    private Object executeQuery(Db db, SqlResult sql, OperationNode node) throws Exception {
        QueryNode query = node.query;
        AssertUtils.require(query != null, "query is null");
        AssertUtils.require(query.page == null, "page is not null");

        try {
            return db.query(sql.sql, sql.params.toArray());
        } catch (Exception e) {
            throw new AppException(400, e.getMessage());
        }
    }

    private Object executePage(Db db, SqlResult sql, OperationNode node) throws Exception {
        QueryNode query = node.query;
        AssertUtils.require(query != null, "query is null");
        AssertUtils.require(query.page != null, "page is null");

        SqlResult countSql = SqlTransformer.transformCount(node.query);
        log.info("page: run count sql: {}", countSql.sql);

        PageResult pageResult = new PageResult();

        pageResult.currentPage = query.page.currentPage;
        pageResult.pageSize = query.page.pageSize;

        // run total

        Map<String, Object> totalQueryResult = DbUtils.parseQueryResult(db.query(countSql.sql, countSql.params.toArray()));
        pageResult.total = totalQueryResult.get("total");

        try {
            pageResult.data = db.query(sql.sql, sql.params.toArray());
        } catch (Exception e) {
            throw new AppException(400, e.getMessage());
        }

        return pageResult;
    }

    private Object executeMutation(Db db, SqlResult sql, OperationNode node) throws Exception {
        Map<String, String> data = new HashMap<>();

        try {
            int rowCount = db.execute(sql.sql, sql.params.toArray());
            data.put("affectedRows", String.valueOf(rowCount));
        } catch (Exception e) {
            throw new AppException(400, e.getMessage());
        }

        return data;
    }
}