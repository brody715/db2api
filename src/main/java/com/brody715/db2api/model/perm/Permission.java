package com.brody715.db2api.model.perm;

import com.brody715.db2api.exceptions.PermissionException;

public class Permission {
    public boolean query = false;
    public boolean insert = false;
    public boolean update = false;
    public boolean delete = false;
}
