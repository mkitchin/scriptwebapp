package com.opsysinc.scripting.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobExecutorData;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("script")
public interface ScriptService extends RemoteService {

    /**
     * Add executor.
     *
     * @param executorData Executor data.
     * @return Executor state.
     */
    JobExecutorData addExecutor(JobExecutorData executorData);

    /**
     * Gets completed jobs from time.
     *
     * @param timeInMS Job modified time to select (0 = all).
     * @return Completed jobs from time.
     */
    JobContentData[] getAllCompletedJobs(long timeInMS);

    /**
     * Get all executors
     *
     * @return All executor data.
     */
    JobExecutorData[] getAllExecutors();

    /**
     * Gets jobs from time.
     *
     * @param timeInMS Job modified time to select (0 = all).
     * @return Completed jobs from time.
     */
    JobContentData[] getAllJobs(long timeInMS);

    /**
     * Gets pending jobs from time for all executors.
     *
     * @param timeInMS Job modified time to select (0 = all).
     * @return Pending jobs from time.
     */
    JobContentData[] getAllPendingJobs(long timeInMS);

    /**
     * Get executor.
     *
     * @param executorData Executor data.
     * @return Executor data if found, null otherwise.
     */
    JobExecutorData getExecutor(JobExecutorData executorData);

    /**
     * Gets completed jobs from time for specific executor.
     *
     * @param executorData Executor for completed jobs.
     * @param timeInMS     Job modified time to select (0 = all).
     * @return Completed jobs from time.
     */
    JobContentData[] getExecutorCompletedJobs(JobExecutorData executorData,
                                              long timeInMS);

    /**
     * Gets pending jobs from time for specific executor.
     *
     * @param executorData Executor for pending jobs.
     * @param timeInMS     Job modified time to select (0 = all).
     * @return Pending jobs from time.
     */
    JobContentData[] getExecutorPendingJobs(JobExecutorData executorData,
                                            long timeInMS);

    /**
     * Gets executor variables.
     *
     * @param executorData  Executor data.
     * @param variableScope Variable scope.
     * @return Variable keys/values.
     */
    String[][] getExecutorVariables(JobExecutorData executorData,
                                    int variableScope, int variableFormat);

    /**
     * Remove all executors.
     *
     * @return True if set modified, false otherwise.
     */
    boolean removeAllExecutors();

    /**
     * Remove executor.
     *
     * @param executorData Executor data.
     * @return Executor data if found, null otherwise.
     */
    JobExecutorData removeExecutor(JobExecutorData executorData);

    /**
     * Submit job.
     *
     * @param pendingJob job.
     * @return Job state modified with assigned executor.
     */
    JobContentData submitJob(JobContentData pendingJob);
}
