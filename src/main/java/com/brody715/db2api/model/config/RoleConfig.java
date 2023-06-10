package com.brody715.db2api.model.config;

import com.brody715.db2api.model.perm.PermRoleKind;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoleConfig {
    String name;
    PermRoleKind kind = PermRoleKind.User;

    List<RolePermConfig> perms = new ArrayList<>();
}
