package org.entity;

import org.database.WriteDatabase;
import org.example.item.ItemInfo;
import org.example.sheet.*;
import org.excel.ReadExcelReport;


import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static org.database.Query.getQuery;

public class LoadTemp extends ReadExcelReport implements WriteDatabase {

    static ResourceBundle rb;
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String insertPath;

    private static final String refInfoFile = "tmp_ref_info.sql";

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

    protected void clearTempTableBalanceInfo(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, refInfoFile);

    }

    protected void insertTempTableBalanceInfo(Object object) {

        Connection connection = checkConnection(object);

        {
            String queryText = getQuery(Paths.get(insertPath, refInfoFile).toString());

            try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
                for (ItemInfo itemInfo : getBalanceRichSheetInfo().getBalanceItemInfoList()) {
                    preparedStatement.setString(1, getEmitterName());
                    preparedStatement.setString(2, getFileName());
                    preparedStatement.setLong(3, itemInfo.getItemIndex());
                    preparedStatement.setString(4, itemInfo.getItemName());
                    preparedStatement.setString(5, itemInfo.getItemPureName());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {
/*            String queryText = getQuery(Paths.get(insertPath, refInfoFile).toString());

            try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
                for (Integer ind : getBalanceRichSheetInfo().getReportInfoMap().keySet()) {
                    preparedStatement.setString(1, getEmitterName());
                    preparedStatement.setString(2, getFileName());
                    preparedStatement.setLong(3, itemInfo.getItemIndex());
                    preparedStatement.setString(4, itemInfo.getItemName());
                    preparedStatement.setString(5, itemInfo.getItemPureName());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }*/
        }
        String queryText = getQuery(Paths.get(insertPath, refInfoFile).toString());

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {

            preparedStatement.setString(1, getEmitterName());
            preparedStatement.setString(2, getFileName());
            preparedStatement.setString(3, getRefSheetInfo().getReportPeriod());
            preparedStatement.setString(4, dateFormat.format(getRefSheetInfo().getFileDate()));
            preparedStatement.setString(5, dateFormat.format(getRefSheetInfo().getPublishDate()));
            preparedStatement.setString(6, getRefSheetInfo().getAuditor());
            preparedStatement.setLong(7, getRefSheetInfo().getFactor());
            preparedStatement.setString(8, getRefSheetInfo().getCurrency());
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void executeAuxiliary(Connection connection) {}

    @Override
    public void writeDestination(Connection connection) {

        clearTempTableRefInfo(connection);
        insertTempTableRefInfo(connection);

    }

    public LoadTemp(String sourcePath) {
        super(sourcePath);

        BalanceRichSheetInfo balanceRichSheetInfo = new BalanceRichSheetInfo(getBalanceRawSheetInfo());
        setBalanceRichSheetInfo(getBalanceRichSheetInfo());


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
