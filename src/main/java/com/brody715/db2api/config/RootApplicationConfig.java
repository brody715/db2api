package com.brody715.db2api.config;

import com.brody715.db2api.model.config.RoleConfig;
import com.brody715.db2api.model.config.TableConfig;
import com.brody715.db2api.model.config.UserConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "db2api")
public class RootApplicationConfig {
    private List<TableConfig> tables = new ArrayList<>();
    private List<UserConfig> users = new ArrayList<>();
    private List<RoleConfig> roles = new ArrayList<>();
}
