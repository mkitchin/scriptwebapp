package com.opsysinc.scripting.shared;

import java.io.Serializable;

/**
 * Job response data.
 *
 * @author mkitchin
 */
public class JobResponseData extends AbstractJobData {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Response data.
     */
    private String responseBody;

    /**
     * Basic ctor.
     */
    public JobResponseData() {
    }

    /**
     * Basic ctor.
     *
     * @param responseBody Response body.
     */
    public JobResponseData(final String responseBody) {

        this.setResponseBody(responseBody, false);
    }

    /**
     * Gets response body.
     *
     * @return Response body (may be null).
     */
    public synchronized String getResponseBody() {

        return this.responseBody;
    }

    /**
     * Sets response body.
     *
     * @param responseBody Response body (may be null).
     */
    public synchronized void setResponseBody(final String responseBody) {

        this.setResponseBody(responseBody, true);
    }

    /**
     * Sets response body.
     *
     * @param responseBody Response body (may be null).
     * @param isRequired   True to require non-null input, false otherwise.
     */
    private void setResponseBody(final String responseBody,
                                 final boolean isRequired) {

        JobDataUtils.checkEmptyString(responseBody, isRequired);
        this.responseBody = responseBody;

        this.setModifiedTime(0L);
    }
}
