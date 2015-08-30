package com.opsysinc.scripting.server.engine;

import com.opsysinc.scripting.server.util.ThreadLocalKey;
import com.opsysinc.scripting.server.util.ThreadLocalMap;
import com.opsysinc.scripting.shared.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AbstractJobExecutor class.
 *
 * @author mkitchin
 */
public abstract class AbstractJobExecutor implements JobExecutor {

    /**
     * Default thread join wait in ms.
     */
    public static final long DEFAULT_THREAD_JOIN_WAIT_MS = 5000L;

    /**
     * Default job wait in ms.
     */
    public static final long DEFAULT_JOB_WAIT_MS = 500L;

    /**
     * Default none text.
     */
    public static final String DEFAULT_NONE_TEXT = "<none>";

    /**
     * Default error prefix text.
     */
    public static final String DEFAULT_ERROR_PREFIX_TEXT = "Error: ";

    /**
     * Default thread join wait in ms.
     */
    public static final int DEFAULT_MAX_COMPLETED_JOBS = 20;

    /**
     * Default base folder for files.
     */
    public static final File DEFAULT_FILE_BASEDIR;

    /**
     * Logger.
     */
    private static final Logger LOGGER;

    /**
     * Job manager.
     */
    private final JobManager jobManager;

    /**
     * Worker.
     */
    private Thread worker;

    /**
     * Pending jobs.
     */
    private BlockingDeque<JobContentData> pendingJobs;

    /**
     * Completed jobs.
     */
    private BlockingDeque<JobContentData> completedJobs;

    /**
     * Job executor data.
     */
    private JobExecutorData executorData;

    /**
     * Thread local map on creation (should be obtained with every submit
     * but...no).
     */
    private Map<ThreadLocalKey, Object> threadMap;

    /**
     * File base folder.
     */
    private File fileBase;

    static {

        LOGGER = Logger.getLogger(AbstractJobExecutor.class.getName());
        DEFAULT_FILE_BASEDIR = new File(FileUtils.getTempDirectoryPath(), "scriptwebapp");
    }

    /**
     * Executor runnable.
     */
    private final Runnable workerRunnable = new Runnable() {

        @Override
        public void run() {

            AbstractJobExecutor.LOGGER.log(Level.INFO, "Executor started ("
                    + Thread.currentThread().getName() + "; "
                    + AbstractJobExecutor.this.executorData + ").");
            AbstractJobExecutor.this.jobManager.registerThread();

            try {

                while (!Thread.interrupted()) {

                    try {

                        AbstractJobExecutor.this.runWork();

                    } catch (final InterruptedException ex) {

                        AbstractJobExecutor.LOGGER.log(Level.FINE, "Exiting",
                                ex);
                        break;

                    } catch (final Throwable ex) {

                        AbstractJobExecutor.LOGGER.log(Level.WARNING,
                                "Can't execute", ex);
                    }
                }

            } catch (final Throwable ex) {

                AbstractJobExecutor.LOGGER.log(Level.WARNING,
                        "Executor error (" + Thread.currentThread().getName()
                                + "; " + AbstractJobExecutor.this.executorData
                                + ").", ex);

            } finally {

                AbstractJobExecutor.this.jobManager.deRegisterThread();

                AbstractJobExecutor.LOGGER.log(Level.INFO, "Executor stopped ("
                        + Thread.currentThread().getName() + "; "
                        + AbstractJobExecutor.this.executorData + ").");
            }
        }

    };

    /**
     * Basic ctor.
     */
    protected AbstractJobExecutor(final JobManager jobManager,
                                  final JobExecutorData executorData) {

        this.jobManager = jobManager;
        this.setExecutorData(executorData, false);

        this.init();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobManager#pollPendingJob(boolean)
     */
    @Override
    public JobContentData checkNextPendingJob(final boolean isToBlock)
            throws InterruptedException {

        JobContentData pendingJob = this.pendingJobs.peek();

        while (isToBlock && (pendingJob == null)) {

            Thread.sleep(AbstractJobExecutor.DEFAULT_JOB_WAIT_MS);
            pendingJob = this.pendingJobs.peek();
        }

        return pendingJob;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobExecutor#cleanUpExecutor()
     */
    @Override
    public synchronized void cleanUp() {

        if (this.worker == null) {

            // ignore, return
            return;
        }

        try {

            this.worker.interrupt();
            this.worker.join(AbstractJobExecutor.DEFAULT_THREAD_JOIN_WAIT_MS);

            this.cleanUpImpl();
            this.cleanUpFiles();

        } catch (final Throwable ex) {

            AbstractJobExecutor.LOGGER.log(Level.FINE, "Can't clean up", ex);

        } finally {

            this.worker = null;
        }
    }

    /**
     * Clean up files.
     *
     * @throws IOException I/O exception.
     */
    private void cleanUpFiles() throws IOException {

        FileUtils.deleteDirectory(this.fileBase);
        AbstractJobExecutor.LOGGER.log(Level.INFO, "Executor folder removed: " +
                this.fileBase.getAbsolutePath());
    }

    /**
     * Start up implementation.
     *
     * @throws Throwable Any exception (caller handles).
     */
    protected abstract void cleanUpImpl() throws Throwable;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.opsysinc.scripting.engine.JobManager#pushCompleteJob(com.opsysinc
     * .oe.r.test1.shared.JobStateData)
     */
    @Override
    public void completeJob(final JobContentData completedJob) {

        JobContentUtils.checkJobStateData(completedJob, false);

        synchronized (this.completedJobs) {

            this.completedJobs.addLast(completedJob);

            while (this.completedJobs.size() > AbstractJobExecutor.DEFAULT_MAX_COMPLETED_JOBS) {

                this.completedJobs.removeFirst();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobManager#getCompletedJobs(java.util.
     * Collection , boolean)
     */
    @Override
    public boolean getAllCompletedJobs(
            final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        return target.addAll(this.completedJobs);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.opsysinc.scripting.engine.JobManager#getPendingJobs(java.util.Collection
     * , boolean)
     */
    @Override
    public boolean getAllPendingJobs(
            final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        return target.addAll(this.pendingJobs);
    }

    @Override
    public boolean getCompletedJobsFromTime(final long timeInMS,
                                            final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        boolean result = false;

        for (final JobContentData item : this.completedJobs) {

            if (item.getModifiedTime() > timeInMS) {

                if (target.add(item)) {

                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Gets executor data.
     *
     * @return Executor data.
     */
    @Override
    public JobExecutorData getExecutorData() {

        return this.executorData;
    }

    @Override
    public JobManager getJobManager() {

        return this.jobManager;
    }

    @Override
    public boolean getPendingJobsFromTime(final long timeInMS,
                                          final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);
        boolean result = false;

        if (isClearFirst) {

            if (result = !target.isEmpty()) {

                target.clear();
            }
        }

        for (final JobContentData item : this.pendingJobs) {

            if (item.getModifiedTime() > timeInMS) {

                if (target.add(item)) {

                    result = true;
                }
            }
        }

        return result;
    }

    @Override
    public boolean getVariables(final int variableScope, final int variableFormat,
                                final Map<String, String> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);
        boolean result = false;

        if (isClearFirst) {

            if (result = !target.isEmpty()) {

                target.clear();
            }
        }

        if (this.getVariablesImpl(variableScope, variableFormat, target, false)) {

            result = true;
        }

        return result;
    }

    /**
     * Get variables implementation.
     *
     * @param variableScope  Variable scope.
     * @param variableFormat Variable format.
     * @param target         Target collection.
     * @param isClearFirst   True to clear map before adding, false otherwise.
     * @return True if target was modified, false otherwise.
     */
    protected abstract boolean getVariablesImpl(final int variableScope, final int variableFormat,
                                                final Map<String, String> target, final boolean isClearFirst);

    @Override
    public boolean getFiles(final String basePath,
                            final List<JobFileData> target,
                            final boolean isClearFirst) {

        JobFileUtils.checkBadFilePath(basePath, true);
        JobDataUtils.checkNullObject(target, true);

        boolean result = false;

        if (isClearFirst) {

            if (result = !target.isEmpty()) {

                target.clear();
            }
        }

        final File searchPath = new File(this.fileBase.getAbsolutePath(), basePath);

        for (final File item : FileUtils.listFiles(searchPath, null, false)) {

            final JobFileData fileData = new JobFileData(this.executorData,
                    basePath, item.getName(),
                    (item.isFile() ? JobFileType.file : JobFileType.folder),
                    (item.isFile() ? item.length() : -1L));

            fileData.setCreatedTime(item.lastModified());
            fileData.setModifiedTime(item.lastModified());

            if (target.add(fileData)) {

                result = true;
            }
        }

        return result;
    }

    /**
     * One-shot init.
     */
    private void init() {

        this.pendingJobs = new LinkedBlockingDeque<>();
        this.completedJobs = new LinkedBlockingDeque<>();
        this.threadMap = Collections.unmodifiableMap(ThreadLocalMap
                .getInstance().get());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobManager#clearCompletedJobs()
     */
    @Override
    public boolean removeAllCompletedJobs() {

        final boolean result = !this.completedJobs.isEmpty();
        this.completedJobs.clear();

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobManager#clearPendingJobs()
     */
    @Override
    public boolean removeAllPendingJobs() {

        final boolean result = !this.pendingJobs.isEmpty();
        this.pendingJobs.clear();

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobManager#pollCompletedJob(boolean)
     */
    @Override
    public JobContentData removeNextCompletedJob(final boolean isToBlock)
            throws InterruptedException {

        final JobContentData completedJob;

        if (isToBlock) {

            completedJob = this.completedJobs.takeFirst();

        } else {

            completedJob = this.completedJobs.pollFirst();
        }

        return completedJob;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobManager#pollPendingJob(boolean)
     */
    @Override
    public JobContentData removeNextPendingJob(final boolean isToBlock)
            throws InterruptedException {

        final JobContentData pendingJob;

        if (isToBlock) {

            pendingJob = this.pendingJobs.takeFirst();

        } else {

            pendingJob = this.pendingJobs.pollFirst();
        }

        return pendingJob;
    }

    /**
     * Build script engine.
     */
    protected void resetScriptEngine() {

        this.removeAllPendingJobs();
        this.removeAllCompletedJobs();

        this.resetScriptEngineImpl();
    }

    /**
     * Reset script engine implementation.
     */
    protected abstract void resetScriptEngineImpl();

    /**
     * Main work method.
     *
     * @throws Throwable Any exception.
     */
    protected void runWork() throws Throwable {

        final JobContentData contentData = this.checkNextPendingJob(true);

        if (contentData == null) {

            return;
        }

        Throwable lastEx = null;

        try {

            ThreadLocalMap.getInstance().set(this.threadMap);
            contentData.setState(JobState.started);

            AbstractJobExecutor.LOGGER.log(Level.INFO, "Job started ("
                    + this.executorData + "; " + contentData + ").");

            final String requestBody = contentData.getRequestData()
                    .getRequestBody();

            JobDataUtils.checkEmptyString(requestBody, true);

            this.jobManager.enableThreadOutput();
            final Object resultObject = this.runWorkImpl(requestBody);

            if (!JobDataUtils.checkEmptyString(resultObject, false)) {

                contentData.setResponseData(new JobResponseData(resultObject
                        .toString()));
            }

            contentData.setStatus(JobStatus.success);

        } catch (final Throwable ex) {

            final String stackTrace;

            try (StringWriter stringWriter = new StringWriter()) {

                try (PrintWriter printWriter = new PrintWriter(stringWriter)) {

                    ex.printStackTrace(printWriter);

                    printWriter.flush();
                    stackTrace = stringWriter.toString();
                }
            }

            contentData.setStatus(JobStatus.failure);
            contentData
                    .setResponseData(new JobResponseData(
                            AbstractJobExecutor.DEFAULT_ERROR_PREFIX_TEXT
                                    + stackTrace));

            lastEx = ex;

        } finally {

            this.jobManager.disableThreadOutput();
            final String outputText = this.jobManager.readThreadOutput();

            if (!JobDataUtils.checkEmptyString(outputText, false)) {

                contentData.setOutputData(new JobOutputData(outputText));
            }

            if (lastEx == null) {

                AbstractJobExecutor.LOGGER.log(Level.INFO, "Job succeeded ("
                        + this.executorData + "; " + contentData + ").");

            } else {

                AbstractJobExecutor.LOGGER
                        .log(Level.INFO, "Job failed (" + this.executorData
                                + "; " + contentData + ").", lastEx);
            }

            contentData.setState(JobState.completed);

            this.completeJob(contentData);
            this.removeNextPendingJob(false);
        }
    }

    /**
     * Run method.
     *
     * @param inputText Input text.
     * @return Result object (may be null).
     * @throws Throwable Any exeception (caller handles :P ).
     */
    protected abstract Object runWorkImpl(String inputText) throws Throwable;

    /**
     * Sets executor data and resets script engine.
     *
     * @param executorData Executor data.
     */
    @Override
    public void setExecutorData(final JobExecutorData executorData) {

        this.setExecutorData(executorData, true);
    }

    /**
     * Sets executor data.
     *
     * @param executorData Executor data.
     * @param isToReset    True to reset script engine, false otherwise.
     */
    private void setExecutorData(final JobExecutorData executorData,
                                 final boolean isToReset) {

        JobExecutorUtils.checkJobExecutorData(executorData, true);
        this.executorData = executorData;

        if (isToReset) {

            this.resetScriptEngine();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobExecutor#startUpExecutor()
     */
    @Override
    public synchronized void startUp() {

        if (this.worker != null) {

            // ignore, return
            return;
        }

        try {

            this.startUpFiles();
            this.startUpImpl();
            this.resetScriptEngine();

            final Thread newWorker = new Thread(this.workerRunnable);

            newWorker.setDaemon(true);
            newWorker.start();

            this.worker = newWorker;

        } catch (final Throwable ex) {

            AbstractJobExecutor.LOGGER.log(Level.WARNING, "Can't start up", ex);
        }
    }

    /**
     * Start up file area.
     *
     * @throws IOException I/O exception.
     */
    private void startUpFiles() throws IOException {

        this.fileBase = new File(AbstractJobExecutor.DEFAULT_FILE_BASEDIR,
                this.getExecutorData().getId());
        FileUtils.forceMkdir(this.fileBase);

        AbstractJobExecutor.LOGGER.log(Level.INFO, "Executor folder added: " +
                this.fileBase.getAbsolutePath());
    }

    /**
     * Start up implementation.
     *
     * @throws Throwable Any exception (caller handles).
     */
    protected abstract void startUpImpl() throws Throwable;

    /*
     * (non-Javadoc)
     *
     * @see com.opsysinc.scripting.engine.JobManager#pushPendingJob(com.opsysinc
     * .oe.r.test1.shared.JobStateData)
     */
    @Override
    public void submitJob(final JobContentData pendingJob) {

        JobContentUtils.checkJobStateData(pendingJob, false);
        this.pendingJobs.addLast(pendingJob);
    }
}
