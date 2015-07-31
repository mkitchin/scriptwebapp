package com.opsysinc.scripting.shared;

/**
 * Job data format.
 * <p/>
 * Created by Michael J. Kitchin on 7/31/2015.
 */
public enum JobDataFormat {

    raw("Raw"), json("JSON"), xml("XML");

    /**
     * Basic ctor.
     *
     * @param formatTitle Format title.
     */
    private JobDataFormat(final String formatTitle) {

        this.formatTitle = formatTitle;
    }

    /**
     * Format title.
     */
    private final String formatTitle;

    public String getFormatTitle() {

        return this.formatTitle;
    }
}
