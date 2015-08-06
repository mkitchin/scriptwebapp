package com.opsysinc.scripting.shared;

/**
 * Job file data.
 * <p/>
 * Created by Michael J. Kitchin on 8/5/2015.
 */
public class JobFileData extends AbstractJobData {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Job executor data.
     */
    private JobExecutorData executorData;

    /**
     * Path.
     */
    private String filePath;

    /**
     * Name.
     */
    private String fileName;

    /**
     * File type.
     */
    private JobFileType fileType;

    /**
     * File size.
     */
    private long fileSize;

    /**
     * Modified date.
     */
    private long fileModified;

    /**
     * Basic ctor (GWT needs no-arg).
     */
    public JobFileData() {
    }

    /**
     * Basic ctor.
     *
     * @param executorData Executor data.
     * @param filePath     Path.
     * @param fileName     Name.
     * @param fileType     File type.
     */
    public JobFileData(final JobExecutorData executorData,
                       final String filePath,
                       final String fileName,
                       final JobFileType fileType,
                       final long fileSize,
                       final long modifiedDate) {

        this.executorData = executorData;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileModified = modifiedDate;
    }

    /**
     * Gets executor data.
     *
     * @return Executor data.
     */
    public synchronized JobExecutorData getExecutorData() {

        return this.executorData;
    }

    /**
     * Gets file path.
     *
     * @return File path.
     */
    public synchronized String getFilePath() {

        return this.filePath;
    }

    /**
     * Gets file name.
     *
     * @return File name.
     */
    public synchronized String getFileName() {

        return this.fileName;
    }

    /**
     * Gets file type.
     *
     * @return File type.
     */
    public synchronized JobFileType getFileType() {

        return this.fileType;
    }

    /**
     * Gets file size.
     *
     * @return File size.
     */
    public synchronized long getFileSize() {

        return this.fileSize;
    }

    /**
     * Gets file modified date.
     *
     * @return File modified date.
     */
    public synchronized long getFileModifiedDate() {

        return this.fileModified;
    }

    /**
     * Sets executor data.
     *
     * @param executorData Executor data.
     */
    public synchronized void setExecutorData(final JobExecutorData executorData) {

        this.executorData = executorData;
    }

    /**
     * Sets file path.
     *
     * @param filePath File path.
     */
    public synchronized void setFilePath(final String filePath) {

        this.filePath = filePath;
    }

    /**
     * Sets file name.
     *
     * @param fileName File name.
     */
    public synchronized void setFileName(final String fileName) {

        this.fileName = fileName;
    }

    /**
     * Sets file type.
     *
     * @param fileType File type.
     */
    public synchronized void setFileType(final JobFileType fileType) {

        this.fileType = fileType;
    }

    /**
     * Sets file size.
     *
     * @param fileSize File size.
     */
    public synchronized void setFileSize(final long fileSize) {

        this.fileSize = fileSize;
    }

    /**
     * Sets file modified date.
     *
     * @param fileModified File modified date.
     */
    public synchronized void setFileModifiedDate(final long fileModified) {

        this.fileModified = fileModified;
    }
}
