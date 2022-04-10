package org.entity;

import org.database.WriteDatabase;
import org.example.item.BalanceItemInfo;
import org.example.item.DoubleDimensionItemInfo;
import org.example.item.ItemInfo;
import org.example.item.SingleDimensionItemInfo;
import org.example.report.PeriodReportInfo;
import org.example.report.ReportInfo;
import org.example.report.SnapshotReportInfo;
import org.example.sheet.*;
import org.excel.ReadExcelReport;


import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static org.database.Query.getQuery;

public class LoadTemp extends ReadExcelReport implements WriteDatabase {

    static ResourceBundle rb;
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String insertPath;

    private static final String refInfoFile = "tmp_ref_info.sql";

    private static final String balanceItemFile = "tmp_balance_item.sql";
    private static final String balanceStatisticFile = "tmp_balance_statistic_snapshot.sql";

    private static final String singleDimensionItemFile = "tmp_single_dimension_item.sql";
    private static final String singleDimensionStatisticFile = "tmp_single_dimension_statistic_period.sql";

    private static final String doubleDimensionItemFile = "tmp_double_dimension_item.sql";
    private static final String doubleDimensionStatisticFile = "tmp_double_dimension_statistic_period.sql";

    static {
        rb = ResourceBundle.getBundle("application");
        insertPath = Paths.get(rb.getString("sql_temp_directory"), "insert").toString();
    }

    protected void clearTempTableRefInfo(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, refInfoFile);

    }

    protected void insertTempTableRefInfo(Object object) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(insertPath, refInfoFile).toString());

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
            preparedStatement.setString(1, getEmitterName());
            preparedStatement.setString(2, getFileName());
            preparedStatement.setString(3, getRefSheetInfo().getField());
            preparedStatement.setString(4, getRefSheetInfo().getReportPeriod());
            preparedStatement.setString(5, dateFormat.format(getRefSheetInfo().getFileDate()));
            preparedStatement.setString(6, dateFormat.format(getRefSheetInfo().getPublishDate()));
            preparedStatement.setString(7, getRefSheetInfo().getAuditor());
            preparedStatement.setLong(8, getRefSheetInfo().getFactor());
            preparedStatement.setString(9, getRefSheetInfo().getCurrency());
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    protected void clearTempTableBalanceItem(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, balanceItemFile);

    }

    protected void clearTempTableBalanceStatistic(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, balanceStatisticFile);

    }

    protected void insertTempTableBalance(Object object) {

        Connection connection = checkConnection(object);

        {
            String queryText = getQuery(Paths.get(insertPath, balanceItemFile).toString());

            try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
                for (BalanceItemInfo itemInfo : getBalanceRichSheetInfo().getBalanceItemInfoList()) {
                    preparedStatement.setString(1, getEmitterName());
                    preparedStatement.setString(2, getFileName());
                    preparedStatement.setLong(3, itemInfo.getItemIndex());
                    preparedStatement.setLong(4, itemInfo.getParentItemIndex());
                    preparedStatement.setLong(5, itemInfo.getItemLevel());
                    preparedStatement.setString(6, itemInfo.getItemHeaderFlag() ? "HEADER" : "NOT_HEADER");
                    preparedStatement.setString(7, itemInfo.getItemSubtotalFlag() ? "SUBTOTAL" : "NOT_SUBTOTAL");
                    preparedStatement.setString(8, itemInfo.getItemName());
                    preparedStatement.setString(9, itemInfo.getItemPureName());
                    preparedStatement.execute();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {
            String queryText = getQuery(Paths.get(insertPath, balanceStatisticFile).toString());

            try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
                for (Integer ind : getBalanceRichSheetInfo().getReportInfoMap().keySet()) {
                    List<SnapshotReportInfo> reportInfoList = getBalanceRichSheetInfo().getReportInfoMap().get(ind);
                    for (SnapshotReportInfo reportInfo : reportInfoList) {
                        preparedStatement.setString(1, getEmitterName());
                        preparedStatement.setString(2, getFileName());
                        preparedStatement.setInt(3, ind);
                        preparedStatement.setString(4, dateFormat.format(reportInfo.getReportDate()));
                        preparedStatement.setLong(5, reportInfo.getReportValue());
                        preparedStatement.execute();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void clearTempTableSingleDimensionItem(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, singleDimensionItemFile);

    }

    protected void clearTempTableSingleDimensionStatistic(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, singleDimensionStatisticFile);

    }

    protected void insertTempTableSingleDimension(Object object) {

        Connection connection = checkConnection(object);

        {
            String queryTextItem = getQuery(Paths.get(insertPath, singleDimensionItemFile).toString());
            String queryTextStatistic = getQuery(Paths.get(insertPath, singleDimensionStatisticFile).toString());

            try (PreparedStatement preparedStatementItem = connection.prepareStatement(queryTextItem);
                 PreparedStatement preparedStatementStatistic = connection.prepareStatement(queryTextStatistic)) {
                for (String reportCode : getSingleDimensionRichSheetInfoMap().keySet()) {
                    SingleDimensionRichSheetInfo sheetInfo = getSingleDimensionRichSheetInfoMap().get(reportCode);
                    for (SingleDimensionItemInfo itemInfo : sheetInfo.getItemInfoList()) {
                        preparedStatementItem.setString(1, getEmitterName());
                        preparedStatementItem.setString(2, getFileName());
                        preparedStatementItem.setString(3, reportCode);
                        preparedStatementItem.setInt(4, itemInfo.getItemInfo().getItemIndex());
                        preparedStatementItem.setString(5, itemInfo.getItemInfo().getItemName());
                        preparedStatementItem.setString(6, itemInfo.getItemInfo().getItemPureName());
                        preparedStatementItem.execute();
                    }

                    for (int ind : sheetInfo.getReportInfoMap().keySet()) {
                        List<PeriodReportInfo> reportInfoList = sheetInfo.getReportInfoMap().get(ind);
                        for (PeriodReportInfo reportInfo : reportInfoList) {
                            preparedStatementStatistic.setString(1, getEmitterName());
                            preparedStatementStatistic.setString(2, getFileName());
                            preparedStatementStatistic.setString(3, reportCode);
                            preparedStatementStatistic.setInt(4, ind);
                            preparedStatementStatistic.setString(5, dateFormat.format(reportInfo.getReportBeginDate()));
                            preparedStatementStatistic.setString(6, dateFormat.format(reportInfo.getReportEndDate()));
                            preparedStatementStatistic.setLong(7, reportInfo.getReportValue());
                            preparedStatementStatistic.execute();
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void clearTempTableDoubleDimensionItem(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, doubleDimensionItemFile);

    }

    protected void clearTempTableDoubleDimensionStatistic(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, doubleDimensionStatisticFile);

    }

    protected void insertTempTableDoubleDimension(Object object) {

        Connection connection = checkConnection(object);

        {
            String queryTextItem = getQuery(Paths.get(insertPath, doubleDimensionItemFile).toString());
            String queryTextStatistic = getQuery(Paths.get(insertPath, doubleDimensionStatisticFile).toString());

            try (PreparedStatement preparedStatementItem = connection.prepareStatement(queryTextItem);
                 PreparedStatement preparedStatementStatistic = connection.prepareStatement(queryTextStatistic)) {
                for (String reportCode : getDoubleDimensionRichSheetInfoMap().keySet()) {
                    DoubleDimensionRichSheetInfo sheetInfo = getDoubleDimensionRichSheetInfoMap().get(reportCode);
                    for (DoubleDimensionItemInfo itemInfo : sheetInfo.getItemInfoList()) {
                        preparedStatementItem.setString(1, getEmitterName());
                        preparedStatementItem.setString(2, getFileName());
                        preparedStatementItem.setString(3, reportCode);
                        preparedStatementItem.setInt(4, itemInfo.getHorizontalItemInfo().getItemIndex());
                        preparedStatementItem.setString(5, itemInfo.getHorizontalItemInfo().getItemName());
                        preparedStatementItem.setString(6, itemInfo.getHorizontalItemInfo().getItemPureName());
                        preparedStatementItem.setInt(7, itemInfo.getVerticalItemInfo().getItemIndex());
                        preparedStatementItem.setString(8, itemInfo.getVerticalItemInfo().getItemName());
                        preparedStatementItem.setString(9, itemInfo.getVerticalItemInfo().getItemPureName());
                        preparedStatementItem.execute();
                    }

                    for (int ind : sheetInfo.getReportInfoMap().keySet()) {
                        Map<Integer, List<PeriodReportInfo>> singleDimReportInfoMap = sheetInfo.getReportInfoMap().get(ind);
                        for (int jnd : singleDimReportInfoMap.keySet()) {
                            List<PeriodReportInfo> reportInfoList = singleDimReportInfoMap.get(jnd);
                            for (PeriodReportInfo reportInfo : reportInfoList) {
                                preparedStatementStatistic.setString(1, getEmitterName());
                                preparedStatementStatistic.setString(2, getFileName());
                                preparedStatementStatistic.setString(3, reportCode);
                                preparedStatementStatistic.setInt(4, ind);
                                preparedStatementStatistic.setInt(5, jnd);
                                preparedStatementStatistic.setString(6, dateFormat.format(reportInfo.getReportBeginDate()));
                                preparedStatementStatistic.setString(7, dateFormat.format(reportInfo.getReportEndDate()));
                                preparedStatementStatistic.setLong(8, reportInfo.getReportValue());
                                preparedStatementStatistic.execute();
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void executeAuxiliary(Connection connection) {}

    @Override
    public void writeDestination(Connection connection) {

        clearTempTableRefInfo(connection);
        insertTempTableRefInfo(connection);

        clearTempTableBalanceItem(connection);
        clearTempTableBalanceStatistic(connection);
        insertTempTableBalance(connection);

        clearTempTableSingleDimensionItem(connection);
        clearTempTableSingleDimensionStatistic(connection);
        insertTempTableSingleDimension(connection);

        clearTempTableDoubleDimensionItem(connection);
        clearTempTableDoubleDimensionStatistic(connection);
        insertTempTableDoubleDimension(connection);

    }

    public LoadTemp(String sourcePath) {
        super(sourcePath);

        BalanceRichSheetInfo balanceRichSheetInfo = new BalanceRichSheetInfo(getBalanceRawSheetInfo());
        setBalanceRichSheetInfo(balanceRichSheetInfo);


        Map<String, SingleDimensionRawSheetInfo> rawMap1 = getSingleDimensionRawSheetInfoMap();
        Map<String, SingleDimensionRichSheetInfo> singleDimensionRichSheetInfoMap = new HashMap<>();

        for (String key : rawMap1.keySet()) {
            singleDimensionRichSheetInfoMap.put(key, new SingleDimensionRichSheetInfo(rawMap1.get(key)));
        }
        setSingleDimensionRichSheetInfoMap(singleDimensionRichSheetInfoMap);


        Map<String, DoubleDimensionRawSheetInfo> rawMap2 = getDoubleDimensionRawSheetInfoMap();
        Map<String, DoubleDimensionRichSheetInfo> doubleDimensionRichSheetInfoMap = new HashMap<>();

        for (String key : rawMap2.keySet()) {
            doubleDimensionRichSheetInfoMap.put(key, new DoubleDimensionRichSheetInfo(rawMap2.get(key)));
        }
        setDoubleDimensionRichSheetInfoMap(doubleDimensionRichSheetInfoMap);

        writeDestination(rb.getString("url_temp"));
    }
}
