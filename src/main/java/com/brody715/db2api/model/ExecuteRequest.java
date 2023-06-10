package com.brody715.db2api.model;

import com.brody715.db2api.model.api.OperationNode;
import lombok.Data;

@Data
public class ExecuteRequest {
    OperationNode op;
    String user;
}
