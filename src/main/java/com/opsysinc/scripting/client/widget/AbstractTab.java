package com.opsysinc.scripting.client.widget;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.opsysinc.scripting.client.app.ScriptWebApp;
import com.opsysinc.scripting.client.service.ScriptServiceAsync;

/**
 * Abstract tab.
 *
 * @author mkitchin
 */
public abstract class AbstractTab {

    /**
     * Default refresh timer interval in milliseconds.
     */
    public static final int DEFAULT_REFRESH_TIMER_INTERVAL_MS = Math.max(1000,
            ScriptWebApp.DEFAULT_REFRESH_TIMER_INTERVAL_MS - 1000);

    /**
     * Script service.
     */
    private final ScriptServiceAsync scriptService;

    /**
     * Parent panel.
     */
    private final TabLayoutPanel parentPanel;

    /**
     * Border panel.
     */
    private LayoutPanel borderPanel;

    /**
     * Refresh timer.
     */
    private Timer refreshTimer;

    /**
     * Title.
     */
    private final String tabTitle;

    /**
     * Script web app.
     */
    private final ScriptWebApp scriptWebApp;

    /**
     * Basic ctor.
     *
     * @param scriptService Script service.
     * @param parentPanel   Parent panel.
     */
    public AbstractTab(final ScriptWebApp scriptWebApp,
                       final ScriptServiceAsync scriptService,
                       final TabLayoutPanel parentPanel, final String tabTitle) {

        this.scriptWebApp = scriptWebApp;
        this.scriptService = scriptService;
        this.parentPanel = parentPanel;
        this.tabTitle = tabTitle;
    }

    /**
     * Cleans up.
     */
    public void cleanUp() {

        if (this.borderPanel == null) {

            return;
        }

        this.runCleanUp();
        this.parentPanel.remove(this.borderPanel);

        if (this.refreshTimer != null) {

            try {

                this.refreshTimer.cancel();

            } catch (final Throwable ignore) {

                // ignore;
            }

            this.refreshTimer = null;
        }
    }

    /**
     * Gets border panel.
     *
     * @return Border panel.
     */
    public LayoutPanel getBorderPanel() {

        return this.borderPanel;
    }

    /**
     * Gets parent panel.
     *
     * @return Parent panel.
     */
    public TabLayoutPanel getParentPanel() {

        return this.parentPanel;
    }

    /**
     * Gets refresh timer.
     *
     * @return Refresh timer.
     */
    public Timer getRefreshTimer() {

        return this.refreshTimer;
    }

    /**
     * Gets script service.
     *
     * @return Script service.
     */
    public ScriptServiceAsync getScriptService() {

        return this.scriptService;
    }

    /**
     * Gets script web app.
     *
     * @return Script web app.
     */
    public ScriptWebApp getScriptWebApp() {

        return this.scriptWebApp;
    }

    /**
     * Gets tab index.
     *
     * @return Tab index.
     */
    public int getTabIndex() {

        return this.parentPanel.getWidgetIndex(this.borderPanel);
    }

    /**
     * Gets tabTitle.
     *
     * @return Title.
     */
    public String getTabTitle() {

        return this.tabTitle;
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
     * @param ex         Setup error.
     */
    protected void handleNotice(final String noticeText) {

        Window.alert("Notice: " + noticeText);
    }

    /**
     * Is this an active tab?
     *
     * @return True if this is an active tab, false otherwise.
     */
    public boolean isActiveTab() {

        boolean result = false;
        final int tabIndex = this.parentPanel.getWidgetIndex(this.borderPanel);

        if (tabIndex == -1) {

            return result;
        }

        result = this.parentPanel.getSelectedIndex() == tabIndex;
        return result;
    }

    /**
     * Run clean up.
     */
    protected void runCleanUp() {

    }

    /**
     * Run refresh override.
     */
    protected void runRefresh() {
    }

    /**
     * Run startup override.
     */
    protected void runStartUp() {
    }

    /**
     * Starts up.
     */
    public void startUp() {

        if (this.borderPanel != null) {

            return;
        }

        this.borderPanel = new LayoutPanel();
        this.parentPanel.add(this.borderPanel, this.tabTitle);

        this.runStartUp();

        this.refreshTimer = new Timer() {

            @Override
            public void run() {

                try {

                    AbstractTab.this.runRefresh();

                } catch (final Throwable ex) {

                    Window.alert("Can't refresh (will retry; "
                            + ex.getMessage() + ").");
                }
            }
        };

        this.refreshTimer
                .scheduleRepeating(AbstractTab.DEFAULT_REFRESH_TIMER_INTERVAL_MS);
    }
}
