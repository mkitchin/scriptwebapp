package com.opsysinc.scripting.server.engine;

import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobDataUtils;

import java.util.UUID;

/**
 * Job utilities.
 *
 * @author mkit
 */
public final class JobContentUtils {

    /**
     * Check job state.
     *
     * @param contentData  Job state.
     * @param isIdRequired True to require id, false to populate if missing.
     */
    public static void checkJobStateData(final JobContentData contentData,
                                         final boolean isIdRequired) {

        JobDataUtils.checkNullObject(contentData, true);

        synchronized (contentData) {

            JobDataUtils.checkNullObject(contentData.getRequestData(), true);
            JobExecutorUtils.checkJobExecutorData(
                    contentData.getExecutorData(), true);

            if (JobDataUtils
                    .checkEmptyString(contentData.getId(), isIdRequired)) {

                contentData.setId(UUID.randomUUID().toString());
            }
        }
    }

    /**
     * Private ctor for util class.
     */
    private JobContentUtils() {
    }
}
