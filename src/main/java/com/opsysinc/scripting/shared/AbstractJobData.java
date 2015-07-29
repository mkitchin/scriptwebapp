package com.opsysinc.scripting.shared;

import java.io.Serializable;

/**
 * Abstract job data.
 *
 * @author mkitchin
 */
public abstract class AbstractJobData implements Serializable {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Created date.
     */
    private long createdTime;

    /**
     * Modified date.
     */
    private long modifiedTime;

    /**
     * Basic ctor (GWT needs on-arg).
     */
    public AbstractJobData() {

        this.init();
    }

    /**
     * Gets pending date/time.
     *
     * @return Created date/time.
     */
    public synchronized long getCreatedTime() {

        return this.createdTime;
    }

    /**
     * Gets modified date/time.
     *
     * @return Modified date/time.
     */
    public synchronized long getModifiedTime() {

        return this.modifiedTime;
    }

    /**
     * Single-use init method.
     */
    private void init() {

        this.setCreatedTime(0L);
    }

    /**
     * Sets pending date (cascades to modified date).
     *
     * @param createdTime Created date (0L = now).
     */
    public synchronized void setCreatedTime(final long createdTime) {

        long workCreatedTime = createdTime;

        if (workCreatedTime <= 0L) {

            workCreatedTime = System.currentTimeMillis();
        }

        this.createdTime = workCreatedTime;
        this.setModifiedTime(workCreatedTime);
    }

    /**
     * Sets modified date.
     *
     * @param modifiedTime Modified date (0L = now).
     */
    public synchronized void setModifiedTime(final long modifiedTime) {

        long workModifiedTime = modifiedTime;

        if (workModifiedTime <= 0L) {

            workModifiedTime = System.currentTimeMillis();
        }

        this.modifiedTime = workModifiedTime;
    }

}
