package com.brody715.db2api.transforms;

import com.brody715.db2api.model.perm.Permission;

public class TableRequestPerm {
    public String table;
    public Permission permission = new Permission();
}
