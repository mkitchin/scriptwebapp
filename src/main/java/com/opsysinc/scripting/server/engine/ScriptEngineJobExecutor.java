package com.opsysinc.scripting.server.engine;

import com.opsysinc.scripting.server.util.FormatUtils;
import com.opsysinc.scripting.shared.JobDataFormat;
import com.opsysinc.scripting.shared.JobDataUtils;
import com.opsysinc.scripting.shared.JobExecutorData;
import com.thoughtworks.xstream.XStream;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Script engine (JSR223) job executor.
 *
 * @author mkitchin
 */
public class ScriptEngineJobExecutor extends AbstractJobExecutor {

    /**
     * Script engine manager.
     */
    private ScriptEngineManager scriptEngineManager;

    /**
     * Script engine.
     */
    private ScriptEngine scriptEngine;

    /**
     * Basic ctor.
     *
     * @param jobManager   Job manager.
     * @param executorData Executor data.
     */
    public ScriptEngineJobExecutor(final JobManager jobManager,
                                   final JobExecutorData executorData) {

        super(jobManager, executorData);
        this.init();
    }

    @Override
    protected void cleanUpImpl() throws Throwable {

    }

    /**
     * Creates script engine.
     *
     * @return Script engine.
     */
    private ScriptEngine createScriptEngine() {

        final String scriptEngineName = this.getExecutorData().getType().getScriptEngineName();
        ScriptEngine result = this.scriptEngineManager.getEngineByName(scriptEngineName);

        if (result == null) {

            result = this.scriptEngineManager.getEngineByExtension(scriptEngineName);
        }

        return result;
    }

    /**
     * Gets script engine.
     *
     * @return Script engine.
     */
    public ScriptEngine getScriptEngine() {

        return this.scriptEngine;
    }

    /**
     * Gets script engine manager.
     *
     * @return Script engine manager.
     */
    public ScriptEngineManager getScriptEngineManager() {

        return this.scriptEngineManager;
    }

    @Override
    protected boolean getVariablesImpl(final int variableScope, final int variableFormat,
                                       final Map<String, String> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        final Bindings bindings = this.scriptEngine.getBindings(variableScope);
        final JobDataFormat dataFormat = JobDataFormat.values()[variableFormat];

        boolean result = false;

        if ((bindings != null) &&
                !bindings.isEmpty()) {

            final XStream xstream = FormatUtils.getFormatXStream(dataFormat);

            for (final Map.Entry<String, Object> item : bindings.entrySet()) {

                final String keyText = item.getKey().trim();

                final Object valueObject = item.getValue();
                String valueText = null;

                if (!JobDataUtils.checkNullObject(valueObject, false)) {

                    valueText = FormatUtils.formatObject(valueObject, xstream);
                }

                if (target.put(keyText, valueText) == null) {

                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Single-use init method.
     */
    private void init() {

        this.scriptEngineManager = new ScriptEngineManager();
    }

    @Override
    protected void resetScriptEngineImpl() {

        this.scriptEngine = this.createScriptEngine();

        if (this.scriptEngine == null) {

            throw new IllegalStateException("Can't create script engine");
        }

        if (this.getExecutorData().getType().isVariablesReadable()) {

            final Bindings engineBindings = this.scriptEngine
                    .getBindings(ScriptContext.ENGINE_SCOPE);

            final Map<String, Object> executorObjects = new HashMap<>();
            this.getJobManager().getExecutorObjects(executorObjects, false);

            for (final Map.Entry<String, Object> item : executorObjects.entrySet()) {

                engineBindings.put(item.getKey(), item.getValue());
            }
        }
    }

    @Override
    protected Object runWorkImpl(final String inputText) throws Throwable {

        JobDataUtils.checkEmptyString(inputText, true);
        return this.scriptEngine.eval(inputText);
    }

    @Override
    protected void startUpImpl() throws Throwable {

    }
}
