package com.brody715.db2api.model.api;

import javax.annotation.Nullable;
import java.util.List;

public class OperationNode {
    @Nullable
    public QueryNode query;

    @Nullable
    public InsertNode insert;

    @Nullable
    public DeleteNode delete;

    @Nullable
    public UpdateNode update;

    // we will handle batch in main service, since DbExecutor can't execute on select
    // batch node, run multi sql sequentially
    @Nullable
    public List<OperationNode> batch;
}
