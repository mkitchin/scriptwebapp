package com.opsysinc.scripting.client.widget;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.opsysinc.scripting.client.app.ScriptWebResources;
import com.opsysinc.scripting.shared.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Job widget utils.
 *
 * @author mkit
 */
public final class JobWidgetUtils {

    /**
     * Build job content result.
     *
     * @param contentData Content data.
     * @return Combined response/output text.
     */
    public static String buildJobResultText(final JobContentData contentData) {

        JobDataUtils.checkNullObject(contentData, true);

        final StringBuilder tempText = new StringBuilder();
        final String outputText = contentData.getOutputData().getOutputBody();

        if (!JobDataUtils.checkEmptyString(outputText, false)) {

            if (tempText.length() > 0) {

                tempText.append("\n");
            }

            tempText.append(outputText);
        }

        final String responseText = contentData.getResponseData()
                .getResponseBody();

        if (!JobDataUtils.checkEmptyString(responseText, false)) {

            if (tempText.length() > 0) {

                tempText.append("\n");
            }

            tempText.append(responseText);
        }

        return tempText.toString();
    }

    /**
     * Build data format list box.
     *
     * @return Data format list box.
     */
    public static ListBox createDataFormatListBox() {

        final ListBox result = new ListBox();

        for (final JobDataFormat item : JobDataFormat.values()) {

            result.addItem(item.getFormatTitle());
        }

        return result;
    }

    /**
     * Create job content data grid.
     *
     * @param input Input.
     * @return Data grid.
     */
    public static DataGrid<JobContentData> createJobContentDataGrid(
            final ListDataProvider<JobContentData> input) {

        JobDataUtils.checkNullObject(input, true);

        // DATAGRID
        final DataGrid<JobContentData> result = new DataGrid<>(
                JobWidgetUtils.JOB_CONTENT_KEY_PROVIDER);

        // BASICS

        // sort handler
        final ColumnSortEvent.ListHandler<JobContentData> sortHandler = new ColumnSortEvent.ListHandler<>(
                input.getList());
        result.addColumnSortHandler(sortHandler);

        // pager
        final SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, false,
                0, true);
        pager.setDisplay(result);

        // selection model
        final SingleSelectionModel<JobContentData> selectionModel = new SingleSelectionModel<>();
        result.setSelectionModel(selectionModel);

        // COLUMNS

        // date column
        final Column<JobContentData, Date> dateColumn = new Column<JobContentData, Date>(
                new DateCell(
                        DateTimeFormat
                                .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM))) {
            @Override
            public Date getValue(final JobContentData arg0) {

                return new Date(arg0.getModifiedTime());
            }
        };
        dateColumn.setSortable(true);
        sortHandler.setComparator(dateColumn, new Comparator<JobContentData>() {

            @Override
            public int compare(final JobContentData o1, final JobContentData o2) {
                return Long.compare(o1.getModifiedTime(), o2.getModifiedTime());
            }
        });
        dateColumn.setDefaultSortAscending(false);

        result.addColumn(dateColumn, "Time");
        result.setColumnWidth(dateColumn, 12.0, Style.Unit.EM);

        // request column
        final Column<JobContentData, String> requestColumn = new Column<JobContentData, String>(
                new TextCell()) {
            @Override
            public String getValue(final JobContentData arg0) {

                return JobWidgetUtils.formatCellText(arg0.getRequestData()
                        .getRequestBody(), true);
            }
        };
        requestColumn.setSortable(true);
        sortHandler.setComparator(requestColumn,
                new Comparator<JobContentData>() {

                    @Override
                    public int compare(final JobContentData o1,
                                       final JobContentData o2) {

                        final String tempText1 = JobWidgetUtils.formatCellText(
                                o1.getRequestData().getRequestBody(), true);
                        final String tempText2 = JobWidgetUtils.formatCellText(
                                o2.getRequestData().getRequestBody(), true);

                        return tempText1.compareTo(tempText2);
                    }
                });
        result.addColumn(requestColumn, "Input");

        // response column
        final Column<JobContentData, String> responseColumn = new Column<JobContentData, String>(
                new TextCell()) {
            @Override
            public String getValue(final JobContentData arg0) {

                return JobWidgetUtils.formatCellText(
                        JobWidgetUtils.buildJobResultText(arg0), true);
            }
        };
        responseColumn.setSortable(true);
        sortHandler.setComparator(responseColumn,
                new Comparator<JobContentData>() {

                    @Override
                    public int compare(final JobContentData o1,
                                       final JobContentData o2) {

                        final String tempText1 = JobWidgetUtils.formatCellText(
                                JobWidgetUtils.buildJobResultText(o1), true);
                        final String tempText2 = JobWidgetUtils.formatCellText(
                                JobWidgetUtils.buildJobResultText(o2), true);

                        return tempText1.compareTo(tempText2);
                    }
                });
        result.addColumn(responseColumn, "Output");

        // state column
        final Column<JobContentData, String> stateColumn = new Column<JobContentData, String>(
                new TextCell()) {
            @Override
            public String getValue(final JobContentData arg0) {

                return JobWidgetUtils.formatCellText(
                        Character.toUpperCase(arg0.getState().toString()
                                .charAt(0)), true);
            }
        };
        stateColumn.setSortable(true);
        sortHandler.setComparator(stateColumn,
                new Comparator<JobContentData>() {

                    @Override
                    public int compare(final JobContentData o1,
                                       final JobContentData o2) {

                        return o1.getState().compareTo(o2.getState());
                    }
                });
        result.addColumn(stateColumn, "State");
        result.setColumnWidth(stateColumn, 4.0, Style.Unit.EM);

        // status column
        final Column<JobContentData, String> statusColumn = new Column<JobContentData, String>(
                new TextCell()) {
            @Override
            public String getValue(final JobContentData arg0) {

                return JobWidgetUtils.formatCellText(
                        Character.toUpperCase(arg0.getStatus().toString()
                                .charAt(0)), true);
            }
        };
        statusColumn.setSortable(true);
        sortHandler.setComparator(statusColumn,
                new Comparator<JobContentData>() {

                    @Override
                    public int compare(final JobContentData o1,
                                       final JobContentData o2) {

                        return o1.getStatus().compareTo(o2.getStatus());
                    }
                });
        result.addColumn(statusColumn, "Status");
        result.setColumnWidth(statusColumn, 4.0, Style.Unit.EM);

        // from column
        final Column<JobContentData, String> fromColumn = new Column<JobContentData, String>(
                new TextCell()) {
            @Override
            public String getValue(final JobContentData arg0) {

                return JobWidgetUtils.formatCellText(arg0.getRequestData()
                        .getFromHost(), true);
            }
        };
        fromColumn.setSortable(true);
        sortHandler.setComparator(fromColumn, new Comparator<JobContentData>() {

            @Override
            public int compare(final JobContentData o1, final JobContentData o2) {

                return JobWidgetUtils.formatCellText(
                        o1.getRequestData().getFromHost(), true).compareTo(
                        JobWidgetUtils.formatCellText(o2.getRequestData()
                                .getFromHost(), true));
            }
        });
        result.addColumn(fromColumn, "From");
        result.setColumnWidth(fromColumn, 10.0, Style.Unit.EM);

        // STYLE
        result.setRowStyles(new RowStyles<JobContentData>() {

            @Override
            public String getStyleNames(final JobContentData arg0,
                                        final int arg1) {

                if (arg0.getStatus().equals(JobStatus.failure)) {

                    return JobWidgetUtils.DEFAULT_RED_TEXT_STYLE_NAME;

                } else if (arg0.getState().equals(JobState.started)) {

                    return JobWidgetUtils.DEFAULT_BLUE_TEXT_STYLE_NAME;

                } else {

                    return null;
                }
            }
        });

        // SETUP
        result.setAutoHeaderRefreshDisabled(true);
        result.setEmptyTableWidget(new Label(
                JobWidgetUtils.DEFAULT_EMPTY_DATAGRID_TEXT));
        result.getColumnSortList().push(dateColumn);
        input.addDataDisplay(result);

        return result;
    }

    /**
     * Create job executor data grid.
     *
     * @param input Input.
     * @return Data grid.
     */
    public static DataGrid<JobExecutorData> createJobExecutorDataGrid(
            final ListDataProvider<JobExecutorData> input) {

        JobDataUtils.checkNullObject(input, true);

        // DATAGRID
        final DataGrid<JobExecutorData> result = new DataGrid<>(
                JobWidgetUtils.JOB_EXECUTOR_KEY_PROVIDER);

        // BASICS

        // sort handler
        final ColumnSortEvent.ListHandler<JobExecutorData> sortHandler = new ColumnSortEvent.ListHandler<>(
                input.getList());
        result.addColumnSortHandler(sortHandler);

        // pager
        final SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, false,
                0, true);
        pager.setDisplay(result);

        // selection model
        final SingleSelectionModel<JobExecutorData> selectionModel = new SingleSelectionModel<>();
        result.setSelectionModel(selectionModel);

        // COLUMNS

        // date column
        final Column<JobExecutorData, Date> dateColumn = new Column<JobExecutorData, Date>(
                new DateCell(
                        DateTimeFormat
                                .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM))) {
            @Override
            public Date getValue(final JobExecutorData arg0) {

                return new Date(arg0.getModifiedTime());
            }
        };
        dateColumn.setSortable(true);
        sortHandler.setComparator(dateColumn,
                new Comparator<JobExecutorData>() {

                    @Override
                    public int compare(final JobExecutorData o1,
                                       final JobExecutorData o2) {
                        return Long.compare(o1.getModifiedTime(),
                                o2.getModifiedTime());
                    }
                });
        dateColumn.setDefaultSortAscending(false);

        result.addColumn(dateColumn, "Time");
        result.setColumnWidth(dateColumn, 12.0, Style.Unit.EM);

        // type column
        final Column<JobExecutorData, String> typeColumn = new Column<JobExecutorData, String>(
                new TextCell()) {
            @Override
            public String getValue(final JobExecutorData arg0) {

                return arg0.getType().getExecutorTitle();

            }
        };
        typeColumn.setSortable(true);
        sortHandler.setComparator(typeColumn,
                new Comparator<JobExecutorData>() {

                    @Override
                    public int compare(final JobExecutorData o1,
                                       final JobExecutorData o2) {

                        return o1.getType().getExecutorTitle()
                                .compareTo(o2.getType().getExecutorTitle());
                    }
                });
        result.addColumn(typeColumn, "Type");
        result.setColumnWidth(typeColumn, 12.0, Style.Unit.EM);

        // title column
        final Column<JobExecutorData, String> titleColumn = new Column<JobExecutorData, String>(
                new TextCell()) {
            @Override
            public String getValue(final JobExecutorData arg0) {

                return JobWidgetUtils.formatCellText(arg0.getTitle(), true);
            }
        };
        titleColumn.setSortable(true);
        sortHandler.setComparator(titleColumn,
                new Comparator<JobExecutorData>() {

                    @Override
                    public int compare(final JobExecutorData o1,
                                       final JobExecutorData o2) {

                        return JobWidgetUtils.formatCellText(o1.getTitle(),
                                true).compareTo(
                                JobWidgetUtils.formatCellText(o2.getTitle(),
                                        true));
                    }
                });
        result.addColumn(titleColumn, "Title");

        // SETUP
        result.setAutoHeaderRefreshDisabled(true);
        result.setEmptyTableWidget(new Label(
                JobWidgetUtils.DEFAULT_EMPTY_DATAGRID_TEXT));
        result.getColumnSortList().push(dateColumn);
        input.addDataDisplay(result);

        return result;
    }

    /**
     * Create job content data grid.
     *
     * @param input Input.
     * @return Data grid.
     */
    public static DataGrid<Map.Entry<String, String>> createNameValueDataGrid(
            final List<Map.Entry<String, String>> input) {

        JobDataUtils.checkNullObject(input, true);

        // DATAGRID
        final DataGrid<Map.Entry<String, String>> result = new DataGrid<>(
                JobWidgetUtils.NAME_VALUE_KEY_PROVIDER);

        // BASICS

        // sort handler
        final ColumnSortEvent.ListHandler<Map.Entry<String, String>> sortHandler = new ColumnSortEvent.ListHandler<>(
                input);
        result.addColumnSortHandler(sortHandler);

        // pager
        final SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, false,
                0, true);
        pager.setDisplay(result);

        // selection model
        final SingleSelectionModel<Map.Entry<String, String>> selectionModel = new SingleSelectionModel<>();
        result.setSelectionModel(selectionModel);

        // COLUMNS

        // name column
        final Column<Map.Entry<String, String>, String> nameColumn = new Column<Map.Entry<String, String>, String>(
                new TextCell()) {
            @Override
            public String getValue(final Map.Entry<String, String> arg0) {

                return JobWidgetUtils.formatCellText(arg0.getKey(), false);
            }
        };
        nameColumn.setSortable(true);
        sortHandler.setComparator(nameColumn,
                new Comparator<Map.Entry<String, String>>() {

                    @Override
                    public int compare(final Map.Entry<String, String> o1,
                                       final Map.Entry<String, String> o2) {

                        final String tempText1 = JobWidgetUtils.formatCellText(
                                o1.getKey(), false);
                        final String tempText2 = JobWidgetUtils.formatCellText(
                                o2.getKey(), false);

                        return tempText1.compareTo(tempText2);
                    }
                });

        result.addColumn(nameColumn, "Name");

        // value column
        final Column<Map.Entry<String, String>, String> valueColumn = new Column<Map.Entry<String, String>, String>(
                new TextCell()) {
            @Override
            public String getValue(final Map.Entry<String, String> arg0) {

                return JobWidgetUtils.formatCellText(arg0.getValue(), false);
            }
        };
        valueColumn.setSortable(true);
        sortHandler.setComparator(valueColumn,
                new Comparator<Map.Entry<String, String>>() {

                    @Override
                    public int compare(final Map.Entry<String, String> o1,
                                       final Map.Entry<String, String> o2) {

                        final String tempText1 = JobWidgetUtils.formatCellText(
                                o1.getValue(), false);
                        final String tempText2 = JobWidgetUtils.formatCellText(
                                o2.getValue(), false);

                        return tempText1.compareTo(tempText2);
                    }
                });
        result.addColumn(valueColumn, "Value");

        // SETUP
        result.setColumnWidth(nameColumn, 25, Style.Unit.PCT);
        result.setAutoHeaderRefreshDisabled(true);
        result.setEmptyTableWidget(new Label(
                JobWidgetUtils.DEFAULT_EMPTY_DATAGRID_TEXT));
        result.getColumnSortList().push(nameColumn);

        return result;
    }

    /**
     * Creates job file data grid.
     *
     * @param input File data provider.
     * @return Data grid.
     */
    public static DataGrid<JobFileData> createJobFileDataGrid(final ListDataProvider<JobFileData> input) {

        JobDataUtils.checkNullObject(input, true);

        // RESOURCES

        final ScriptWebResources scriptWebResources = GWT.create(ScriptWebResources.class);

        // DATAGRID

        final DataGrid<JobFileData> result = new DataGrid<>(
                JobWidgetUtils.JOB_FILE_KEY_PROVIDER);

        // BASICS

        // sort handler
        final ColumnSortEvent.ListHandler<JobFileData> sortHandler = new ColumnSortEvent.ListHandler<>(
                input.getList());
        result.addColumnSortHandler(sortHandler);

        // pager
        final SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, false,
                0, true);
        pager.setDisplay(result);

        // selection model
        final SingleSelectionModel<JobFileData> selectionModel = new SingleSelectionModel<>();
        result.setSelectionModel(selectionModel);

        // COLUMNS

        // icon column
        final Column<JobFileData, ImageResource> iconColumn = new Column<JobFileData, ImageResource>(new ImageResourceCell()) {

            @Override
            public ImageResource getValue(final JobFileData jobFileData) {

                ImageResource result = null;

                switch (jobFileData.getType()) {

                    case folder:
                        result = scriptWebResources.getFolderIconResource();
                        break;

                    case file:
                        result = scriptWebResources.getFileIconResource();
                        break;

                    default:
                        result = scriptWebResources.getDefaultIconResource();
                        break;
                }

                return result;
            }
        };
        iconColumn.setSortable(true);
        sortHandler.setComparator(iconColumn, new Comparator<JobFileData>() {

            @Override
            public int compare(final JobFileData o1, final JobFileData o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
        iconColumn.setDefaultSortAscending(false);

        result.addColumn(iconColumn, "Type");
        result.setColumnWidth(iconColumn, 6.0, Style.Unit.EM);

        // name column
        final Column<JobFileData, String> typeColumn = new Column<JobFileData, String>(
                new TextCell()) {
            @Override
            public String getValue(final JobFileData arg0) {
                return arg0.getName();
            }
        };
        typeColumn.setSortable(true);
        sortHandler.setComparator(typeColumn,
                new Comparator<JobFileData>() {

                    @Override
                    public int compare(final JobFileData o1,
                                       final JobFileData o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });

        result.addColumn(typeColumn, "Name");
        result.setColumnWidth(typeColumn, 18.0, Style.Unit.EM);

        // size column
        final Column<JobFileData, Number> sizeColumn = new Column<JobFileData, Number>(new NumberCell()) {
            @Override
            public Long getValue(final JobFileData arg0) {
                return arg0.getSize();
            }
        };

        sizeColumn.setSortable(true);
        sortHandler.setComparator(sizeColumn, new Comparator<JobFileData>() {

            @Override
            public int compare(final JobFileData o1, final JobFileData o2) {
                return Long.compare(o1.getModifiedTime(), o2.getModifiedTime());
            }
        });
        sizeColumn.setDefaultSortAscending(false);

        result.addColumn(sizeColumn, "Size");
        result.setColumnWidth(sizeColumn, 12.0, Style.Unit.EM);

        // time column
        final Column<JobFileData, Date> dateColumn = new Column<JobFileData, Date>(
                new DateCell(
                        DateTimeFormat
                                .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM))) {
            @Override
            public Date getValue(final JobFileData arg0) {
                return new Date(arg0.getModifiedTime());
            }
        };
        dateColumn.setSortable(true);
        sortHandler.setComparator(dateColumn, new Comparator<JobFileData>() {

            @Override
            public int compare(final JobFileData o1, final JobFileData o2) {
                return Long.compare(o1.getModifiedTime(), o2.getModifiedTime());
            }
        });
        dateColumn.setDefaultSortAscending(false);

        result.addColumn(dateColumn, "Date");
        result.setColumnWidth(dateColumn, 12.0, Style.Unit.EM);

        // SETUP
        result.setAutoHeaderRefreshDisabled(true);
        result.setEmptyTableWidget(new Label(
                JobWidgetUtils.DEFAULT_EMPTY_DATAGRID_TEXT));
        result.getColumnSortList().push(dateColumn);
        input.addDataDisplay(result);

        return result;
    }

    /**
     * Build cell text.
     *
     * @param input         Input.
     * @param isLimitLength True to limit length, false otherwise.
     * @return Formatted text.
     */
    public static String formatCellText(final Object input,
                                        final boolean isLimitLength) {

        String result = null;

        if (JobDataUtils.checkEmptyString(input, false)) {

            result = JobWidgetUtils.DEFAULT_EMPTY_CELL_TEXT;

        } else {

            result = input.toString().trim();

            if (isLimitLength
                    && (result.length() > JobWidgetUtils.DEFAULT_MAX_CELL_TEXT_LENGTH)) {

                result = result.substring(0,
                        JobWidgetUtils.DEFAULT_MAX_CELL_TEXT_LENGTH - 3)
                        + "...";
            }
        }

        return result;
    }

    /**
     * Job content key provider.
     */
    public static final ProvidesKey<JobContentData> JOB_CONTENT_KEY_PROVIDER;

    /**
     * Job content key provider.
     */
    public static final ProvidesKey<JobExecutorData> JOB_EXECUTOR_KEY_PROVIDER;

    /**
     * Job content key provider.
     */
    public static final ProvidesKey<JobFileData> JOB_FILE_KEY_PROVIDER;

    /**
     * Job content key provider.
     */
    public static final ProvidesKey<Map.Entry<String, String>> NAME_VALUE_KEY_PROVIDER;

    /**
     * Default red text style name.
     */
    public static final String DEFAULT_RED_TEXT_STYLE_NAME = "redText";

    /**
     * Default blue text style name.
     */
    public static final String DEFAULT_BLUE_TEXT_STYLE_NAME = "blueText";

    /**
     * Default empty cell text.
     */
    public static final String DEFAULT_EMPTY_CELL_TEXT = "(none)";
    ;

    /**
     * Default empty table text.
     */
    public static final String DEFAULT_EMPTY_DATAGRID_TEXT = JobWidgetUtils.DEFAULT_EMPTY_CELL_TEXT;
    ;

    /**
     * Default max cell text length.
     */
    public static final int DEFAULT_MAX_CELL_TEXT_LENGTH = 80;
    ;

    static {

        JOB_CONTENT_KEY_PROVIDER = new ProvidesKey<JobContentData>() {

            @Override
            public Object getKey(final JobContentData arg0) {

                return arg0.getId();
            }
        };

        JOB_EXECUTOR_KEY_PROVIDER = new ProvidesKey<JobExecutorData>() {

            @Override
            public Object getKey(final JobExecutorData arg0) {

                return arg0.getId();
            }
        };

        JOB_FILE_KEY_PROVIDER = new ProvidesKey<JobFileData>() {

            @Override
            public Object getKey(final JobFileData arg0) {

                return arg0.getId();
            }
        };

        NAME_VALUE_KEY_PROVIDER = new ProvidesKey<Map.Entry<String, String>>() {

            @Override
            public Object getKey(final Map.Entry<String, String> arg0) {

                return JobWidgetUtils.formatCellText(arg0.getKey(), true);
            }
        };
    }

    /**
     * Private ctor.
     */
    private JobWidgetUtils() {
    }
}
