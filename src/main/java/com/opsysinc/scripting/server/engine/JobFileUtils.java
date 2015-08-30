package com.opsysinc.scripting.server.engine;

import com.opsysinc.scripting.shared.JobDataUtils;
import com.opsysinc.scripting.shared.JobFileData;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Job utilities.
 *
 * @author mkit
 */
public final class JobFileUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER;

    static {

        LOGGER = Logger.getLogger(JobFileUtils.class.getName());
    }

    /**
     * Check job executor.
     *
     * @param fileData     Job executor.
     * @param isIdRequired True to require id, false to populate if missing.
     */
    public static void checkJobFileData(final JobFileData fileData,
                                        final boolean isIdRequired) {

        JobDataUtils.checkNullObject(fileData, true);

        synchronized (fileData) {

            JobDataUtils.checkNullObject(fileData.getExecutorData(), true);
            JobDataUtils.checkNullObject(fileData.getPath(), true);
            JobDataUtils.checkNullObject(fileData.getName(), true);
            JobDataUtils.checkNullObject(fileData.getType(), true);

            if (JobDataUtils.checkEmptyString(fileData.getId(),
                    isIdRequired)) {

                fileData.setId(UUID.randomUUID().toString());
            }
        }
    }

    /**
     * Checks file path.
     *
     * @param filePath  Prospective file path.
     * @param isToThrow True to throw exception on bad path, false otherwise.
     * @return True if this is a bad file path, false otherwise.
     */
    public static boolean checkBadFilePath(final String filePath,
                                           final boolean isToThrow) {

        JobDataUtils.checkNullObject(filePath, true);

        final String workFilePath = filePath.trim();
        final boolean result = workFilePath.startsWith(File.separator) ||
                workFilePath.startsWith("/") ||
                workFilePath.contains("..") ||
                workFilePath.contains("~");

        try {

            if (result && isToThrow) {

                throw new IllegalArgumentException("invalid input (null)");
            }

        } catch (final Throwable ex) {

            JobFileUtils.LOGGER.log(Level.WARNING, "checkBadFilePath()", ex);
            throw ex;
        }

        return result;
    }

    /**
     * Private ctor for util class.
     */
    private JobFileUtils() {
    }
}
