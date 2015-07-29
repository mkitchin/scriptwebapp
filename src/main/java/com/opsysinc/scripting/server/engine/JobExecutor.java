package com.opsysinc.scripting.server.engine;

import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobExecutorData;

import java.util.Collection;
import java.util.Map;

/**
 * Job executor interface.
 *
 * @author mkitchin
 */
public interface JobExecutor {

    /**
     * Peek pending job.
     *
     * @param isToBlock True to block, false otherwise.
     * @return Returns pending job if found, null otherwise.
     * @throws InterruptedException Interrupted exception.
     */
    JobContentData checkNextPendingJob(boolean isToBlock)
            throws InterruptedException;

    /**
     * Clean up executor.
     */
    void cleanUp();

    /**
     * Push completed job.
     *
     * @param completedJob Completed job.
     */
    void completeJob(JobContentData completedJob);

    /**
     * Gets completed jobs.
     *
     * @return Completed jobs.
     */
    boolean getAllCompletedJobs(Collection<JobContentData> target,
                                boolean isClearFirst);

    /**
     * Gets pending jobs.
     *
     * @return Pending jobs.
     */
    boolean getAllPendingJobs(Collection<JobContentData> target,
                              boolean isClearFirst);

    /**
     * Gets completed jobs from last job id.
     *
     * @param lastJobId    Last job id.
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection before adding, false
     *                     otherwise.
     * @return True if target collection modified, false otherwise.
     */
    boolean getCompletedJobsFromTime(long lastJobId,
                                     Collection<JobContentData> target, boolean isClearFirst);

    /**
     * Gets job executor data.
     *
     * @return Job executor data.
     */
    JobExecutorData getExecutorData();

    /**
     * Get job manager.
     *
     * @return the jobManager
     */
    JobManager getJobManager();

    /**
     * Gets pending jobs from last job id.
     *
     * @param lastJobId    Last job id.
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection before adding, false
     *                     otherwise.
     * @return True if target collection modified, false otherwise.
     */
    boolean getPendingJobsFromTime(long lastJobId,
                                   Collection<JobContentData> target, boolean isClearFirst);

    /**
     * Gets variables.
     *
     * @param variableScope Variable scope.
     * @param target        Target map.
     * @param isClearFirst  True to clear target map before populating, false otherwise.
     * @return True if target map was altered, false otherwise.
     */
    boolean getVariables(int variableScope, Map<String, String> target,
                         boolean isClearFirst);

    /**
     * Clear completed jobs.
     *
     * @return Completed jobs (non-null).
     */
    boolean removeAllCompletedJobs();

    /**
     * Clear pending jobs.
     *
     * @return Pending jobs (non-null).
     */
    boolean removeAllPendingJobs();

    /**
     * Poll completed job.
     *
     * @param isToBlock True to block, false otherwise.
     * @return Returns completed job if found, null otherwise.
     * @throws InterruptedException Interrupted exception.
     */
    JobContentData removeNextCompletedJob(boolean isToBlock)
            throws InterruptedException;

    /**
     * Poll pending job.
     *
     * @param isToBlock True to block, false otherwise.
     * @return Returns pending job if found, null otherwise.
     * @throws InterruptedException Interrupted exception.
     */
    JobContentData removeNextPendingJob(boolean isToBlock)
            throws InterruptedException;

    /**
     * Sets job executor data.
     *
     * @return Job executor data.
     */
    void setExecutorData(JobExecutorData data);

    /**
     * Start up executor.
     */
    void startUp();

    /**
     * Push pending job.
     *
     * @param pendingJob Pending job.
     */
    void submitJob(JobContentData pendingJob);
}