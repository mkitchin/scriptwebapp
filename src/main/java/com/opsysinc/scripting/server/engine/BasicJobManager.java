package com.opsysinc.scripting.server.engine;

import com.opsysinc.scripting.server.object.ExecutorObjectUtils;
import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobDataUtils;
import com.opsysinc.scripting.shared.JobExecutorData;
import com.opsysinc.scripting.shared.JobExecutorType;
import org.python.core.Options;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Job manager.
 *
 * @author mkitchin
 */
public final class BasicJobManager implements JobManager {

    /**
     * Job executor factory.
     *
     * @author mkitchin
     */
    private interface JobExecutorFactory {

        /**
         * Creates job executor (main method).
         *
         * @return Job executor.
         */
        JobExecutor createJobExecutor(JobManager jobManager,
                                      JobExecutorData executorData);
    }

    /**
     * Holder for printstream/bytearrayoutputstream pair.
     *
     * @author mkit
     */
    private final class OutputStreamEntry {

        /**
         * Print stream.
         */
        private final PrintStream printStream;

        /**
         * Output stream.
         */
        private final ByteArrayOutputStream outputStream;

        /**
         * Is enabled?
         */
        private boolean isEnabled;

        /**
         * Basic ctor.
         */
        private OutputStreamEntry() {

            this.outputStream = new ByteArrayOutputStream();
            this.printStream = new PrintStream(this.outputStream, true);
            this.isEnabled = false;
        }

        /**
         * Close everything.
         */
        private void close() {

            try {

                this.outputStream.close();

            } catch (final Throwable ignore) {

                // ignore;
            }

            try {

                this.printStream.close();

            } catch (final Throwable ignore) {

                // ignore;
            }
        }

        /**
         * Gets output stream.
         *
         * @return Output stream.
         */
        private ByteArrayOutputStream getOutputStream() {

            return this.outputStream;
        }

        /**
         * Gets print stream.
         *
         * @return Print stream.
         */
        private PrintStream getPrintStream() {

            return this.printStream;
        }

        /**
         * Gets is enabled?
         *
         * @return Is enabled?
         */
        private boolean isEnabled() {

            return this.isEnabled;
        }

        /**
         * Read/truncate output stream.
         *
         * @return Truncated output.
         */
        private String readOutput() {

            this.printStream.flush();

            final String result = this.outputStream.toString();
            this.outputStream.reset();

            return result;
        }

        /**
         * Sets is enabled?
         *
         * @param isEnabled Is enabled?
         */
        private void setEnabled(final boolean isEnabled) {

            this.isEnabled = isEnabled;
        }
    }

    /**
     * Output stream handler.
     *
     * @author mkit
     */
    private class OutputStreamHandler extends PrintStream {

        /**
         * Parent stream.
         */
        private final PrintStream parentStream;

        /**
         * Output streams.
         */
        private final Map<Thread, BasicJobManager.OutputStreamEntry> outputStreams;

        /**
         * Basic ctor.
         *
         * @param parentStream  Parent stream.
         * @param outputStreams Output streams map.
         */
        public OutputStreamHandler(final PrintStream parentStream,
                                   final Map<Thread, BasicJobManager.OutputStreamEntry> outputStreams) {

            super(parentStream, true);

            this.parentStream = parentStream;
            this.outputStreams = outputStreams;
        }

        @Override
        public PrintStream append(final char c) {

            return this.getPrintStream().append(c);
        }

        @Override
        public PrintStream append(final CharSequence csq) {

            return this.getPrintStream().append(csq);
        }

        @Override
        public PrintStream append(final CharSequence csq, final int start,
                                  final int end) {

            return this.getPrintStream().append(csq, start, end);
        }

        @Override
        public boolean checkError() {

            return this.getPrintStream().checkError();
        }

        @Override
        public void close() {

            this.getPrintStream().close();
        }

        @Override
        public void flush() {

            this.getPrintStream().flush();
        }

        @Override
        public PrintStream format(final Locale l, final String format,
                                  final Object... args) {

            return this.getPrintStream().format(l, format, args);
        }

        @Override
        public PrintStream format(final String format, final Object... args) {

            return this.getPrintStream().format(format, args);
        }

        /**
         * Gets print stream.
         *
         * @return Print stream.
         */
        private PrintStream getPrintStream() {

            PrintStream result = this.parentStream;
            BasicJobManager.OutputStreamEntry outputEntry = null;

            synchronized (this.outputStreams) {

                outputEntry = this.outputStreams.get(Thread.currentThread());
            }

            if ((outputEntry != null) && outputEntry.isEnabled()) {

                result = outputEntry.getPrintStream();
            }

            return result;
        }

        @Override
        public void print(final boolean b) {

            this.getPrintStream().print(b);
        }

        @Override
        public void print(final char c) {

            this.getPrintStream().print(c);
        }

        @Override
        public void print(final char[] s) {

            this.getPrintStream().print(s);
        }

        @Override
        public void print(final double d) {

            this.getPrintStream().print(d);
        }

        @Override
        public void print(final float f) {

            this.getPrintStream().print(f);
        }

        @Override
        public void print(final int i) {

            this.getPrintStream().print(i);
        }

        @Override
        public void print(final long l) {

            this.getPrintStream().print(l);
        }

        @Override
        public void print(final Object obj) {

            this.getPrintStream().print(obj);
        }

        @Override
        public void print(final String s) {

            this.getPrintStream().print(s);
        }

        @Override
        public PrintStream printf(final Locale l, final String format,
                                  final Object... args) {

            return this.getPrintStream().printf(l, format, args);
        }

        @Override
        public PrintStream printf(final String format, final Object... args) {

            return this.getPrintStream().printf(format, args);
        }

        @Override
        public void println() {

            this.getPrintStream().println();
        }

        @Override
        public void println(final boolean x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void println(final char x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void println(final char[] x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void println(final double x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void println(final float x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void println(final int x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void println(final long x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void println(final Object x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void println(final String x) {

            this.getPrintStream().println(x);
        }

        @Override
        public void write(final byte[] arg0) throws IOException {

            this.getPrintStream().write(arg0);
        }

        @Override
        public void write(final byte[] buf, final int off, final int len) {

            this.getPrintStream().write(buf, off, len);
        }

        @Override
        public void write(final int b) {

            this.getPrintStream().write(b);
        }
    }

    /**
     * Default job executors.
     */
    public static final int DEFAULT_JOB_EXECUTOR_COUNT = 10;

    /**
     * Logger.
     */
    private static final Logger LOGGER;

    /**
     * Executor factories.
     */
    private static Map<JobExecutorType, BasicJobManager.JobExecutorFactory> EXECUTOR_FACTORIES;

    /**
     * Executor objects.
     */
    private static Map<String, Object> EXECUTOR_OBJECTS;

    /**
     * Default executor factory.
     */
    private static BasicJobManager.JobExecutorFactory DEFAULT_EXECUTOR_FACTORY;

    /**
     * JobContentData executors.
     */
    private Map<JobExecutorData, JobExecutor> executors;

    /**
     * Output streams.
     */
    private Map<Thread, BasicJobManager.OutputStreamEntry> outputStreams;

    static {

        LOGGER = Logger.getLogger(BasicJobManager.class.getName());

        final Map<JobExecutorType, BasicJobManager.JobExecutorFactory> executorFactories =
                new EnumMap<>(JobExecutorType.class);
        /**
         * TODO - 01/19/15 - MJK - Finish JPY executor some day :P
         *
         * executorFactories.put(JobExecutorType.python_jpy, new
         * JobExecutorFactory() {
         *
         * @Override public JobExecutor createJobExecutor( final JobManager
         *           jobManager, final JobExecutorData executorData) {
         *
         *           return new JpyJobExecutor(jobManager, executorData); } });
         */

        BasicJobManager.EXECUTOR_FACTORIES = Collections
                .unmodifiableMap(executorFactories);

        BasicJobManager.DEFAULT_EXECUTOR_FACTORY = new BasicJobManager.JobExecutorFactory() {

            @Override
            public JobExecutor createJobExecutor(final JobManager jobManager,
                                                 final JobExecutorData executorData) {

                return new ScriptEngineJobExecutor(jobManager, executorData);
            }
        };

        final Map<String, Object> executorObjects = new HashMap<>();
        ExecutorObjectUtils.getExecutorObjects(executorObjects, false);

        BasicJobManager.EXECUTOR_OBJECTS = Collections
                .unmodifiableMap(executorObjects);
    }

    /**
     * Basic ctor.
     */
    public BasicJobManager() {

        this.init();
    }

    @Override
    public JobExecutor addExecutorInstance(final JobExecutorData executorData) {

        JobExecutorUtils.checkJobExecutorData(executorData, false);
        BasicJobManager.JobExecutorFactory executorFactory = BasicJobManager.EXECUTOR_FACTORIES
                .get(executorData.getType());

        if (executorFactory == null) {

            executorFactory = BasicJobManager.DEFAULT_EXECUTOR_FACTORY;
        }

        final JobExecutor executor = executorFactory.createJobExecutor(this,
                executorData);

        executor.startUp();
        this.executors.put(executorData, executor);

        return executor;
    }

    /**
     * Build default executors.
     */
    private void buildDefaultExecutors() {

        // set utility properties
        System.setProperty("python.console.encoding", "UTF-8");
        Options.importSite = false;
        Options.respectJavaAccessibility = false;

        // add executors
        for (final JobExecutorType item : JobExecutorType.values()) {

            this.addExecutorInstance(new JobExecutorData("Default", item));
        }
    }

    @Override
    public boolean deRegisterThread() {

        boolean result = false;

        synchronized (this.outputStreams) {

            final BasicJobManager.OutputStreamEntry outputEntry = this.outputStreams
                    .remove(Thread.currentThread());

            if (outputEntry != null) {

                try {

                    outputEntry.close();

                } catch (final Throwable ignore) {

                    // ignore;
                }

                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean disableThreadOutput() {

        BasicJobManager.OutputStreamEntry outputEntry = null;

        synchronized (this.outputStreams) {

            outputEntry = this.outputStreams.get(Thread.currentThread());
        }

        boolean result = false;

        if (outputEntry != null) {

            result = outputEntry.isEnabled();
            outputEntry.setEnabled(false);
        }

        return result;
    }

    @Override
    public boolean enableThreadOutput() {

        BasicJobManager.OutputStreamEntry outputEntry = null;

        synchronized (this.outputStreams) {

            outputEntry = this.outputStreams.get(Thread.currentThread());
        }

        boolean result = false;

        if (outputEntry != null) {

            result = !outputEntry.isEnabled();
            outputEntry.setEnabled(true);
        }

        return result;
    }

    @Override
    public boolean getAllCompletedJobs(final long timeInMS,
                                       final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        boolean result = false;

        for (final JobExecutor item : this.executors.values()) {

            if (item.getCompletedJobsFromTime(timeInMS, target, false)) {

                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean getAllExecutorData(final Collection<JobExecutorData> target,
                                      final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        return target.addAll(this.executors.keySet());
    }

    @Override
    public boolean getAllExecutorInstances(
            final Collection<JobExecutor> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        return target.addAll(this.executors.values());
    }

    @Override
    public boolean getAllJobs(final long timeInMS,
                              final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        boolean result = false;

        if (this.getAllPendingJobs(timeInMS, target, false)) {

            result = true;
        }

        if (this.getAllCompletedJobs(timeInMS, target, false)) {

            result = true;
        }

        return result;
    }

    @Override
    public boolean getAllPendingJobs(final long timeInMS,
                                     final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        boolean result = false;

        for (final JobExecutor item : this.executors.values()) {

            if (item.getPendingJobsFromTime(timeInMS, target, false)) {

                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean getExecutorCompletedJobs(final JobExecutorData executorData,
                                            final long timeInMS, final Collection<JobContentData> target,
                                            final boolean isClearFirst) {

        final JobExecutor executor = this.getExecutorInstance(executorData);
        JobDataUtils.checkNullObject(executor, true);

        return executor
                .getCompletedJobsFromTime(timeInMS, target, isClearFirst);
    }

    @Override
    public JobExecutor getExecutorInstance(final JobExecutorData executorData) {

        JobExecutorUtils.checkJobExecutorData(executorData, true);
        return this.executors.get(executorData);
    }

    @Override
    public boolean getExecutorObjects(final Map<String, Object> target,
                                      final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        final boolean wasEmpty = target.isEmpty();
        target.putAll(BasicJobManager.EXECUTOR_OBJECTS);

        return wasEmpty != target.isEmpty();
    }

    @Override
    public boolean getExecutorPendingJobs(final JobExecutorData executorData,
                                          final long timeInMS, final Collection<JobContentData> target,
                                          final boolean isClearFirst) {

        final JobExecutor executor = this.getExecutorInstance(executorData);
        JobDataUtils.checkNullObject(executor, true);

        return executor.getPendingJobsFromTime(timeInMS, target, isClearFirst);
    }

    @Override
    public boolean getExecutorVariables(final JobExecutorData executorData,
                                        final int variableScope, final int variableFormat,
                                        final Map<String, String> target, final boolean isClearFirst) {

        final JobExecutor executor = this.getExecutorInstance(executorData);
        JobDataUtils.checkNullObject(executor, true);

        return executor.getVariables(variableScope, variableFormat,
                target, isClearFirst);
    }

    /**
     * One-off init.
     */
    private void init() {

        this.executors = new ConcurrentHashMap<>();
        this.outputStreams = new IdentityHashMap<>();

        System.setOut(new OutputStreamHandler(System.out, this.outputStreams));
        System.setErr(new OutputStreamHandler(System.err, this.outputStreams));

        this.buildDefaultExecutors();
    }

    @Override
    public String readThreadOutput() {
        BasicJobManager.OutputStreamEntry outputEntry = null;

        synchronized (this.outputStreams) {

            outputEntry = this.outputStreams.get(Thread.currentThread());
        }

        String result = null;

        if (outputEntry != null) {

            result = outputEntry.readOutput();
        }

        return result;
    }

    @Override
    public boolean registerThread() {

        boolean result = false;

        synchronized (this.outputStreams) {

            final BasicJobManager.OutputStreamEntry outputEntry = this.outputStreams.get(Thread
                    .currentThread());

            if (outputEntry == null) {

                this.outputStreams.put(Thread.currentThread(),
                        new BasicJobManager.OutputStreamEntry());
                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean removeAllExecutorInstances() {

        final List<JobExecutorData> toRemove = new ArrayList<>(
                this.executors.keySet());
        final boolean result = !toRemove.isEmpty();

        for (final JobExecutorData item : toRemove) {

            this.removeExecutorInstance(item);
        }

        return result;
    }

    @Override
    public JobExecutor removeExecutorInstance(final JobExecutorData executorData) {

        JobExecutorUtils.checkJobExecutorData(executorData, true);
        final JobExecutor result = this.executors.remove(executorData);

        if (result != null) {

            result.cleanUp();
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobManager#pushPendingJob(com.opsysinc
     * .oe.r.test1.shared.JobStateData)
     */
    @Override
    public JobContentData submitJob(final JobContentData contentData) {

        JobContentUtils.checkJobStateData(contentData, false);

        final JobExecutorData executorData = contentData.getExecutorData();
        JobExecutorUtils.checkJobExecutorData(executorData, true);

        final JobExecutor executor = this.getExecutorInstance(executorData);

        if (executor == null) {

            throw new IllegalArgumentException("missing executor");

        } else {

            executor.submitJob(contentData);
        }

        return contentData;
    }
}
