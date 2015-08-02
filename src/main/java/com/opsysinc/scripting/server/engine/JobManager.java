package com.opsysinc.scripting.server.engine;

import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobExecutorData;

import java.util.Collection;
import java.util.Map;

public interface JobManager {

    /**
     * Add executor.
     *
     * @param executorData Executor data.
     * @return Executor instance.
     */
    JobExecutor addExecutorInstance(JobExecutorData executorData);

    /**
     * Register the current thread for thread-local output (to catch scripting
     * engine output).
     *
     * @return True if this thread was been registered before, false otherwise.
     */
    boolean deRegisterThread();

    /**
     * Disables thread output.
     *
     * @return True if status changed, false otherwise.
     */
    boolean disableThreadOutput();

    /**
     * Enables thread output.
     *
     * @return True if status changed, false otherwise.
     */
    boolean enableThreadOutput();

    /**
     * Gets completed jobs from last modified time.
     *
     * @param timeInMS     Job modified time to select (0 = all).
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection before adding, false
     *                     otherwise.
     * @return True if target collection modified, false otherwise.
     */
    boolean getAllCompletedJobs(long timeInMS,
                                Collection<JobContentData> target, boolean isClearFirst);

    /**
     * Get all executor data.
     *
     * @param target       Target collection.
     * @param isClearFirst True to clear collection first, false otherwise.
     * @return True if target collection added to, false otherwise.
     */
    boolean getAllExecutorData(Collection<JobExecutorData> target,
                               boolean isClearFirst);

    /**
     * Get all executor instances.
     *
     * @param target       Target collection.
     * @param isClearFirst True to clear collection first, false otherwise.
     */
    boolean getAllExecutorInstances(Collection<JobExecutor> target,
                                    boolean isClearFirst);

    /**
     * Gets jobs from last modified time.
     *
     * @param timeInMS     Job modified time to select (0 = all).
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection before adding, false
     *                     otherwise.
     * @return True if target collection modified, false otherwise.
     */
    boolean getAllJobs(long timeInMS, Collection<JobContentData> target,
                       boolean isClearFirst);

    /**
     * Gets pending jobs from last modified time.
     *
     * @param timeInMS     Job modified time to select (0 = all).
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection before adding, false
     *                     otherwise.
     * @return True if target collection modified, false otherwise.
     */
    boolean getAllPendingJobs(long timeInMS, Collection<JobContentData> target,
                              boolean isClearFirst);

    /**
     * Gets completed jobs from last modified time.
     *
     * @param executorData Executor for completed jobs.
     * @param timeInMS     Job modified time to select (0 = all).
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection before adding, false
     *                     otherwise.
     * @return True if target collection modified, false otherwise.
     */
    boolean getExecutorCompletedJobs(JobExecutorData executorData,
                                     long timeInMS, Collection<JobContentData> target,
                                     boolean isClearFirst);

    /**
     * Get executor instance.
     *
     * @param executorData Executor data.
     * @return Executor instance if found, null otherwise.
     */
    JobExecutor getExecutorInstance(JobExecutorData executorData);

    /**
     * Get exectuor objects.
     *
     * @param target       Target map.
     * @param isClearFirst True to clear target map first, false otherwise.
     * @return True of target map was modified, false otherwise.
     */
    boolean getExecutorObjects(Map<String, Object> target, boolean isClearFirst);

    /**
     * Gets pending jobs from last modified time.
     *
     * @param executorData Executor for pending jobs.
     * @param timeInMS     Job modified time to select (0 = all).
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection before adding, false
     *                     otherwise.
     * @return True if target collection modified, false otherwise.
     */
    boolean getExecutorPendingJobs(JobExecutorData executorData, long timeInMS,
                                   Collection<JobContentData> target, boolean isClearFirst);

    /**
     * Gets executor variables.
     *
     * @param executorData   Executor data.
     * @param variableScope  Variable scope.
     * @param variableFormat Variable format.
     * @param target         Target map.
     * @param isClearFirst   True to clear target map before populating, false otherwise.
     * @return True if target map was altered, false otherwise.
     */
    boolean getExecutorVariables(JobExecutorData executorData,
                                 int variableScope, int variableFormat,
                                 Map<String, String> target, boolean isClearFirst);

    /**
     * Fetch and clear stored thread output.
     *
     * @return Thread output if found, null otherwise.
     */
    String readThreadOutput();

    /**
     * Register the current thread for thread-local output (to catch scripting
     * engine output).
     *
     * @return True if this thread hasn't been registered before, false
     * otherwise.
     */
    boolean registerThread();

    /**
     * Remove all executors.
     *
     * @return True if set modified, false otherwise.
     */
    boolean removeAllExecutorInstances();

    /**
     * Remove executor.
     *
     * @param executorData Executor data.
     * @return Executor instance if found, null otherwise.
     */
    JobExecutor removeExecutorInstance(JobExecutorData executorData);

    /**
     * Submit job.
     *
     * @param contentData Pending job data.
     * @return Job state modified with assigned executor.
     */
    JobContentData submitJob(JobContentData contentData);

}