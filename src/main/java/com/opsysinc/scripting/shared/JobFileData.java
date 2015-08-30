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
     * Executor id.
     */
    private String id;

    /**
     * Job executor data.
     */
    private JobExecutorData executorData;

    /**
     * Path.
     */
    private String path;

    /**
     * Name.
     */
    private String name;

    /**
     * File type.
     */
    private JobFileType type;

    /**
     * File size.
     */
    private long size;

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
     * @param name         Name.
     * @param type         File type.
     * @param size         File size.
     */
    public JobFileData(final JobExecutorData executorData,
                       final String filePath,
                       final String name,
                       final JobFileType type,
                       final long size) {

        this.executorData = executorData;
        this.path = filePath;
        this.name = name;
        this.type = type;
        this.size = size;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public synchronized boolean equals(final Object obj) {

        JobDataUtils.checkNullObject(obj, true);

        if (!(obj instanceof JobFileData)) {

            throw new ClassCastException("invalid input");
        }

        return String.valueOf(this.id).equals(
                String.valueOf(((JobFileData) obj).id));
    }

    /**
     * Gets id.
     *
     * @return Id.
     */
    public synchronized String getId() {

        return this.id;
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
    public synchronized String getPath() {

        return this.path;
    }

    /**
     * Gets file name.
     *
     * @return File name.
     */
    public synchronized String getName() {

        return this.name;
    }

    /**
     * Gets file type.
     *
     * @return File type.
     */
    public synchronized JobFileType getType() {

        return this.type;
    }

    /**
     * Gets file size.
     *
     * @return File size.
     */
    public synchronized long getSize() {

        return this.size;
    }

    /**
     * Sets executor data.
     *
     * @param executorData Executor data.
     */
    public synchronized void setExecutorData(final JobExecutorData executorData) {

        this.executorData = executorData;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public synchronized int hashCode() {

        return String.valueOf(this.id).hashCode();
    }

    /**
     * Sets id.
     *
     * @param id ID.
     */
    public synchronized void setId(final String id) {

        this.id = id;
        this.setModifiedTime(0L);
    }

    /**
     * Sets file path.
     *
     * @param path File path.
     */
    public synchronized void setPath(final String path) {

        this.path = path;
    }

    /**
     * Sets file name.
     *
     * @param name File name.
     */
    public synchronized void setName(final String name) {

        this.name = name;
    }

    /**
     * Sets file type.
     *
     * @param type File type.
     */
    public synchronized void setType(final JobFileType type) {

        this.type = type;
    }

    /**
     * Sets file size.
     *
     * @param size File size.
     */
    public synchronized void setSize(final long size) {

        this.size = size;
    }
}
