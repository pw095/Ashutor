package org.entity;

import org.database.WriteDatabase;
import org.example.item.BalanceItemInfo;
import org.example.item.DoubleDimensionItemInfo;
import org.example.item.SingleDimensionItemInfo;
import org.example.report.PeriodReportInfo;
import org.example.report.SnapshotReportInfo;
import org.example.sheet.*;
import org.excel.ReadExcelFine;
import org.excel.ReadExcelReport;
import org.export.GroupItem;
import org.export.QueryResult;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static org.database.Query.getQuery;

public class LoadFineTemp extends ReadExcelFine implements WriteDatabase {

    static ResourceBundle rb;
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:s");

    private static final String insertPath;

    private static final String pureFineItemMatchFile = "tmp_pure_fine_item_match.sql";

    static {
        rb = ResourceBundle.getBundle("application");
        insertPath = Paths.get(rb.getString("sql_temp_directory"), "insert").toString();
    }

    protected void clearTempPureFineItemMatch(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, pureFineItemMatchFile);

    }

    protected void insertTempPureFineItemMatch(Object object, LocalDateTime localDateTime) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(insertPath, pureFineItemMatchFile).toString());

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
            for (String reportTypeCode : getQueryResultMap().keySet()) {
                Map<String, List<QueryResult>> emitterQueryResultMap = getQueryResultMap().get(reportTypeCode);
                for (String emitterName : emitterQueryResultMap.keySet()) {
                    List<QueryResult> queryResultList = emitterQueryResultMap.get(emitterName);
                    for (QueryResult queryResult : queryResultList) {
                        for (GroupItem groupItem : queryResult.getGroupItemList()) {
                            preparedStatement.setString(1, reportTypeCode);
                            preparedStatement.setString(2, emitterName);
                            preparedStatement.setString(3, queryResult.getFineItemCode());
                            preparedStatement.setInt(4, groupItem.getId());
                            preparedStatement.setString(5, groupItem.getReportDate());
                            preparedStatement.setString(6, dateTimeFormat.format(localDateTime));
                            preparedStatement.addBatch();
                        }
                    }
                }
            }
            preparedStatement.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void executeAuxiliary(Connection connection) {}

    @Override
    public void execute(Connection connection) {

        LocalDateTime localDateTime = LocalDateTime.now();

        clearTempPureFineItemMatch(connection);
        insertTempPureFineItemMatch(connection, localDateTime);

    }
    public void writeDestination(String path) {
        execute(path);
    }

    public LoadFineTemp(String sourcePath) {
        writeDestination(sourcePath);
    }

    public static void main(String[] args) {
        new LoadFineTemp(rb.getString("url_temp"));
    }
}
