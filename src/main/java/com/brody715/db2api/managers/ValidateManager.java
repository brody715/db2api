package com.brody715.db2api.managers;

import com.brody715.db2api.config.RootApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ValidateManager {

    private Map<String, ValidateStore> tableStoreMap = new HashMap<>();

    public ValidateManager(RootApplicationConfig config) {
        config.getTables().forEach(tableConfig -> {
            ValidateStore store = new ValidateStore(tableConfig);
            tableStoreMap.put(tableConfig.getName(), store);
        });
    }
}
