package com.brody715.db2api.managers;

import com.brody715.db2api.model.config.RoleConfig;
import com.brody715.db2api.model.config.RolePermConfig;

import java.util.HashMap;
import java.util.Map;

class RoleStore {
    public RoleConfig config;
    public Map<String, RolePermConfig> tablePermMap = new HashMap<>();
}
