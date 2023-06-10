package com.brody715.db2api.common;

import lombok.Data;

/**
 * @author brody
 */
@Data
public class BaseContext {

    /** states */
    private String username;

    private BaseContext() {}

    public static final ThreadLocal<BaseContext> THREAD_LOCAL = ThreadLocal.withInitial(BaseContext::new);

    public static BaseContext instance() {
        return THREAD_LOCAL.get();
    }
}
