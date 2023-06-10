package com.brody715.db2api.model.config;

import lombok.Data;

@Data
public class RolePermConfig {
    String role;
    String table;

    boolean all = false;
    boolean query = false;
    boolean insert = false;
    boolean update = false;
    boolean delete = false;
}
