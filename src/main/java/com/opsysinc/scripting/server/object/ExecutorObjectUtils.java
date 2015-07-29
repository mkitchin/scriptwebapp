package com.opsysinc.scripting.server.object;

import com.opsysinc.scripting.server.util.BaseUtils;

import java.util.Map;

/**
 * Executor object utils.
 *
 * @author mkitchin
 */
public class ExecutorObjectUtils {

    /**
     * Gets executor objects.
     *
     * @param target       Target map.
     * @param isClearFirst True toclear target map before insert, false otherwise.
     * @return True if target map changed, false otherwise.
     */
    public static boolean getExecutorObjects(final Map<String, Object> target,
                                             final boolean isClearFirst) {

        BaseUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        return true;
    }

    /**
     * Private ctor for utils class.
     */
    private ExecutorObjectUtils() {
    }
}
