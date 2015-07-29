package com.opsysinc.scripting.server.engine;

import com.opsysinc.scripting.shared.JobDataUtils;
import com.opsysinc.scripting.shared.JobExecutorData;

import java.util.UUID;

/**
 * Job utilities.
 *
 * @author mkit
 */
public final class JobExecutorUtils {

    /**
     * Check job executor.
     *
     * @param executorData Job executor.
     * @param isIdRequired True to require id, false to populate if missing.
     */
    public static void checkJobExecutorData(final JobExecutorData executorData,
                                            final boolean isIdRequired) {

        JobDataUtils.checkNullObject(executorData, true);

        synchronized (executorData) {

            JobDataUtils.checkNullObject(executorData.getType(), true);

            if (JobDataUtils.checkEmptyString(executorData.getId(),
                    isIdRequired)) {

                executorData.setId(UUID.randomUUID().toString());
            }
        }
    }

    /**
     * Private ctor for util class.
     */
    private JobExecutorUtils() {
    }
}
