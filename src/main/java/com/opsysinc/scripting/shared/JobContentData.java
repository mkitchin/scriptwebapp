package com.opsysinc.scripting.shared;

import java.util.Arrays;

/**
 * R job.
 *
 * @author mkitchin
 */
public class JobContentData extends AbstractJobData {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Job request data.
     */
    private JobRequestData requestData;

    /**
     * Job response data.
     */
    private JobResponseData responseData;

    /**
     * Job output data.
     */
    private JobOutputData outputData;

    /**
     * Job executor data.
     */
    private JobExecutorData executorData;

    /**
     * Job status.
     */
    private JobStatus status;

    /**
     * Job state.
     */
    private JobState state;

    /**
     * JobContentData id.
     */
    private String id;

    /**
     * Resend script engine first.
     */
    private boolean isReset;

    /**
     * Basic ctor (GWT needs no-arg).
     */
    public JobContentData() {

        this.init();
    }

    /**
     * Basic ctor.
     *
     * @param requestData Request data.
     * @param isReset     True to reset prior to exec, false otherwise.
     */
    public JobContentData(final JobRequestData requestData,
                          final boolean isReset) {

        this.setRequestData(requestData);
        this.setIsReset(isReset);

        this.init();
    }

    /**
     * Basic ctor.
     *
     * @param requestData Request data.
     * @param isReset        True to reset prior to exec, false otherwise.
     */
    public JobContentData(final String requestData, final boolean isReset) {

        this(new JobRequestData(requestData), isReset);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        JobDataUtils.checkNullObject(obj, true);

        if (!(obj instanceof JobContentData)) {

            throw new ClassCastException("invalid input");
        }

        return String.valueOf(this.id).equals(
                String.valueOf(((JobContentData) obj).id));
    }

    /**
     * Gets job executor data.
     *
     * @return Job executor data.
     */
    public synchronized JobExecutorData getExecutorData() {

        return this.executorData;
    }

    /**
     * Gets job id.
     *
     * @return Job id.
     */
    public synchronized String getId() {

        return this.id;
    }

    /**
     * Gets job output data.
     *
     * @return Job output data.
     */
    public synchronized JobOutputData getOutputData() {

        return this.outputData;
    }

    /**
     * Gets job request data.
     *
     * @return Job request data.
     */
    public synchronized JobRequestData getRequestData() {

        return this.requestData;
    }

    /**
     * Gets job response data.
     *
     * @return Job response data.
     */
    public synchronized JobResponseData getResponseData() {

        return this.responseData;
    }

    /**
     * Gets state.
     *
     * @return State.
     */
    public synchronized JobState getState() {

        return this.state;
    }

    /**
     * Gets status.
     *
     * @return Status.
     */
    public synchronized JobStatus getStatus() {

        return this.status;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return String.valueOf(this.id).hashCode();
    }

    /**
     * One-off init method.
     */
    private void init() {

        this.setResponseData(new JobResponseData(null));
        this.setOutputData(new JobOutputData(null));

        this.status = JobStatus.success;
        this.state = JobState.pending;
    }

    /**
     * Gets is reset first.
     *
     * @return True to reset executor on job execution, false otherwise.
     */
    public synchronized boolean isReset() {

        return this.isReset;
    }

    /**
     * Sets job executor data.
     *
     * @param executorData Job executor data.
     */
    public synchronized void setExecutorData(
            final JobExecutorData executorData) {

        JobDataUtils.checkNullObject(executorData, true);
        this.executorData = executorData;

        this.setModifiedTime(0L);
    }

    /**
     * Sets job id.
     *
     * @param id Job id.
     */
    public synchronized void setId(final String id) {

        this.id = id;
        this.setModifiedTime(0L);
    }

    /**
     * Sets is reset first.
     *
     * @param isReset True to reset executor on job execution, false otherwise.
     */
    public synchronized void setIsReset(final boolean isReset) {

        this.isReset = isReset;
    }

    /**
     * Sets output data.
     *
     * @param outputData Output data.
     */
    public synchronized void setOutputData(final JobOutputData outputData) {

        JobDataUtils.checkNullObject(outputData, true);
        this.outputData = outputData;

        this.setModifiedTime(0L);
    }

    /**
     * Sets request data.
     *
     * @param requestData Request data.
     */
    public synchronized void setRequestData(
            final JobRequestData requestData) {

        JobDataUtils.checkNullObject(requestData, true);
        this.requestData = requestData;

        this.setModifiedTime(0L);
    }

    /**
     * Sets reset flag.
     *
     * @param isReset Reset flag.
     */
    public synchronized void setReset(final boolean isReset) {

        this.isReset = isReset;
    }

    /**
     * Sets response data.
     *
     * @param responseData Response data.
     */
    public synchronized void setResponseData(
            final JobResponseData responseData) {

        JobDataUtils.checkNullObject(responseData, true);
        this.responseData = responseData;

        this.setModifiedTime(0L);
    }

    /**
     * Sets state.
     *
     * @param state State.
     */
    public synchronized void setState(final JobState state) {

        JobDataUtils.checkNullObject(state, true);
        this.state = state;

        this.setModifiedTime(0L);
    }

    /**
     * Sets state and status.
     *
     * @param state  State (null = unchanged).
     * @param status Status (null = unchanged).
     */
    public synchronized void setStateAndStatus(final JobState state,
                                               final JobStatus status) {

        boolean isModified = false;

        if (state != null) {

            this.state = state;
            isModified = true;
        }

        if (status != null) {

            this.status = status;
            isModified = true;
        }

        if (isModified) {

            this.setModifiedTime(0L);
        }
    }

    /**
     * Sets status.
     *
     * @param status Status.
     */
    public synchronized void setStatus(final JobStatus status) {

        JobDataUtils.checkNullObject(this.state, true);
        this.status = status;

        this.setModifiedTime(0L);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return Arrays.asList(this.getClass().getSimpleName(),
                String.valueOf(this.id), String.valueOf(this.state),
                String.valueOf(this.status), this.getModifiedTime()).toString();
    }

}
