package org.entity;

import org.database.ReadDatabase;
import org.excel.WriteExcel;
import org.export.FineItemInfo;
import org.export.QueryResult;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import static org.database.Query.getQuery;

public class ExportFine implements ReadDatabase, WriteExcel {

    static ResourceBundle rb;
    private static final String selectPath;

    private static final String reportTypeFile = "report_type.sql";
    private static final String emitterFile = "emitter.sql";
    private static final String balanceFineItemFile = "balance_fine_item.sql";
    private static final String balancePureFineItemMatchFile = "balance_pure_fine_item_match.sql";

    private static final String singleDimItemFile = "single_dim_item.sql";
    private static final String singleDimPureFineItemMatchFile = "single_dim_pure_fine_item_match.sql";

    private static final String doubleDimHorizontalItemFile = "double_dim_horizontal_item.sql";
    private static final String doubleDimPureFineHorizontalItemMatchFile = "double_dim_pure_fine_horizontal_item_match.sql";

    private static final String doubleDimVerticalItemFile = "double_dim_vertical_item.sql";
    private static final String doubleDimPureFineVerticalItemMatchFile = "double_dim_pure_fine_vertical_item_match.sql";

    private List<String> emitterList = new ArrayList<>();

    // Map<report, List<FineItemInfo>>
    private Map<String, List<FineItemInfo>> fineItemInfoMap = new HashMap<>();

    // Map<report, Map<emitter, List<QeuryResult>>
    private Map<String, Map<String, List<QueryResult>>> queryResultMap = new HashMap<>();

    public List<String> getEmitterList() {
        return emitterList;
    }

    public Map<String, List<FineItemInfo>> getFineItemInfoMap() {
        return fineItemInfoMap;
    }

    public void setEmitterList(List<String> emitterList) {
        this.emitterList = emitterList;
    }

    public void setFineItemInfoMap(Map<String, List<FineItemInfo>> fineItemInfoMap) {
        this.fineItemInfoMap = fineItemInfoMap;
    }

    static {
        rb = ResourceBundle.getBundle("application");
        selectPath = Paths.get(rb.getString("sql_persist_directory"), "select").toString();
    }
    @Override
    public void executeAuxiliary(Connection connection) {}

    protected void exportEmitter(Object object) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(selectPath, emitterFile).toString());

        try (Statement statement = connection.createStatement()) {

            statement.execute(queryText);
            try (ResultSet resultSet = statement.getResultSet()) {
                while (resultSet.next()) {
                    List<String> emitterList = getEmitterList();
                    emitterList.add(resultSet.getString("emitter_name"));
                    setEmitterList(emitterList);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void exportFineItemBalance(Object object) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(selectPath, balanceFineItemFile).toString());

        try(Statement statement = connection.createStatement()) {

            statement.execute(queryText);
            try (ResultSet resultSet = statement.getResultSet()) {
                List<FineItemInfo> fineItemInfoList = new ArrayList<>();
                while (resultSet.next()) {
                    String fineItemCode = resultSet.getString("fine_item_code");
                    String fineItemName = resultSet.getString("fine_item_name");
                    String hierPureItemPath = resultSet.getString("hier_pure_item_path");
                    fineItemInfoList.add(new FineItemInfo(fineItemCode, fineItemName, hierPureItemPath));
                }
                if (!fineItemInfoList.isEmpty()) {
                    fineItemInfoMap.put("BALANCE", fineItemInfoList);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void exportPureFineItemMatchBalance(Object object) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(selectPath, balancePureFineItemMatchFile).toString());

        Map<String, List<QueryResult>> emitterQueryResultMap = new HashMap<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {

            for (String emitterName : emitterList) {
                preparedStatement.setString(1, emitterName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<QueryResult> queryResultList = new ArrayList<>();
                    while (resultSet.next()) {
                        String fineItemCode = resultSet.getString("fine_item_code");
                        String hierPureItemPath = resultSet.getString("hier_pure_item_path");
                        int level = resultSet.getInt("level");
                        int index = resultSet.getInt("ifb_index");
                        int cnt = resultSet.getInt("cnt");
                        String groupCnct = resultSet.getString("group_cnct");

                        QueryResult queryResult = new QueryResult(fineItemCode, hierPureItemPath, level, index, cnt, groupCnct);
                        queryResultList.add(queryResult);
                    }
                    if (!queryResultList.isEmpty()) {
                        emitterQueryResultMap.put(emitterName, queryResultList);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!emitterQueryResultMap.isEmpty()) {
            queryResultMap.put("BALANCE", emitterQueryResultMap);
        }

    }

    protected void exportFineItem(Object object, String queryPath) {

        Connection connection = checkConnection(object);

        String queryReportText = getQuery(Paths.get(selectPath, reportTypeFile).toString());
        String queryText = getQuery(Paths.get(selectPath, queryPath).toString());

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryReportText);
            PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
            while (resultSet.next()) {
                String reportTypeCode = resultSet.getString("report_type_code");
                preparedStatement.setString(1, reportTypeCode);
                List<FineItemInfo> fineItemInfoList = new ArrayList<>();
                try(ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String fineItemCode = resultSet.getString("fine_item_code");
                        String fineItemName = resultSet.getString("fine_item_name");
                        String hierPureItemPath = resultSet.getString("hier_pure_item_path");
                        fineItemInfoList.add(new FineItemInfo(fineItemCode, fineItemName, hierPureItemPath));
                    }
                }
                if (!fineItemInfoList.isEmpty()) {
                    fineItemInfoMap.put(reportTypeCode, fineItemInfoList);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void exportPureFineItemMatch(Object object, String queryPath) {

        Connection connection = checkConnection(object);

        String queryReportText = getQuery(Paths.get(selectPath, reportTypeFile).toString());
        String queryText = getQuery(Paths.get(selectPath, queryPath).toString());


        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryReportText);
            PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {

            while (resultSet.next()) {
                Map<String, List<QueryResult>> emitterQueryResultMap = new HashMap<>();
                String reportTypeCode = resultSet.getString("report_type_code");
                for (String emitterName : emitterList) {
                    preparedStatement.setString(1, reportTypeCode);
                    preparedStatement.setString(2, emitterName);

                    try (ResultSet rs = preparedStatement.executeQuery()) {
                        List<QueryResult> queryResultList = new ArrayList<>();
                        while (rs.next()) {
                            String fineItemCode = rs.getString("fine_item_code");
                            String hierPureItemPath = rs.getString("hier_pure_item_path");
                            int index = rs.getInt("if_index");
                            int cnt = rs.getInt("cnt");
                            String groupCnct = rs.getString("group_cnct");

                            QueryResult queryResult = new QueryResult(fineItemCode, hierPureItemPath, index, cnt, groupCnct);
                            queryResultList.add(queryResult);
                        }
                        if (!queryResultList.isEmpty()) {
                            emitterQueryResultMap.put(emitterName, queryResultList);
                        }
                    }
                }
                if (!emitterQueryResultMap.isEmpty()) {
                    this.queryResultMap.put(reportTypeCode, emitterQueryResultMap);
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void execute(Connection connection) {

        exportEmitter(connection);

        exportFineItemBalance(connection);
        exportPureFineItemMatchBalance(connection);

        exportFineItem(connection, singleDimItemFile);
        exportPureFineItemMatch(connection, singleDimPureFineItemMatchFile);

        exportFineItem(connection, doubleDimHorizontalItemFile);
        exportPureFineItemMatch(connection, doubleDimPureFineHorizontalItemMatchFile);

        exportFineItem(connection, doubleDimVerticalItemFile);
        exportPureFineItemMatch(connection, doubleDimPureFineVerticalItemMatchFile);

//        exportBalance(connection);
    }

    @Override
    public void readSource(String sourcePath) {
        execute(sourcePath);
    }

    @Override
    public void writeDestination(String destinationPath) {

    }

    ExportFine() {
        readSource(rb.getString("url_persist"));
    }

    public static void main(String[] args) throws IOException {
        new ExportFine();
    }
}
