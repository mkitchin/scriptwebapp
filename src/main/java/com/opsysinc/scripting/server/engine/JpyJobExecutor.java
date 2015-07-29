package com.opsysinc.scripting.server.engine;

import com.google.gwt.dev.util.collect.IdentityHashSet;
import com.opsysinc.scripting.shared.JobExecutorData;
import org.jpy.PyLib;
import org.jpy.PyModule;
import org.jpy.PyObject;

import java.util.Map;
import java.util.Set;

/**
 * JPY (Java->CPython) job executor.
 *
 * @author mkitchin
 */
public class JpyJobExecutor extends AbstractJobExecutor {

    /**
     * Checks if python should be started.
     *
     * @return True if python was started, false otherwise.
     */
    public static boolean checkStartPython() {

        boolean result = false;

        synchronized (JpyJobExecutor.class) {

            if (JpyJobExecutor.EXECUTOR_THREADS.isEmpty()
                    && !PyLib.isPythonRunning()) {

                PyLib.Diag.setFlags(PyLib.Diag.F_ALL);
                PyLib.startPython();

                JpyJobExecutor.BUILTINS_MODULE = PyModule
                        .importModule("builtins");

                result = true;

            }

            JpyJobExecutor.EXECUTOR_THREADS.add(Thread.currentThread());
        }

        return result;
    }

    /**
     * Checks if python should be stopped.
     *
     * @return True if python was stopped, false otherwise.
     */
    public static boolean checkStopPython() {

        boolean result = false;

        synchronized (JpyJobExecutor.class) {

            JpyJobExecutor.EXECUTOR_THREADS.remove(Thread.currentThread());

            if (JpyJobExecutor.EXECUTOR_THREADS.isEmpty()
                    && PyLib.isPythonRunning()) {

                PyLib.stopPython();
                result = true;
            }
        }

        return result;
    }

    /**
     * Exector threads.
     */
    private static final Set<Thread> EXECUTOR_THREADS;

    /**
     * Code module.
     */
    private static PyModule BUILTINS_MODULE;

    static {

        EXECUTOR_THREADS = new IdentityHashSet<>();
    }

    /**
     * Basic ctor.
     *
     * @param jobManager   Job manager.
     * @param executorData Executor data.
     */
    public JpyJobExecutor(final JobManager jobManager,
                          final JobExecutorData executorData) {

        super(jobManager, executorData);
    }

    @Override
    protected void cleanUpImpl() throws Throwable {

        JpyJobExecutor.checkStopPython();
    }

    @Override
    protected boolean getVariablesImpl(final int variableScope,
                                       final Map<String, String> target) {
        return false;
    }

    @Override
    protected void resetScriptEngineImpl() {

        // ignore;
    }

    @Override
    protected Object runWorkImpl(final String inputText) throws Throwable {

        final PyObject resultObject = JpyJobExecutor.BUILTINS_MODULE.call(
                "eval", inputText);
        String result = null;

        if (resultObject != null) {

            result = resultObject.getStringValue();
        }

        return result;
    }

    @Override
    protected void startUpImpl() throws Throwable {

        JpyJobExecutor.checkStartPython();
    }
}
