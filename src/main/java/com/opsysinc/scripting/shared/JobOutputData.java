package com.opsysinc.scripting.shared;

/**
 * Job output data.
 *
 * @author mkitchin
 */
public class JobOutputData extends AbstractJobData {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Output body.
     */
    private String outputBody;

    /**
     * Basic ctor.
     */
    public JobOutputData() {
    }

    /**
     * Basic ctor.
     *
     * @param outputBody Output body.
     */
    public JobOutputData(final String outputBody) {

        this.setOutputBody(outputBody, false);
    }

    /**
     * Gets output body.
     *
     * @return Output body (may be null).
     */
    public synchronized String getOutputBody() {

        return this.outputBody;
    }

    /**
     * Sets output body.
     *
     * @param outputBody Output body (may be null).
     */
    public synchronized void setOutputBody(final String outputBody) {

        this.setOutputBody(outputBody, true);
    }

    /**
     * Sets output body.
     *
     * @param outputBody Output body (may be null).
     * @param isRequired True to require non-null input, false otherwise.
     */
    private void setOutputBody(final String outputBody, final boolean isRequired) {

        JobDataUtils.checkEmptyString(outputBody, isRequired);
        this.outputBody = outputBody;

        this.setModifiedTime(0L);
    }
}
