package com.opsysinc.scripting.shared;

/**
 * Job executor type.
 *
 * @author mkitchin
 */
public enum JobExecutorType {

    r_renjin("Renjin", "R", false),
    python_jython("python", "Python", true),
    javascript_rhino("js", "JavaScript", true),
    groovy_groovy("groovy", "Groovy", true),
    lisp_abcl("lisp", "LISP", true);

    /**
     * Scripting engine type.
     */
    private final String scriptingEngineName;

    /**
     * Scripting engine type.
     */
    private final String executorTitle;

    /**
     * Has script engine bindings.
     */
    private final boolean isVariablesReadable;

    /**
     * Basic ctor (GWT needs no-arg).
     */
    private JobExecutorType() {

        this(null, null, false);
    }

    /**
     * Basic ctor.
     *
     * @param scriptingEngineName Scripting engine type.
     */
    private JobExecutorType(final String scriptingEngineName,
                            final String executorTitle,
                            final boolean isVariablesReadable) {

        this.scriptingEngineName = scriptingEngineName;
        this.executorTitle = executorTitle;
        this.isVariablesReadable = isVariablesReadable;
    }

    /**
     * Gets scripting engine title.
     *
     * @return Scripting engine title.
     */
    public String getExecutorTitle() {

        return this.executorTitle;
    }

    /**
     * Gets scripting engine type.
     *
     * @return Scripting engine type.
     */
    public String getScriptEngineName() {

        return this.scriptingEngineName;
    }

    /**
     * Gets if variables are readable.
     *
     * @return True if variables are readable, false otherwise.
     */
    public boolean isVariablesReadable() {

        return this.isVariablesReadable;
    }
}
