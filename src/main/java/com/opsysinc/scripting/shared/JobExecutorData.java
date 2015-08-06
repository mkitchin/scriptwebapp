package com.opsysinc.scripting.shared;

import java.util.Arrays;

/**
 * Job executor data.
 *
 * @author mkitchin
 */
public class JobExecutorData extends AbstractJobData {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Executor id.
     */
    private String id;

    /**
     * Executor title.
     */
    private String title;

    /**
     * Executor type.
     */
    private JobExecutorType type;

    /**
     * Basic ctor (GWT needs no-arg).
     */
    public JobExecutorData() {
    }

    /**
     * Basic ctor.
     *
     * @param title Title.
     * @param type  Type.
     */
    public JobExecutorData(final String title, final JobExecutorType type) {

        this.setTitle(title);
        this.setType(type);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public synchronized boolean equals(final Object obj) {

        JobDataUtils.checkNullObject(obj, true);

        if (!(obj instanceof JobExecutorData)) {

            throw new ClassCastException("invalid input");
        }

        return String.valueOf(this.id).equals(
                String.valueOf(((JobExecutorData) obj).id));
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
     * Gets title.
     *
     * @return Title.
     */
    public synchronized String getTitle() {

        return this.title;
    }

    /**
     * Gets type.
     *
     * @return Type.
     */
    public synchronized JobExecutorType getType() {

        return this.type;
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
     * Sets title.
     *
     * @param title Title.
     */
    public synchronized void setTitle(final String title) {

        JobDataUtils.checkEmptyString(title, true);
        this.title = title;

        this.setModifiedTime(0L);
    }

    /**
     * Sets type.
     *
     * @param type Type.
     */
    public synchronized void setType(final JobExecutorType type) {

        JobDataUtils.checkNullObject(type, true);
        this.type = type;

        this.setModifiedTime(0L);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public synchronized String toString() {

        return Arrays.asList(this.getClass().getSimpleName(),
                String.valueOf(this.id), String.valueOf(this.title),
                String.valueOf(this.type)).toString();
    }

}
