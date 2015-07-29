package com.opsysinc.scripting.shared;

/**
 * Job request data.
 *
 * @author mkitchin
 */
public class JobRequestData extends AbstractJobData {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Request body.
     */
    private String requestBody;

    /**
     * From host.
     */
    private String fromHost;

    /**
     * To host.
     */
    private String toHost;

    /**
     * Basic ctor.
     */
    public JobRequestData() {
    }

    /**
     * Basic ctor.
     *
     * @param requestBody Request body.
     */
    public JobRequestData(final String requestBody) {

        this.setRequestBody(requestBody, true);
    }

    /**
     * Gets from host.
     *
     * @return From host.
     */
    public synchronized String getFromHost() {

        return this.fromHost;
    }

    /**
     * Gets request body.
     *
     * @return Request body (may be null).
     */
    public synchronized String getRequestBody() {

        return this.requestBody;
    }

    /**
     * Gets to host.
     *
     * @return To host.
     */
    public synchronized String getToHost() {

        return this.toHost;
    }

    /**
     * Sets from host.
     *
     * @param fromHost From host.
     */
    public synchronized void setFromHost(final String fromHost) {

        this.fromHost = fromHost;
    }

    /**
     * Sets request body.
     *
     * @param requestBody Request body (may be null).
     */
    public synchronized void setRequestBody(final String requestBody) {

        this.setRequestBody(requestBody, true);
    }

    /**
     * Sets request body.
     *
     * @param requestBody Request body (may be null).
     * @param isRequired  True to require non-null input, false otherwise.
     */
    private void setRequestBody(final String requestBody,
                                final boolean isRequired) {

        JobDataUtils.checkEmptyString(requestBody, isRequired);
        this.requestBody = requestBody;

        this.setModifiedTime(0L);
    }

    /**
     * Sets to host.
     *
     * @param toHost To host.
     */
    public synchronized void setToHost(final String toHost) {

        this.toHost = toHost;
    }
}
