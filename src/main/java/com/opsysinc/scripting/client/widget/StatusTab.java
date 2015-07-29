package com.opsysinc.scripting.client.widget;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.opsysinc.scripting.client.app.ScriptWebApp;
import com.opsysinc.scripting.client.service.ScriptServiceAsync;
import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobDataUtils;
import com.opsysinc.scripting.shared.JobExecutorData;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Executor tab.
 *
 * @author mkitchin
 */
public class StatusTab extends AbstractTab {

    /**
     * Default max completed jobs.
     */
    public static final int DEFAULT_MAX_COMPLETED_JOBS = 100;

    /**
     * Default double-click time in milliseconds.
     */
    public static final long DEFAULT_DOUBLE_CLICK_TIME_IN_MS = 300L;

    /**
     * Executors.
     */
    private ListDataProvider<JobExecutorData> executors;

    /**
     * Executor data grid.
     */
    private DataGrid<JobExecutorData> executorsDataGrid;

    /**
     * Pending jobs.
     */
    private ListDataProvider<JobContentData> pendingJobs;

    /**
     * Pending data grid.
     */
    private DataGrid<JobContentData> pendingJobsDataGrid;

    /**
     * Completed jobs.
     */
    private ListDataProvider<JobContentData> completedJobs;

    /**
     * Completed data grid.
     */
    private DataGrid<JobContentData> completedJobsDataGrid;

    /**
     * Basic ctor.
     *
     * @param scriptService Script service.
     * @param parentPanel   Parent panel.
     */
    public StatusTab(final ScriptWebApp scriptWebApp,
                     final ScriptServiceAsync scriptService,
                     final TabLayoutPanel parentPanel) {

        super(scriptWebApp, scriptService, parentPanel, "Status");
        this.init();
    }

    /**
     * Adds completed job.
     *
     * @param completedJob Completed job.
     * @param isToRefresh  True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean addCompletedJob(final JobContentData completedJob,
                                   final boolean isToRefresh) {

        JobDataUtils.checkNullObject(completedJob, true);

        this.removePendingJob(completedJob, false);
        final boolean result = !this.removeCompletedJob(completedJob, false);

        this.completedJobs.getList().add(completedJob);

        while (this.completedJobs.getList().size() > StatusTab.DEFAULT_MAX_COMPLETED_JOBS) {

            this.completedJobs.getList().remove(
                    this.completedJobs.getList().get(0));
        }

        if (isToRefresh) {

            this.updateJobDataGrids();
        }

        return result;
    }

    /**
     * Adds pending job.
     *
     * @param executor    Pending job.
     * @param isToRefresh True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean addExecutor(final JobExecutorData executor,
                               final boolean isToRefresh) {

        JobDataUtils.checkNullObject(executor, true);
        final boolean result = !this.removeExecutor(executor, false);

        this.executors.getList().add(executor);

        if (isToRefresh) {

            this.updateExecutorDataGrids();
        }

        return result;
    }

    /**
     * Adds pending job.
     *
     * @param pendingJob  Pending job.
     * @param isToRefresh True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean addPendingJob(final JobContentData pendingJob,
                                 final boolean isToRefresh) {

        JobDataUtils.checkNullObject(pendingJob, true);

        this.removeCompletedJob(pendingJob, false);
        final boolean result = !this.removePendingJob(pendingJob, false);

        this.pendingJobs.getList().add(pendingJob);

        if (isToRefresh) {

            this.updateJobDataGrids();
        }

        return result;
    }

    /**
     * Get completed jobs.
     *
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection first, false otherwise.
     * @return True if target collection modified.
     */
    public boolean getCompletedJobs(
            final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        return target.addAll(this.completedJobs.getList());
    }

    /**
     * Get pending jobs.
     *
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection first, false otherwise.
     * @return True if target collection modified.
     */
    public boolean getExecutors(final Collection<JobExecutorData> target,
                                final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        return target.addAll(this.executors.getList());
    }

    /**
     * Get pending jobs.
     *
     * @param target       Target collection.
     * @param isClearFirst True to clear target collection first, false otherwise.
     * @return True if target collection modified.
     */
    public boolean getPendingJobs(
            final Collection<JobContentData> target, final boolean isClearFirst) {

        JobDataUtils.checkNullObject(target, true);

        if (isClearFirst) {

            target.clear();
        }

        return target.addAll(this.pendingJobs.getList());
    }

    /**
     * Single-use init method.
     */
    private void init() {

        this.executors = new ListDataProvider<>(
                new LinkedList<JobExecutorData>());
        this.pendingJobs = new ListDataProvider<>(
                new LinkedList<JobContentData>());
        this.completedJobs = new ListDataProvider<>(
                new LinkedList<JobContentData>());
    }

    /**
     * Remove all completed jobs.
     *
     * @param isToRefresh True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean removeAllCompletedJobs(final boolean isToRefresh) {

        final boolean result = !this.completedJobs.getList().isEmpty();
        this.completedJobs.getList().clear();

        if (isToRefresh) {

            this.updateJobDataGrids();
        }

        return result;
    }

    /**
     * Remove all pending jobs.
     *
     * @param isToRefresh True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean removeAllExecutors(final boolean isToRefresh) {

        final boolean result = !this.executors.getList().isEmpty();
        this.executors.getList().clear();

        if (isToRefresh) {

            this.updateExecutorDataGrids();
        }

        return result;
    }

    /**
     * Remove all pending jobs.
     *
     * @param isToRefresh True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean removeAllPendingJobs(final boolean isToRefresh) {

        final boolean result = !this.pendingJobs.getList().isEmpty();
        this.pendingJobs.getList().clear();

        if (isToRefresh) {

            this.updateJobDataGrids();
        }

        return result;
    }

    /**
     * Remove completed job.
     *
     * @param completedJob Completed job.
     * @param isToRefresh  True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean removeCompletedJob(final JobContentData completedJob,
                                      final boolean isToRefresh) {

        JobDataUtils.checkNullObject(completedJob, true);
        final boolean result = this.completedJobs.getList()
                .remove(completedJob);

        if (isToRefresh) {

            this.updateJobDataGrids();
        }

        return result;
    }

    /**
     * Remove pending job.
     *
     * @param executor    Pending job.
     * @param isToRefresh True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean removeExecutor(final JobExecutorData executor,
                                  final boolean isToRefresh) {

        JobDataUtils.checkNullObject(executor, true);
        final boolean result = this.executors.getList().remove(executor);

        if (isToRefresh) {

            this.updateExecutorDataGrids();
        }

        return result;
    }

    /**
     * Remove pending job.
     *
     * @param pendingJob  Pending job.
     * @param isToRefresh True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean removePendingJob(final JobContentData pendingJob,
                                    final boolean isToRefresh) {

        JobDataUtils.checkNullObject(pendingJob, true);
        final boolean result = this.pendingJobs.getList().remove(pendingJob);

        if (isToRefresh) {

            this.updateJobDataGrids();
        }

        return result;
    }

    /**
     * Double-click on executor.
     *
     * @param executorDat
     */
    private void runSelectExecutor(final JobExecutorData executorData) {

        JobDataUtils.checkNullObject(executorData, true);
        this.getScriptWebApp().selectExecutor(executorData, true);
    }

    /**
     * Starts up.
     */
    @Override
    public void runStartUp() {

        final TabLayoutPanel parentPanel = this.getParentPanel();
        final LayoutPanel borderPanel = this.getBorderPanel();

        parentPanel.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(final SelectionEvent<Integer> arg0) {

                StatusTab.this.getScriptWebApp().setLastClickTime(0L);

                if (StatusTab.this.isActiveTab()) {

                    StatusTab.this.updateJobDataGrids();
                    StatusTab.this.updateExecutorDataGrids();
                }
            }
        });

        // MAIN
        final SplitLayoutPanel mainPanel = new SplitLayoutPanel();
        borderPanel.add(mainPanel); // +

        borderPanel.setWidgetLeftRight(mainPanel, 1, Style.Unit.EM, 1, Style.Unit.EM);
        borderPanel.setWidgetTopBottom(mainPanel, 1, Style.Unit.EM, 1, Style.Unit.EM);

        // WEST
        final SplitLayoutPanel westPanel = new SplitLayoutPanel();
        mainPanel.addWest(westPanel, 700); // +

        // EXECUTORS (west, north)
        final StackLayoutPanel statusPanel = new StackLayoutPanel(Style.Unit.EM);
        westPanel.addNorth(statusPanel, 400); // +

        this.executorsDataGrid = JobWidgetUtils
                .createJobExecutorDataGrid(this.executors);
        this.executorsDataGrid.setWidth("100%");
        statusPanel.add(this.executorsDataGrid, new HTML("Engines"), 2);

        this.executorsDataGrid
                .addCellPreviewHandler(new CellPreviewEvent.Handler<JobExecutorData>() {

                    private long prevClickAt = 0L;

                    @Override
                    public void onCellPreview(
                            final CellPreviewEvent<JobExecutorData> arg0) {

                        StatusTab.this.getScriptWebApp().setLastClickTime(0L);
                        final long currClickAt = System.currentTimeMillis();

                        if (arg0.getNativeEvent().getType().contains("click")) {

                            if ((currClickAt - this.prevClickAt) <
                                    StatusTab.DEFAULT_DOUBLE_CLICK_TIME_IN_MS) {

                                StatusTab.this.runSelectExecutor(arg0
                                        .getValue());
                            }

                            this.prevClickAt = currClickAt;
                        }
                    }
                });

        // PENDING (west, south/remainder)
        final StackLayoutPanel pendingStackPanel = new StackLayoutPanel(Style.Unit.EM);
        westPanel.add(pendingStackPanel); // +

        this.pendingJobsDataGrid = JobWidgetUtils
                .createJobContentDataGrid(this.pendingJobs);
        this.pendingJobsDataGrid.setWidth("100%");
        pendingStackPanel.add(this.pendingJobsDataGrid, new HTML("Pending"), 2);

        this.pendingJobsDataGrid
                .addCellPreviewHandler(new CellPreviewEvent.Handler<JobContentData>() {

                    private long prevClickAt = 0L;

                    @Override
                    public void onCellPreview(
                            final CellPreviewEvent<JobContentData> arg0) {

                        StatusTab.this.getScriptWebApp().setLastClickTime(0L);
                        final long currClickAt = System.currentTimeMillis();

                        if (arg0.getNativeEvent().getType().contains("click")) {

                            if ((currClickAt - this.prevClickAt) < StatusTab.DEFAULT_DOUBLE_CLICK_TIME_IN_MS) {

                                StatusTab.this.runSelectExecutor(arg0
                                        .getValue().getExecutorData());
                            }

                            this.prevClickAt = currClickAt;
                        }
                    }
                });

        // COMPLETED (east/remainder)
        final StackLayoutPanel completedStackPanel = new StackLayoutPanel(
                Style.Unit.EM);
        mainPanel.add(completedStackPanel); // +

        this.completedJobsDataGrid = JobWidgetUtils
                .createJobContentDataGrid(this.completedJobs);
        this.completedJobsDataGrid.setWidth("100%");
        completedStackPanel.add(this.completedJobsDataGrid, new HTML(
                "Completed"), 2);

        this.completedJobsDataGrid
                .addCellPreviewHandler(new CellPreviewEvent.Handler<JobContentData>() {

                    private long prevClickAt = 0L;

                    @Override
                    public void onCellPreview(
                            final CellPreviewEvent<JobContentData> arg0) {

                        StatusTab.this.getScriptWebApp().setLastClickTime(0L);
                        final long currClickAt = System.currentTimeMillis();

                        if (arg0.getNativeEvent().getType().contains("click")) {

                            if ((currClickAt - this.prevClickAt) < StatusTab.DEFAULT_DOUBLE_CLICK_TIME_IN_MS) {

                                StatusTab.this.runSelectExecutor(arg0
                                        .getValue().getExecutorData());
                            }

                            this.prevClickAt = currClickAt;
                        }
                    }
                });
    }

    /**
     * Update executor data grids.
     */
    public void updateExecutorDataGrids() {

        // executors
        this.executors.refresh();
        ColumnSortEvent.fire(this.executorsDataGrid,
                this.executorsDataGrid.getColumnSortList());
    }

    /**
     * Update job data grids.
     */
    public void updateJobDataGrids() {

        // pending
        this.pendingJobs.refresh();
        ColumnSortEvent.fire(this.pendingJobsDataGrid,
                this.pendingJobsDataGrid.getColumnSortList());

        // completed
        this.completedJobs.refresh();
        ColumnSortEvent.fire(this.completedJobsDataGrid,
                this.completedJobsDataGrid.getColumnSortList());
    }
}
