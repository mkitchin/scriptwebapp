package com.opsysinc.scripting.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobExecutorData;

/**
 * The async counterpart of <code>ScriptService</code>.
 */
public interface ScriptServiceAsync {

    void addExecutor(JobExecutorData executorData,
                     AsyncCallback<JobExecutorData> callback);

    void getAllCompletedJobs(long timeInMS,
                             AsyncCallback<JobContentData[]> callback);

    void getAllExecutors(AsyncCallback<JobExecutorData[]> callback);

    void getAllJobs(long timeInMS, AsyncCallback<JobContentData[]> callback);

    void getAllPendingJobs(long timeInMS,
                           AsyncCallback<JobContentData[]> callback);

    void getExecutor(JobExecutorData executorData,
                     AsyncCallback<JobExecutorData> callback);

    void getExecutorCompletedJobs(JobExecutorData executorData, long timeInMS,
                                  AsyncCallback<JobContentData[]> callback);

    void getExecutorPendingJobs(JobExecutorData executorData, long timeInMS,
                                AsyncCallback<JobContentData[]> callback);

    void getExecutorVariables(JobExecutorData executorData, int variableScope,
                              AsyncCallback<String[][]> callback);

    void removeAllExecutors(AsyncCallback<Boolean> callback);

    void removeExecutor(JobExecutorData executorData,
                        AsyncCallback<JobExecutorData> callback);

    void submitJob(JobContentData job, AsyncCallback<JobContentData> callback);
}
