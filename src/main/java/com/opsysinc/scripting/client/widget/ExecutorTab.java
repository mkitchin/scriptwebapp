package com.opsysinc.scripting.client.widget;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.opsysinc.scripting.client.app.ScriptWebApp;
import com.opsysinc.scripting.client.service.ScriptServiceAsync;
import com.opsysinc.scripting.shared.JobContentData;
import com.opsysinc.scripting.shared.JobDataUtils;
import com.opsysinc.scripting.shared.JobExecutorData;

import java.util.*;

/**
 * Executor tab.
 *
 * @author mkitchin
 */
public class ExecutorTab extends AbstractTab {

    /**
     * Variable scope.
     *
     * @author mkit
     */
    private enum VariableScope {

        engine(100), global(200);

        /**
         * Value.
         */
        private final int value;

        /**
         * Basic ctor.
         *
         * @param value Value.
         */
        private VariableScope(final int value) {

            this.value = value;
        }

        /**
         * Gets value.
         *
         * @return Value.
         */
        public int getValue() {

            return this.value;
        }
    }

    /**
     * Default double-click time in milliseconds.
     */
    public static final long DEFAULT_DOUBLE_CLICK_TIME_IN_MS = 300L;

    /**
     * Default max completed jobs.
     */
    public static final int DEFAULT_MAX_COMPLETED_JOBS = 20;

    /**
     * Executor data.
     */
    private final JobExecutorData executorData;

    /**
     * Pending jobs.
     */
    private ListDataProvider<JobContentData> pendingJobs;

    /**
     * Pending data grid.
     */
    private DataGrid<JobContentData> pendingJobsDataGrid;

    /**
     * Latest pending job.
     */
    private JobContentData latestPendingJob;

    /**
     * Completed jobs.
     */
    private ListDataProvider<JobContentData> completedJobs;

    /**
     * Completed data grid.
     */
    private DataGrid<JobContentData> completedJobsDataGrid;

    /**
     * Latest completed job.
     */
    private JobContentData latestCompletedJob;

    /**
     * Pending job modified time.
     */
    private long pendingJobModifiedTime;

    /**
     * Completed job modified time.
     */
    private long completedJobModifiedTime;

    /**
     * Latest submitted job.
     */
    private JobContentData latestSubmittedJob;

    /**
     * Latest submitted job.
     */
    private JobContentData latestDisplayedJob;

    /**
     * Local variables.
     */
    private Map<String, String> localVariables;

    /**
     * Local variables data grid.
     */
    private DataGrid<Map.Entry<String, String>> localVariablesDataGrid;

    /**
     * Local variables.
     */
    private Map<String, String> globalVariables;

    /**
     * Global variables data grid.
     */
    private DataGrid<Map.Entry<String, String>> globalVariablesDataGrid;

    /**
     * Input text area.
     */
    private TextArea codeTextArea;

    /**
     * Output text area.
     */
    private TextArea outputTextArea;

    /**
     * Basic ctor.
     *
     * @param scriptService Script service.
     * @param parentPanel   Parent panel.
     */
    public ExecutorTab(final ScriptWebApp scriptWebApp,
                       final ScriptServiceAsync scriptService,
                       final TabLayoutPanel parentPanel, final JobExecutorData executorData) {

        super(scriptWebApp, scriptService, parentPanel, executorData.getType()
                .getExecutorTitle() + ": " + executorData.getTitle());
        this.executorData = executorData;

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
        this.completedJobModifiedTime = Math.max(this.completedJobModifiedTime,
                completedJob.getModifiedTime());

        this.removePendingJob(completedJob, false);
        final boolean result = !this.removeCompletedJob(completedJob, false);

        this.completedJobs.getList().add(completedJob);
        this.latestCompletedJob = completedJob;

        while (this.completedJobs.getList().size() > ExecutorTab.DEFAULT_MAX_COMPLETED_JOBS) {

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
     * @param pendingJob  Pending job.
     * @param isToRefresh True to refresh table, false otherwise
     * @return True if set modified, false otherwise.
     */
    public boolean addPendingJob(final JobContentData pendingJob,
                                 final boolean isToRefresh) {

        JobDataUtils.checkNullObject(pendingJob, true);
        this.pendingJobModifiedTime = Math.max(this.pendingJobModifiedTime,
                pendingJob.getModifiedTime());

        this.removeCompletedJob(pendingJob, false);
        final boolean result = !this.removePendingJob(pendingJob, false);

        this.pendingJobs.getList().add(pendingJob);
        this.latestPendingJob = pendingJob;

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
     * Gets executor data.
     *
     * @return Executor data.
     */
    public JobExecutorData getExecutorData() {

        return this.executorData;
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
     * Single-use init.
     */
    private void init() {

        this.pendingJobs = new ListDataProvider<>(
                new LinkedList<JobContentData>());
        this.completedJobs = new ListDataProvider<>(
                new LinkedList<JobContentData>());
        this.localVariables = new LinkedHashMap<>();
        this.globalVariables = new LinkedHashMap<>();
        this.pendingJobModifiedTime = 0L;
        this.completedJobModifiedTime = 0L;
        this.latestPendingJob = null;
        this.latestCompletedJob = null;
        this.latestSubmittedJob = null;
        this.latestDisplayedJob = null;
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
     * Cleans up.
     */
    @Override
    protected void runCleanUp() {
    }

    /**
     * On input execute.
     *
     * @param clickEvent Click event.
     */
    private void runCodeExecute(final ClickEvent clickEvent) {

        final String inputText = this.codeTextArea.getText();

        if (JobDataUtils.checkEmptyString(inputText, false)) {

            return;
        }

        this.latestSubmittedJob = new JobContentData(inputText, false);
        this.latestSubmittedJob.setExecutorData(this.executorData);

        this.getScriptService().submitJob(this.latestSubmittedJob,
                new AsyncCallback<JobContentData>() {

                    @Override
                    public void onFailure(final Throwable arg0) {

                        ExecutorTab.this.handleError("submit job", arg0);
                    }

                    @Override
                    public void onSuccess(final JobContentData arg0) {

                        ExecutorTab.this.runSubmitJobSucess(arg0);
                    }
                });
    }

    /**
     * Retrieves variables.
     *
     * @param variableScope Variable scope.
     */
    private void runReadVariables(final ExecutorTab.VariableScope variableScope) {

        JobDataUtils.checkNullObject(variableScope, true);
        ScriptWebApp.showWaitCursor();

        this.getScriptService().getExecutorVariables(this.executorData,
                variableScope.getValue(), new AsyncCallback<String[][]>() {

                    @Override
                    public void onFailure(final Throwable arg0) {

                        ScriptWebApp.showDefaultCursor();
                        ExecutorTab.this.handleError("read " + variableScope
                                + " variabls", arg0);
                    }

                    @Override
                    public void onSuccess(final String[][] arg0) {

                        ScriptWebApp.showDefaultCursor();
                        ExecutorTab.this
                                .runUpdateVariables(variableScope, arg0);
                    }
                });
    }

    /**
     * Run refresh.
     */
    @Override
    protected void runRefresh() {

        final int tabIndex = this.getTabIndex();

        if (this.getTabIndex() == -1) {

            return;
        }

        String nextTitle = this.getTabTitle();

        if (this.latestCompletedJob != null) {

            if ((this.latestDisplayedJob == null)
                    || !this.latestDisplayedJob.equals(this.latestCompletedJob)) {

                if (this.isActiveTab()) {

                    this.updateJobTextAreas();
                    this.latestDisplayedJob = this.latestCompletedJob;

                } else {

                    nextTitle += " (*)";
                }
            }
        }

        this.getParentPanel().setTabText(tabIndex, nextTitle);
    }

    /**
     * Starts up.
     */
    @Override
    protected void runStartUp() {

        final TabLayoutPanel parentPanel = this.getParentPanel();
        final LayoutPanel borderPanel = this.getBorderPanel();

        parentPanel.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(final SelectionEvent<Integer> arg0) {

                ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);

                if (ExecutorTab.this.isActiveTab()) {

                    ExecutorTab.this.updateJobDataGrids();
                    ExecutorTab.this.updateVariableDataGrids();
                }
            }
        });

        // split panel
        final SplitLayoutPanel mainPanel = new SplitLayoutPanel();
        borderPanel.add(mainPanel); // +

        borderPanel.setWidgetLeftRight(mainPanel, 1, Style.Unit.EM, 1, Style.Unit.EM);
        borderPanel.setWidgetTopBottom(mainPanel, 1, Style.Unit.EM, 1, Style.Unit.EM);

        // WEST
        final SplitLayoutPanel westPanel = new SplitLayoutPanel();
        mainPanel.addWest(westPanel, 700); // +

        // INPUT (west, north)
        final StackLayoutPanel inputStackPanel = new StackLayoutPanel(Style.Unit.EM);
        westPanel.addNorth(inputStackPanel, 400); // +

        final TabLayoutPanel inputTabPanel = new TabLayoutPanel(2.0, Style.Unit.EM);
        inputStackPanel.add(inputTabPanel, new HTML("Input"), 2);

        inputTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(final SelectionEvent<Integer> arg0) {

                ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
                // TODO - Do stuff
            }
        });

        // CODE
        final DockLayoutPanel codeLayoutPanel = new DockLayoutPanel(Style.Unit.EM);
        inputTabPanel.add(codeLayoutPanel, new HTML("Code"));

        final DockLayoutPanel codeButtonPanel = new DockLayoutPanel(Style.Unit.EM);
        codeLayoutPanel.addSouth(codeButtonPanel, 2);

        final Button codeExecuteButton = new Button("Execute",
                new ClickHandler() {

                    @Override
                    public void onClick(final ClickEvent arg0) {

                        ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
                        ExecutorTab.this.runCodeExecute(arg0);
                    }
                });
        codeExecuteButton.setWidth("5em");
        codeButtonPanel.addWest(codeExecuteButton, 6);

        final Button codeClearButton = new Button("Clear", new ClickHandler() {

            @Override
            public void onClick(final ClickEvent arg0) {

                ExecutorTab.this.codeTextArea.setText("");
            }
        });

        codeClearButton.setWidth("5em");
        codeButtonPanel.addWest(codeClearButton, 6);

        final Button codeSelectButton = new Button("Select",
                new ClickHandler() {

                    @Override
                    public void onClick(final ClickEvent arg0) {

                        ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
                        ExecutorTab.this.codeTextArea.selectAll();
                    }
                });

        codeSelectButton.setWidth("5em");
        codeButtonPanel.addEast(codeSelectButton, 6);

        this.codeTextArea = new TextArea();
        this.codeTextArea.setWidth("99%");
        this.codeTextArea.setHeight("99%");
        codeLayoutPanel.add(this.codeTextArea);

        this.codeTextArea.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(final FocusEvent arg0) {

                ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
            }
        });

        // FILES
        // final DockLayoutPanel filesLayoutPanel = new
        // DockLayoutPanel(Unit.EM);
        // inputTabPanel.add(filesLayoutPanel, new HTML("Files"));
        //
        // final DockLayoutPanel filesButtonPanel = new
        // DockLayoutPanel(Unit.EM);
        // filesLayoutPanel.addSouth(filesButtonPanel, 4);
        //
        // final Tree filesTree = new Tree();
        // filesLayoutPanel.add(filesTree);

        // OUTPUT (west, south/remainder)
        final StackLayoutPanel outputStackPanel = new StackLayoutPanel(Style.Unit.EM);
        westPanel.add(outputStackPanel); // +

        final DockLayoutPanel outputLayoutPanel = new DockLayoutPanel(Style.Unit.EM);
        outputStackPanel.add(outputLayoutPanel, new HTML("Output"), 2);

        final DockLayoutPanel outputButtonPanel = new DockLayoutPanel(Style.Unit.EM);
        outputLayoutPanel.addSouth(outputButtonPanel, 2);

        final Button outputSelectButton = new Button("Select",
                new ClickHandler() {

                    @Override
                    public void onClick(final ClickEvent arg0) {

                        ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
                        ExecutorTab.this.outputTextArea.selectAll();
                    }
                });
        outputSelectButton.setWidth("5em");
        outputButtonPanel.addEast(outputSelectButton, 6);

        this.outputTextArea = new TextArea();
        this.outputTextArea.setWidth("99%");
        this.outputTextArea.setHeight("99%");
        this.outputTextArea.setReadOnly(true);
        outputLayoutPanel.add(this.outputTextArea);

        this.outputTextArea.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(final FocusEvent arg0) {

                ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
            }
        });

        // EAST (remainder)
        final SplitLayoutPanel eastPanel = new SplitLayoutPanel();
        mainPanel.add(eastPanel); // +

        // VARIABLES (west, north)
        final StackLayoutPanel variablesStackPanel = new StackLayoutPanel(
                Style.Unit.EM);
        eastPanel.addNorth(variablesStackPanel, 200); // +

        final TabLayoutPanel variablesTabPanel = new TabLayoutPanel(2.0,
                Style.Unit.EM);
        variablesStackPanel.add(variablesTabPanel, new HTML("Variables"), 2);

        variablesTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(final SelectionEvent<Integer> arg0) {

                ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);

                if (ExecutorTab.this.isActiveTab()) {

                    ExecutorTab.this.updateVariableDataGrids();
                }
            }
        });

        // VARIABLES: GLOBAL
        final DockLayoutPanel globalVariablesLayoutPanel = new DockLayoutPanel(
                Style.Unit.EM);
        variablesTabPanel.add(globalVariablesLayoutPanel, "Global");

        final DockLayoutPanel globalVariablesButtonPanel = new DockLayoutPanel(
                Style.Unit.EM);
        globalVariablesLayoutPanel.addSouth(globalVariablesButtonPanel, 2);

        final Button globalVariablesSelectButton = new Button("Read",
                new ClickHandler() {

                    @Override
                    public void onClick(final ClickEvent arg0) {

                        ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
                        ExecutorTab.this.runReadVariables(ExecutorTab.VariableScope.engine);
                    }
                });
        globalVariablesSelectButton.setWidth("5em");
        globalVariablesButtonPanel.addWest(globalVariablesSelectButton, 6);

        this.globalVariablesDataGrid = JobWidgetUtils
                .createNameValueDataGrid(new ArrayList<Map.Entry<String, String>>(0));
        globalVariablesLayoutPanel.add(this.globalVariablesDataGrid);

        // VARIABLES: LOCAL
        final DockLayoutPanel localVariablesLayoutPanel = new DockLayoutPanel(
                Style.Unit.EM);
        variablesTabPanel.add(localVariablesLayoutPanel, "Local");

        final DockLayoutPanel localVariablesButtonPanel = new DockLayoutPanel(
                Style.Unit.EM);
        localVariablesLayoutPanel.addSouth(localVariablesButtonPanel, 2);

        final Button localVariablesSelectButton = new Button("Read",
                new ClickHandler() {

                    @Override
                    public void onClick(final ClickEvent arg0) {

                        ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
                        ExecutorTab.this.runReadVariables(ExecutorTab.VariableScope.engine);
                    }
                });
        localVariablesSelectButton.setWidth("5em");
        localVariablesButtonPanel.addWest(localVariablesSelectButton, 6);

        this.localVariablesDataGrid = JobWidgetUtils
                .createNameValueDataGrid(new ArrayList<Map.Entry<String, String>>(0));
        localVariablesLayoutPanel.add(this.localVariablesDataGrid);

        variablesTabPanel.selectTab(localVariablesLayoutPanel);

        // PENDING (west, north)
        final StackLayoutPanel pendingStackPanel = new StackLayoutPanel(Style.Unit.EM);
        eastPanel.addNorth(pendingStackPanel, 150); // +

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

                        ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
                        final long currClickAt = System.currentTimeMillis();

                        if (arg0.getNativeEvent().getType().contains("click")) {

                            if ((currClickAt - this.prevClickAt) < ExecutorTab.DEFAULT_DOUBLE_CLICK_TIME_IN_MS) {

                                ExecutorTab.this.latestCompletedJob = arg0
                                        .getValue();
                            }

                            this.prevClickAt = currClickAt;
                        }
                    }
                });

        // COMPLETED (west, north)
        final StackLayoutPanel completedStackPanel = new StackLayoutPanel(
                Style.Unit.EM);
        eastPanel.add(completedStackPanel); // +

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

                        ExecutorTab.this.getScriptWebApp().setLastClickTime(0L);
                        final long currClickAt = System.currentTimeMillis();

                        if (arg0.getNativeEvent().getType().contains("click")) {

                            if ((currClickAt - this.prevClickAt) < ExecutorTab.DEFAULT_DOUBLE_CLICK_TIME_IN_MS) {

                                ExecutorTab.this.latestCompletedJob = arg0
                                        .getValue();
                            }

                            this.prevClickAt = currClickAt;
                        }
                    }
                });
    }

    /**
     * On submit job success.
     *
     * @param contentData Content data.
     */
    private void runSubmitJobSucess(final JobContentData contentData) {

        this.latestSubmittedJob = contentData;
    }

    /**
     * Updates variables.
     *
     * @param variableScope Variable scope.
     * @param variableData  Variable data.
     */
    private void runUpdateVariables(final ExecutorTab.VariableScope variableScope,
                                    final String[][] variableData) {

        JobDataUtils.checkNullObject(variableScope, true);
        JobDataUtils.checkNullObject(variableData, true);

        Map<String, String> variables = null;

        if (variableScope.equals(ExecutorTab.VariableScope.engine)) {

            variables = this.localVariables;

        } else {

            variables = this.globalVariables;
        }

        variables.clear();

        for (final String[] item : variableData) {

            variables.put(item[0], item[1]);
        }

        this.updateVariableDataGrids();
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

    /**
     * Update job text areas.
     */
    public void updateJobTextAreas() {

        this.codeTextArea.setText(JobWidgetUtils.formatCellText(
                this.latestCompletedJob.getRequestData().getRequestBody(),
                false));
        this.outputTextArea.setText(JobWidgetUtils.formatCellText(
                JobWidgetUtils.buildJobResultText(this.latestCompletedJob),
                false));
    }

    /**
     * Update variable data grids.
     */
    public void updateVariableDataGrids() {

        // global
        this.globalVariablesDataGrid
                .setRowData(new ArrayList<>(
                        this.globalVariables.entrySet()));
        this.globalVariablesDataGrid.setRowCount(this.globalVariables.size());
        ColumnSortEvent.fire(this.globalVariablesDataGrid,
                this.globalVariablesDataGrid.getColumnSortList());

        // local
        this.localVariablesDataGrid
                .setRowData(new ArrayList<>(
                        this.localVariables.entrySet()));
        this.localVariablesDataGrid.setRowCount(this.localVariables.size());
        ColumnSortEvent.fire(this.localVariablesDataGrid,
                this.localVariablesDataGrid.getColumnSortList());
    }
}
