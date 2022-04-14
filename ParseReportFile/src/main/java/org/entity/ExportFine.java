package org.entity;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.database.ReadDatabase;
import org.excel.WriteExcel;
import org.export.FineItemInfo;
import org.export.GroupItem;
import org.export.QueryResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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

    protected void readEmitter(Object object) {

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

    protected void readFineItemBalance(Object object) {

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

    protected void readPureFineItemMatchBalance(Object object) {

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

    protected void readFineItem(Object object, String queryPath, String postfix) {

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
                    fineItemInfoMap.put(reportTypeCode.concat(postfix), fineItemInfoList);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void readPureFineItemMatch(Object object, String queryPath, String postfix) {

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
                    this.queryResultMap.put(reportTypeCode.concat(postfix), emitterQueryResultMap);
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    protected void writeBalance(String reportPath) {

        final String reportTypeCode = "BALANCE";
        List<FineItemInfo> fineItemInfoList = fineItemInfoMap.get(reportTypeCode);
        Map<String, List<QueryResult>> emitterQueryResult = queryResultMap.get(reportTypeCode);
        String fileName = reportTypeCode.toLowerCase().concat(".xlsx");

        try (FileOutputStream file = new FileOutputStream(new File(Paths.get(reportPath, fileName).toString()))) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

            int rowNum = 0;
            Row headerRow = emitterListSheet.createRow(rowNum);
            headerRow.createCell(0).setCellValue("EmitterList");

            for (String emitterName : emitterList) {
                Row row = emitterListSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(emitterName);
            }

            XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

            rowNum = 0;
            headerRow = fineItemDictSheet.createRow(rowNum);

            headerRow.createCell(0).setCellValue("fine_item_code");
            headerRow.createCell(1).setCellValue("fine_item_name");
            headerRow.createCell(2).setCellValue("hier_pure_item_path");


            if (fineItemInfoList != null && !fineItemInfoList.isEmpty()) {
                for (FineItemInfo fineItemInfo : fineItemInfoList) {
                    Row row = fineItemDictSheet.createRow(++rowNum);
                    row.createCell(0).setCellValue(fineItemInfo.getFineItemCode());
                    row.createCell(1).setCellValue(fineItemInfo.getFineItemName());
                    row.createCell(2).setCellValue(fineItemInfo.getHierPureItemPath());
                }
            }
            for (String emitterName : emitterQueryResult.keySet()) {
                List<QueryResult> queryResultList = emitterQueryResult.get(emitterName);
                List<String> arrayList = queryResultList.stream()
                    .map(QueryResult::getGroupItemList)
                    .flatMap(Collection::stream)
                    .map(GroupItem::getReportDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                XSSFSheet emitterBalaceInfoSheet = workbook.createSheet(emitterName);

                rowNum = 0;

                Row row = emitterBalaceInfoSheet.createRow(rowNum);

                row.createCell(0).setCellValue("FineItemCode");
                row.createCell(1).setCellValue("HierPureItemPath");
                row.createCell(2).setCellValue("Level");
                row.createCell(3).setCellValue("Index");
                row.createCell(4).setCellValue("Count");

                for (int ind=0; ind < arrayList.size(); ++ind) {
                    row.createCell(5+ind).setCellValue(arrayList.get(ind));
                }

                for (int jnd=0; jnd < queryResultList.size(); ++jnd) {
                    Row newRow = emitterBalaceInfoSheet.createRow(jnd+1);
                    QueryResult queryResult = queryResultList.get(jnd);
                    newRow.createCell(0).setCellValue(queryResult.getFineItemCode());
                    newRow.createCell(1).setCellValue(queryResult.getHierPureItemPath());
                    newRow.createCell(2).setCellValue(queryResult.getLevel());
                    newRow.createCell(3).setCellValue(queryResult.getIndex());
                    newRow.createCell(4).setCellValue(queryResult.getCnt());

                    List<GroupItem> groupItemList = queryResult.getGroupItemList();
                    for (GroupItem groupItem : groupItemList) {
                        int knd = arrayList.indexOf(groupItem.getReportDate());
                        String groupItemString = groupItem.getIdList()
                            .stream()
                            .map(p -> p.toString())
                            .collect(Collectors.joining(";"));
                        newRow.createCell(5 + knd).setCellValue(groupItemString);
                    }
                }
            }
            workbook.write(file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    protected void writeCommonReport(String reportPath) {

        for (String reportCode : queryResultMap.keySet()) {
            if (reportCode.equals("BALANCE")) {
                continue;
            }
            List<FineItemInfo> fineItemInfoList = fineItemInfoMap.get(reportCode);
            Map<String, List<QueryResult>> emitterQueryResult = queryResultMap.get(reportCode);

            String fileName = reportCode.toLowerCase().concat(".xlsx");
            try (FileOutputStream file = new FileOutputStream(new File(Paths.get(reportPath, fileName).toString()))) {

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

                int rowNum = 0;
                Row headerRow = emitterListSheet.createRow(rowNum);
                headerRow.createCell(0).setCellValue("EmitterList");

                for (String emitterName : emitterList) {
                    Row row = emitterListSheet.createRow(++rowNum);
                    row.createCell(0).setCellValue(emitterName);
                }

                XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

                rowNum = 0;
                headerRow = fineItemDictSheet.createRow(rowNum);

                headerRow.createCell(0).setCellValue("fine_item_code");
                headerRow.createCell(1).setCellValue("fine_item_name");
                headerRow.createCell(2).setCellValue("hier_pure_item_path");

                if (fineItemInfoList != null && !fineItemInfoList.isEmpty()) {
                    for (FineItemInfo fineItemInfo : fineItemInfoList) {
                        Row row = fineItemDictSheet.createRow(++rowNum);
                        row.createCell(0).setCellValue(fineItemInfo.getFineItemCode());
                        row.createCell(1).setCellValue(fineItemInfo.getFineItemName());
                        row.createCell(2).setCellValue(fineItemInfo.getHierPureItemPath());
                    }
                }

                for (String emitterName : emitterQueryResult.keySet()) {
                    List<QueryResult> queryResultList = emitterQueryResult.get(emitterName);
                    List<String> arrayList = queryResultList.stream()
                        .map(QueryResult::getGroupItemList)
                        .flatMap(Collection::stream)
                        .map(GroupItem::getReportDate)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                    XSSFSheet emitterBalaceInfoSheet = workbook.createSheet(emitterName);

                    rowNum = 0;

                    Row row = emitterBalaceInfoSheet.createRow(rowNum);

                    row.createCell(0).setCellValue("FineItemCode");
                    row.createCell(1).setCellValue("HierPureItemPath");
                    row.createCell(2).setCellValue("Index");
                    row.createCell(3).setCellValue("Count");

                    for (int ind=0; ind < arrayList.size(); ++ind) {
                        row.createCell(4+ind).setCellValue(arrayList.get(ind));
                    }

                    for (int jnd=0; jnd < queryResultList.size(); ++jnd) {
                        Row newRow = emitterBalaceInfoSheet.createRow(jnd+1);
                        QueryResult queryResult = queryResultList.get(jnd);
                        newRow.createCell(0).setCellValue(queryResult.getFineItemCode());
                        newRow.createCell(1).setCellValue(queryResult.getHierPureItemPath());
                        newRow.createCell(2).setCellValue(queryResult.getIndex());
                        newRow.createCell(3).setCellValue(queryResult.getCnt());

                        List<GroupItem> groupItemList = queryResult.getGroupItemList();
                        for (GroupItem groupItem : groupItemList) {
                            int knd = arrayList.indexOf(groupItem.getReportDate());
                            String groupItemString = groupItem.getIdList()
                                .stream()
                                .map(p -> p.toString())
                                .collect(Collectors.joining(";"));
                            newRow.createCell(4 + knd).setCellValue(groupItemString);
                        }
                    }
                }
                workbook.write(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void execute(Connection connection) {

        readEmitter(connection);

        readFineItemBalance(connection);
        readPureFineItemMatchBalance(connection);

        readFineItem(connection, singleDimItemFile, "");
        readPureFineItemMatch(connection, singleDimPureFineItemMatchFile, "");

        readFineItem(connection, doubleDimHorizontalItemFile, "_HORIZONTAL");
        readPureFineItemMatch(connection, doubleDimPureFineHorizontalItemMatchFile, "_HORIZONTAL");

        readFineItem(connection, doubleDimVerticalItemFile, "_VERTICAL");
        readPureFineItemMatch(connection, doubleDimPureFineVerticalItemMatchFile, "_VERTICAL");

//        exportBalance(connection);
    }

    @Override
    public void readSource(String sourcePath) {
        execute(sourcePath);
    }

    @Override
    public void writeDestination(String destinationPath) {
        writeBalance(destinationPath);
        writeCommonReport(destinationPath);
    }

    ExportFine() {
        readSource(rb.getString("url_persist"));
        writeDestination(rb.getString("export_directory"));
    }

    public static void main(String[] args) {
        new ExportFine();
    }
}
