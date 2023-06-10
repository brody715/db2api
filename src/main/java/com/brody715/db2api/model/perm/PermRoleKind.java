package com.brody715.db2api.model.perm;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PermRoleKind {
    @JsonProperty("admin")
    Admin,
    @JsonProperty("user")
    User,
}
