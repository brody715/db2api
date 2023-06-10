package com.brody715.db2api.managers;

import com.brody715.db2api.exceptions.PermissionException;
import com.brody715.db2api.model.config.RoleConfig;
import com.brody715.db2api.model.config.RolePermConfig;
import com.brody715.db2api.config.RootApplicationConfig;
import com.brody715.db2api.model.config.UserConfig;
import com.brody715.db2api.model.perm.PermRoleKind;
import com.brody715.db2api.transforms.PermResult;
import com.brody715.db2api.transforms.TableRequestPerm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class AuthManager {

    // role to perm
    private Map<String, RoleStore> rolePermMap = new HashMap<>();

    // username to perm
    private Map<String, RoleStore> userPermMap = new HashMap<>();

    AuthManager(RootApplicationConfig config) {
        log.info("load config: users.size={}, roles.size={}", config.getUsers().size(), config.getRoles().size());

        for (RoleConfig role : config.getRoles()) {
            RoleStore store = new RoleStore();
            store.config = role;
            for (RolePermConfig perm : role.getPerms()) {
                store.tablePermMap.put(perm.getTable(), perm);
            }

            this.rolePermMap.put(role.getName(), store);
        }

        for (UserConfig user : config.getUsers()) {

            // get perm
            RoleStore store = this.rolePermMap.get(user.getRole());
            if (store == null) {
                log.error("role {} not found", user.getRole());
                throw new RuntimeException(String.format("role %s not found for user %s", user.getRole(), user.getName()));
            }

            this.userPermMap.put(user.getName(), store);
        }
    }

    public void check(PermResult request, String username) throws PermissionException {

        RoleStore userRoleStore = userPermMap.get(username);
        if (userRoleStore == null) {
            throw new PermissionException("user %s not found", username);
        }

        for (Map.Entry<String, TableRequestPerm> entry : request.requestPermMap.entrySet()) {
            String table = entry.getKey();
            TableRequestPerm reqPerm = entry.getValue();

            // if user is admin, grant to all
            if (userRoleStore.config.getKind() == PermRoleKind.Admin) {
                return;
            }

            RolePermConfig rolePerm = userRoleStore.tablePermMap.get(table);
            if (rolePerm == null) {
                throw new PermissionException("user %s has no perm on table %s", username, table);
            }

            // grant all perm
            if (rolePerm.isAll()) {
                return;
            }

            if (reqPerm.permission.query && !rolePerm.isQuery()) {
                throw new PermissionException("user %s has no query perm on table %s", username, table);
            }

            if (reqPerm.permission.insert && !rolePerm.isInsert()) {
                throw new PermissionException("user %s has no insert perm on table %s", username, table);
            }

            if (reqPerm.permission.update && !rolePerm.isUpdate()) {
                throw new PermissionException("user %s has no update perm on table %s", username, table);
            }

            if (reqPerm.permission.delete && !rolePerm.isDelete()) {
                throw new PermissionException("user %s has no delete perm on table %s", username, table);
            }
        }
        return;
    }
}
