package com.opsysinc.scripting.server.util;

import java.util.EnumMap;
import java.util.Map;

/***
 * Thread local with a enum-keyed map.
 *
 * @author mkitchin
 */
public class ThreadLocalMap extends ThreadLocal<Map<ThreadLocalKey, Object>> {

    /**
     * Lazy holder for safe, unsynchronized singleton.
     *
     * @author mkitchin
     */
    private static final class LazyHolder {

        private static final ThreadLocalMap INSTANCE = new ThreadLocalMap();
    }

    /**
     * Gets instance.
     *
     * @return Instance.
     */
    public static ThreadLocalMap getInstance() {

        return ThreadLocalMap.LazyHolder.INSTANCE;
    }

    @Override
    protected Map<ThreadLocalKey, Object> initialValue() {

        return new EnumMap<>(ThreadLocalKey.class);
    }
}
