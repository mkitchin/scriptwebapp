package com.opsysinc.scripting.client.app;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.opsysinc.scripting.client.service.ScriptService;
import com.opsysinc.scripting.client.service.ScriptServiceAsync;
import com.opsysinc.scripting.client.widget.ExecutorTab;
import com.opsysinc.scripting.client.widget.StatusTab;
import com.opsysinc.scripting.shared.*;

import java.util.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ScriptWebApp implements EntryPoint {

    /**
     * New executor dialog.
     *
     * @author mkitchin
     */
    private final class NewExecutorDialog extends DialogBox {

        /**
         * Basic ctor.
         *
         * @param executorType Executor type.
         */
        private NewExecutorDialog(final JobExecutorType executorType) {

            this.setText("New " + executorType.getExecutorTitle()
                    + " Engine...");
            this.setAnimationEnabled(true);
            this.setGlassEnabled(true);
            this.setModal(true);

            final FlexTable flexTable = new FlexTable();
            final TextBox textBox = new TextBox();
            final Button okButton = new Button("OK");
            final Button cancelButton = new Button("Cancel");

            textBox.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(final ValueChangeEvent<String> arg0) {

                    ScriptWebApp.this.setLastClickTime(0L);
                    okButton.setEnabled(!JobDataUtils.checkEmptyString(
                            arg0.getValue(), false));
                }
            });

            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent event) {

                    ScriptWebApp.this.setLastClickTime(0L);
                    ScriptWebApp.this.runNewExecutor(textBox.getText().trim(),
                            executorType);
                    ScriptWebApp.NewExecutorDialog.this.hide();
                }
            });

            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(final ClickEvent event) {

                    ScriptWebApp.this.setLastClickTime(0L);
                    ScriptWebApp.NewExecutorDialog.this.hide();
                }
            });

            flexTable.setText(0, 0, "Title:");
            flexTable.setWidget(0, 1, textBox);
            flexTable.setWidget(1, 0, okButton);
            flexTable.setWidget(1, 1, cancelButton);

            this.setWidget(flexTable);
            this.center();
        }
    }

    /**
     * Show default cursor.
     */
    public static void showDefaultCursor() {

        RootPanel.getBodyElement().getStyle().setProperty("cursor", "default");
    }

    /**
     * Show wait cursor.
     */
    public static void showWaitCursor() {

        RootPanel.getBodyElement().getStyle().setProperty("cursor", "wait");
    }

    /**
     * Default refresh timer interval in milliseconds.
     */
    public static final int DEFAULT_REFRESH_TIMER_INTERVAL_MS = 2000;

    /**
     * Main menu.
     */
    private MenuBar mainMenu;

    /**
     * Tab layout panel.
     */
    private TabLayoutPanel tabLayoutPanel;

    /**
     * Status tab.
     */
    private StatusTab statusTab;

    /**
     * Refresh timer.
     */
    private Timer refreshTimer;

    /**
     * Prev executors.
     */
    private Set<JobExecutorData> currExecutors;

    /**
     * Next executors.
     */
    private Set<JobExecutorData> nextExecutors;

    /**
     * Tab map.
     */
    private Map<JobExecutorData, ExecutorTab> visibleExecutorTabs;

    /**
     * Tab map.
     */
    private Map<JobExecutorData, ExecutorTab> hiddenExecutorTabs;

    /**
     * Prev job modified time.
     */
    private long currJobModifiedTime;

    /**
     * Next job modified time.
     */
    private long nextJobModifiedTime;

    /**
     * Previous click time.
     */
    private long lastClickTime;

    /**
     * Last timer delay in MS.
     */
    private long lastTimerDelayInMS;

    /**
     * Is app active?
     */
    private boolean isAppActive;

    /**
     * Create a remote service proxy to talk to the server-side Greeting
     * service.
     */
    private final ScriptServiceAsync scriptService = GWT
            .create(ScriptService.class);

    /**
     * Basic ctor.
     */
    public ScriptWebApp() {

        this.init();
    }

    /**
     * Build refresh timer.
     */
    private void buildRefreshTimer() {

        if (this.refreshTimer != null) {

            try {

                this.refreshTimer.cancel();

            } catch (final Throwable ignore) {

                // ignore;
            }

            this.refreshTimer = null;
        }

        this.refreshTimer = new Timer() {

            @Override
            public void run() {

                try {

                    ScriptWebApp.this.runRefresh();

                } catch (final Throwable ex) {

                    Window.alert("Can't refresh (will retry; "
                            + ex.getMessage() + ").");
                }
            }
        };

        this.scheduleRefreshTimer(true);
    }

    /**
     * Builds standard widgets (e.g., status).
     */
    private void buildStandardWidgets() {

        // ROOT
        final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();

        // BORDER
        final LayoutPanel borderPanel = new LayoutPanel();
        rootLayoutPanel.add(borderPanel);

        // MAIN
        final StackLayoutPanel mainLayoutPanel = new StackLayoutPanel(Style.Unit.EM);
        borderPanel.add(mainLayoutPanel);
        borderPanel.setWidgetLeftRight(mainLayoutPanel, 1, Style.Unit.EM, 1, Style.Unit.EM);
        borderPanel.setWidgetTopBottom(mainLayoutPanel, 1, Style.Unit.EM, 1, Style.Unit.EM);

        // MENU: FILE
        final MenuBar fileMenu = new MenuBar(true);

        // MENU: FILE/NEW
        final MenuBar newMenu = new MenuBar(true);
        fileMenu.addItem("New", newMenu);

        final JobExecutorType[] executorTypes = JobExecutorType.values();
        Arrays.sort(executorTypes, new Comparator<JobExecutorType>() {

            @Override
            public int compare(final JobExecutorType arg0,
                               final JobExecutorType arg1) {

                return arg0.getExecutorTitle().compareTo(
                        arg1.getExecutorTitle());
            }
        });

        for (final JobExecutorType item : executorTypes) {

            newMenu.addItem(item.getExecutorTitle(), new Command() {

                @Override
                public void execute() {

                    ScriptWebApp.this.setLastClickTime(0L);
                    new ScriptWebApp.NewExecutorDialog(item).show();
                }
            });
        }

        // MENU: WINDOW
        final MenuBar windowMenu = new MenuBar(true);

        windowMenu.addItem("Hide", new Command() {

            @Override
            public void execute() {

                ScriptWebApp.this.setLastClickTime(0L);
                ScriptWebApp.this.runHideWindow();
            }
        });

        windowMenu.addItem("Close", new Command() {

            @Override
            public void execute() {

                ScriptWebApp.this.setLastClickTime(0L);
                ScriptWebApp.this.runCloseWindow();
            }
        });

        // MENU: MAIN
        this.mainMenu = new MenuBar(false);

        this.mainMenu.addItem("File", fileMenu);
        this.mainMenu.addItem("Window", windowMenu);

        // TAB
        this.tabLayoutPanel = new TabLayoutPanel(2.0, Style.Unit.EM);
        mainLayoutPanel.add(this.tabLayoutPanel, this.mainMenu, 2);

        // STATUS
        this.statusTab = new StatusTab(this, this.scriptService,
                this.tabLayoutPanel);
        this.statusTab.startUp();
    }

    /**
     * Gets last click time.
     *
     * @return Last click time.
     */
    public long getLastClickTime() {

        return this.lastClickTime;
    }

    /**
     * Gets main menu.
     *
     * @return the mainMenu Main menu.
     */
    public MenuBar getMainMenu() {

        return this.mainMenu;
    }

    /**
     * Gets selected executor data.
     *
     * @return Selected executor data if found, null otherwise.
     */
    public JobExecutorData getSelectedExecutorData() {

        JobExecutorData result = null;
        final int tabIndex = this.tabLayoutPanel.getSelectedIndex();

        if (tabIndex == -1) {

            return null;
        }

        for (final ExecutorTab item : this.visibleExecutorTabs.values()) {

            if (item.isActiveTab()) {

                result = item.getExecutorData();
                break;
            }
        }

        return result;
    }

    /**
     * Gets selected executor tab.
     *
     * @return Selected executor tab if found, null otherwise.
     */
    public ExecutorTab getSelectedExecutorTab() {

        ExecutorTab result = null;
        final int tabIndex = this.tabLayoutPanel.getSelectedIndex();

        if (tabIndex == -1) {

            return null;
        }

        for (final ExecutorTab item : this.visibleExecutorTabs.values()) {

            if (item.isActiveTab()) {

                result = item;
                break;
            }
        }

        return result;
    }

    /**
     * Gets tab layout panel.
     *
     * @return the tabLayoutPanel
     */
    public TabLayoutPanel getTabLayoutPanel() {

        return this.tabLayoutPanel;
    }

    /**
     * Handle setup error.
     *
     * @param cantText "Can't" text.
     * @param ex       Setup error.
     */
    protected void handleError(final String cantText, final Throwable ex) {

        Window.alert("Can't " + cantText + " + (" + ex.getMessage() + ").");
    }

    /**
     * Handle setup error.
     *
     * @param noticeText Notice text.
     */
    protected void handleNotice(final String noticeText) {

        Window.alert("Notice: " + noticeText);
    }

    /**
     * Handle setup error.
     *
     * @param ex Setup error.
     */
    private void handleUpdateError(final Throwable ex, final boolean isReset) {

        if (isReset) {

            Window.alert("Can't set up (won't retry; " + ex.getMessage() + ").");
        } else {

            Window.alert("Can't refresh (will retry; " + ex.getMessage() + ").");
        }
    }

    /**
     * Single-use init method.
     */
    private void init() {

        this.isAppActive = true;
    }

    /**
     * Is app active?
     *
     * @return Is app active?
     */
    public boolean isAppActive() {

        return this.isAppActive;
    }

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {

        try {

            // build standard widgets :)
            this.buildStandardWidgets();

            // run setup
            this.runSetup();

            // build refrhsh timer.
            this.buildRefreshTimer();

        } catch (final Throwable ex) {

            Window.alert("Can't set up (won't retry; " + ex.getMessage() + ").");
        }
    }

    /**
     * Run close window.
     */
    private void runCloseWindow() {

        final JobExecutorData executorData = this.getSelectedExecutorData();

        if (executorData != null) {

            ScriptWebApp.showWaitCursor();
            this.scriptService.removeExecutor(executorData,
                    new AsyncCallback<JobExecutorData>() {

                        @Override
                        public void onFailure(final Throwable arg0) {

                            ScriptWebApp.showDefaultCursor();
                            ScriptWebApp.this.handleError("remove executor",
                                    arg0);
                        }

                        @Override
                        public void onSuccess(final JobExecutorData arg0) {

                            ScriptWebApp.showDefaultCursor();
                            // ignore;
                        }
                    });
        }
    }

    /**
     * Run hide window.
     */
    private void runHideWindow() {

        final ExecutorTab executorTab = this.getSelectedExecutorTab();

        if (executorTab != null) {

            this.hiddenExecutorTabs.put(executorTab.getExecutorData(),
                    executorTab);
            this.visibleExecutorTabs.remove(executorTab.getExecutorData());

            this.tabLayoutPanel.remove(executorTab.getBorderPanel());
        }
    }

    /**
     * Create new executor.
     *
     * @param executorType Executor type.
     */
    private void runNewExecutor(final String executorTitle,
                                final JobExecutorType executorType) {

        JobDataUtils.checkEmptyString(executorTitle, true);
        JobDataUtils.checkNullObject(executorType, true);

        final JobExecutorData executorData = new JobExecutorData(executorTitle,
                executorType);

        ScriptWebApp.showWaitCursor();
        this.scriptService.addExecutor(executorData,
                new AsyncCallback<JobExecutorData>() {

                    @Override
                    public void onFailure(final Throwable arg0) {

                        ScriptWebApp.showDefaultCursor();
                        ScriptWebApp.this.handleError("create executor", arg0);
                    }

                    @Override
                    public void onSuccess(final JobExecutorData arg0) {

                        ScriptWebApp.showDefaultCursor();
                        // ignore;
                    }
                });
    }

    /**
     * Run refresh step 1.
     *
     * @throws Throwable Any exception (caller handles).
     */
    private void runRefresh() {

        this.scriptService
                .getAllExecutors(new AsyncCallback<JobExecutorData[]>() {

                    @Override
                    public void onFailure(final Throwable arg0) {

                        ScriptWebApp.this.handleUpdateError(arg0, false);
                    }

                    @Override
                    public void onSuccess(final JobExecutorData[] arg0) {

                        ScriptWebApp.this.runUpdateStep1(arg0, false);
                    }
                });
    }

    /**
     * Run setup step 1.
     *
     * @throws Throwable Any exception (caller handles).
     */
    private void runSetup() {

        // stuff
        this.currExecutors = new LinkedHashSet<>();
        this.nextExecutors = new LinkedHashSet<>();
        this.visibleExecutorTabs = new LinkedHashMap<>();
        this.hiddenExecutorTabs = new LinkedHashMap<>();
        this.currJobModifiedTime = 0L;
        this.nextJobModifiedTime = 0L;

        this.scriptService
                .getAllExecutors(new AsyncCallback<JobExecutorData[]>() {

                    @Override
                    public void onFailure(final Throwable arg0) {

                        ScriptWebApp.this.handleUpdateError(arg0, true);
                    }

                    @Override
                    public void onSuccess(final JobExecutorData[] arg0) {

                        ScriptWebApp.this.runUpdateStep1(arg0, true);
                    }
                });
    }

    /**
     * Run update step 1.
     *
     * @param foundExecutors Executors
     * @param isReset        True to clear and repopulate, false to update.
     */
    private void runUpdateStep1(final JobExecutorData[] foundExecutors,
                                final boolean isReset) {

        this.nextExecutors.clear();
        this.nextExecutors.addAll(Arrays.asList(foundExecutors));

        // add new currExecutors
        final Set<JobExecutorData> toAdd = new HashSet<>();

        toAdd.clear();
        toAdd.addAll(this.nextExecutors);
        toAdd.removeAll(this.currExecutors);

        for (final JobExecutorData item : toAdd) {

            final ExecutorTab newTab = new ExecutorTab(this,
                    this.scriptService, this.tabLayoutPanel, item);
            newTab.startUp();

            this.visibleExecutorTabs.put(item, newTab);
            this.statusTab.addExecutor(item, false);
        }

        // remove old currExecutors
        final Set<JobExecutorData> toRemove = new HashSet<>();

        toRemove.clear();
        toRemove.addAll(this.currExecutors);
        toRemove.removeAll(this.nextExecutors);

        for (final JobExecutorData item : toRemove) {

            ExecutorTab oldTab = this.visibleExecutorTabs.remove(item);

            if (oldTab == null) {

                oldTab = this.hiddenExecutorTabs.remove(item);
            }

            if (oldTab != null) {

                oldTab.cleanUp();
            }

            this.statusTab.removeExecutor(item, false);
        }

        this.currExecutors.clear();
        this.currExecutors.addAll(this.nextExecutors);

        if (!toAdd.isEmpty() || !toRemove.isEmpty()) {

            this.statusTab.updateExecutorDataGrids();
        }

        // wait a tick to pull the jobs.
        if (isReset) {

            this.scheduleRefreshTimer(true);

        } else {

            // get jobs
            this.scriptService.getAllJobs(this.currJobModifiedTime,
                    new AsyncCallback<JobContentData[]>() {

                        @Override
                        public void onFailure(final Throwable arg0) {

                            ScriptWebApp.this.handleUpdateError(arg0, false);
                        }

                        @Override
                        public void onSuccess(final JobContentData[] arg0) {

                            ScriptWebApp.this.runUpdateStep2(arg0, false);
                        }
                    });
        }
    }

    /**
     * Run update step 2.
     *
     * @param foundJobs New pending jobs.
     * @param isReset   True to clear and re-populate, false to update.
     */
    private void runUpdateStep2(final JobContentData[] foundJobs,
                                final boolean isReset) {

        final Map<JobExecutorData, ExecutorTab> toUpdate = new HashMap<JobExecutorData, ExecutorTab>();

        for (final JobContentData item : foundJobs) {

            this.nextJobModifiedTime = Math.max(this.nextJobModifiedTime,
                    item.getModifiedTime());
            final ExecutorTab executorTab = this.visibleExecutorTabs.get(item
                    .getExecutorData());

            if (item.getState() == JobState.completed) {

                if (executorTab != null) {

                    toUpdate.put(item.getExecutorData(), executorTab);
                    executorTab.addCompletedJob(item, false);
                }

                this.statusTab.addCompletedJob(item, false);

            } else {

                if (executorTab != null) {

                    toUpdate.put(item.getExecutorData(), executorTab);
                    executorTab.addPendingJob(item, false);
                }

                this.statusTab.addPendingJob(item, false);
            }
        }

        if (!toUpdate.isEmpty()) {

            for (final ExecutorTab item : toUpdate.values()) {

                item.updateJobDataGrids();
            }
        }

        if (foundJobs.length > 0) {

            this.statusTab.updateJobDataGrids();
        }

        // stash update time
        this.currJobModifiedTime = this.nextJobModifiedTime;

        // schedule timer
        this.scheduleRefreshTimer(true);
    }

    /**
     * Schedule timer.
     *
     * @param isToForce True to force a reschedule, false otherwise.
     */
    private void scheduleRefreshTimer(final boolean isToForce) {

        int delayInMS = ScriptWebApp.DEFAULT_REFRESH_TIMER_INTERVAL_MS;
        final long workClickTime = this.lastClickTime;

        if (workClickTime > 0L) {

            final long diffInMS = System.currentTimeMillis() - workClickTime;

            if (diffInMS > (60L * 1000L)) {

                delayInMS = 10 * 1000;

            } else if (diffInMS > (5L * 60L * 1000)) {

                delayInMS = 30 * 1000;

            } else if (diffInMS > (10L * 60L * 1000)) {

                delayInMS = 60 * 1000;
            }
        }

        if (this.isAppActive
                && (isToForce || (delayInMS != this.lastTimerDelayInMS))) {

            this.refreshTimer.schedule(delayInMS);
            this.lastTimerDelayInMS = delayInMS;
        }
    }

    /**
     * Select executor tab.
     *
     * @param executorData Executor data.
     * @param isToPromote  True to move executor to front of list, false otherwise.
     */
    public void selectExecutor(final JobExecutorData executorData,
                               final boolean isToPromote) {

        JobDataUtils.checkNullObject(executorData, true);

        // visible
        ExecutorTab executorTab = this.visibleExecutorTabs.get(executorData);
        LayoutPanel layoutPanel = null;

        if (executorTab != null) {

            layoutPanel = executorTab.getBorderPanel();

        } else {

            // hidden
            executorTab = this.hiddenExecutorTabs.get(executorData);

            if (executorTab != null) {

                layoutPanel = executorTab.getBorderPanel();

                this.visibleExecutorTabs.put(executorTab.getExecutorData(),
                        executorTab);
                this.hiddenExecutorTabs.remove(executorTab.getExecutorData());
            }
        }

        // swap
        if (layoutPanel != null) {

            this.tabLayoutPanel.remove(layoutPanel);
            this.tabLayoutPanel.insert(layoutPanel, executorTab.getTabTitle(),
                    1);

            this.tabLayoutPanel.selectTab(layoutPanel);
        }
    }

    /**
     * Set is app active?
     *
     * @param isAppActive True if app is active, false otherwise.
     */
    public void setAppActive(final boolean isAppActive) {

        this.isAppActive = isAppActive;
    }

    /**
     * Sets last click time.
     *
     * @param lastClickTime Last click time = (<1L = now).
     */
    public void setLastClickTime(final long lastClickTime) {

        long workLastClickTime = lastClickTime;

        if (workLastClickTime < 1L) {

            workLastClickTime = System.currentTimeMillis();
        }

        this.lastClickTime = workLastClickTime;
        this.scheduleRefreshTimer(false);
    }
}
