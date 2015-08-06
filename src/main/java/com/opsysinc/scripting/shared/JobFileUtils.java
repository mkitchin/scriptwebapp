package com.opsysinc.scripting.shared;

import com.opsysinc.scripting.shared.JobDataUtils;

import java.io.File;

/**
 * File utils.
 * <p/>
 * Created by Michael J. Kitchin on 8/5/2015.
 */
public final class JobFileUtils {

    /**
     * Basic ctor (private for utils class).
     */
    private JobFileUtils() {
    }

    /**
     * Checks file path.
     *
     * @param filePath File path.
     */
    public static void checkFilePath(final String filePath) {

        JobDataUtils.checkNullObject(filePath, true);

        if (filePath.startsWith(File.separator) ||
                filePath.startsWith("/") ||
                filePath.contains("..") ||
                filePath.contains("~")) {

            throw new IllegalArgumentException("invalid input");
        }
    }
}

