package com.opsysinc.scripting.server.servlet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.opsysinc.scripting.client.service.ScriptService;
import com.opsysinc.scripting.server.engine.BasicJobManager;
import com.opsysinc.scripting.server.engine.JobContentUtils;
import com.opsysinc.scripting.server.engine.JobExecutor;
import com.opsysinc.scripting.server.engine.JobManager;
import com.opsysinc.scripting.server.util.ThreadLocalKey;
import com.opsysinc.scripting.server.util.ThreadLocalMap;
import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobExecutorData;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The server-side implementation of the RPC service.
 */
public class ScriptServiceImpl extends RemoteServiceServlet implements ScriptService {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Job manager.
     */
    private JobManager jobManager;

    /**
     * Basic ctor.
     */
    public ScriptServiceImpl() {

        this.initService();
    }

    @Override
    public JobExecutorData addExecutor(final JobExecutorData executorData) {

        this.checkThreadLocals();
        final JobExecutor executor = this.jobManager
                .addExecutorInstance(executorData);

        if (executor == null) {

            return null;

        } else {

            return executor.getExecutorData();
        }
    }

    /**
     * Check/populate thread local values.
     */
    private void checkThreadLocals() {

        final Map<ThreadLocalKey, Object> threadMap = ThreadLocalMap
                .getInstance().get();

        if (threadMap.isEmpty()) {

            threadMap.put(ThreadLocalKey.install_path, this.getServletContext()
                    .getRealPath("/"));
        }
    }

    @Override
    public JobContentData[] getAllCompletedJobs(final long timeInMS) {

        this.checkThreadLocals();

        final Set<JobContentData> contentData = new LinkedHashSet<>();
        this.jobManager.getAllCompletedJobs(timeInMS, contentData, false);

        return contentData.toArray(new JobContentData[contentData.size()]);
    }

    @Override
    public JobExecutorData[] getAllExecutors() {

        this.checkThreadLocals();

        final Set<JobExecutorData> executorData = new LinkedHashSet<>();
        this.jobManager.getAllExecutorData(executorData, false);

        return executorData.toArray(new JobExecutorData[executorData.size()]);
    }

    @Override
    public JobContentData[] getAllJobs(final long timeInMS) {

        this.checkThreadLocals();

        final Set<JobContentData> contentData = new LinkedHashSet<>();
        this.jobManager.getAllJobs(timeInMS, contentData, false);

        return contentData.toArray(new JobContentData[contentData.size()]);
    }

    @Override
    public JobContentData[] getAllPendingJobs(final long timeInMS) {

        this.checkThreadLocals();

        final Set<JobContentData> contentData = new LinkedHashSet<>();
        this.jobManager.getAllPendingJobs(timeInMS, contentData, false);

        return contentData.toArray(new JobContentData[contentData.size()]);
    }

    @Override
    public JobExecutorData getExecutor(final JobExecutorData executorData) {

        this.checkThreadLocals();
        final JobExecutor executor = this.jobManager
                .getExecutorInstance(executorData);

        if (executor == null) {

            return null;

        } else {

            return executor.getExecutorData();
        }
    }

    @Override
    public JobContentData[] getExecutorCompletedJobs(
            final JobExecutorData executorData, final long timeInMS) {

        this.checkThreadLocals();

        final Set<JobContentData> contentData = new LinkedHashSet<>();
        this.jobManager.getExecutorCompletedJobs(executorData, timeInMS,
                contentData, false);

        return contentData.toArray(new JobContentData[contentData.size()]);
    }

    @Override
    public JobContentData[] getExecutorPendingJobs(
            final JobExecutorData executorData, final long timeInMS) {

        this.checkThreadLocals();

        final Set<JobContentData> contentData = new LinkedHashSet<>();
        this.jobManager.getExecutorPendingJobs(executorData, timeInMS,
                contentData, false);

        return contentData.toArray(new JobContentData[contentData.size()]);
    }

    @Override
    public String[][] getExecutorVariables(final JobExecutorData executorData,
                                           final int variableScope, final int variableFormat) {

        this.checkThreadLocals();

        final Map<String, String> variables = new TreeMap<>();
        this.jobManager.getExecutorVariables(executorData,
                variableScope, variableFormat,
                variables, false);

        final String[][] result = new String[variables.size()][2];
        int ctr = 0;

        for (final Map.Entry<String, String> item : variables.entrySet()) {

            result[ctr][0] = item.getKey();
            result[ctr][1] = item.getValue();

            ctr++;
        }

        return result;
    }

    /**
     * Single-use init method.
     */
    private void initService() {

        this.jobManager = new BasicJobManager();
    }

    @Override
    public boolean removeAllExecutors() {

        this.checkThreadLocals();
        return this.jobManager.removeAllExecutorInstances();
    }

    @Override
    public JobExecutorData removeExecutor(
            final JobExecutorData executorData) {

        this.checkThreadLocals();
        final JobExecutor executor = this.jobManager
                .removeExecutorInstance(executorData);

        if (executor == null) {

            return null;

        } else {

            return executor.getExecutorData();
        }
    }

    /**
     * Sets job host data.
     *
     * @param contentData Content data.
     */
    private void setJobHostData(final JobContentData contentData) {

        JobContentUtils.checkJobStateData(contentData, false);

        contentData.getRequestData().setFromHost(
                this.getThreadLocalRequest().getRemoteAddr());
        contentData.getRequestData().setToHost(
                this.getThreadLocalRequest().getRemoteAddr());
    }

    @Override
    public JobContentData submitJob(final JobContentData contentData) {

        this.checkThreadLocals();
        this.setJobHostData(contentData);

        return this.jobManager.submitJob(contentData);
    }
}
