package org.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.export.GroupItem;
import org.export.QueryResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


public class ReadExcelFine implements ReadExcel {

    static ResourceBundle rb;

    private Map<String, Map<String, List<QueryResult>>> queryResultMap = new HashMap<>();

    public Map<String, Map<String, List<QueryResult>>> getQueryResultMap() {
        return queryResultMap;
    }

    public void setQueryResultMap(Map<String, Map<String, List<QueryResult>>> queryResultMap) {
        this.queryResultMap = queryResultMap;
    }

    static {
        rb = ResourceBundle.getBundle("application");
    }

    private void readBalanceReport(String reportPath) {

        try (FileInputStream inputStream = new FileInputStream(new File(Paths.get(reportPath, "balance.xlsx").toString()))) {

            Map<String, List<QueryResult>> emitterQueryResultMap = new HashMap<>();

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("Emitter List") || workSheet.getSheetName().equals("Fine Item Dictionary")) {
                    continue;
                }

                String emitterName = null;
                List<QueryResult> queryResultList = new ArrayList<>();
                int maxColumnIndex = 0;
                Row firstRow = workSheet.getRow(0);

                for (Cell cell : firstRow) {
                    maxColumnIndex = Math.max(maxColumnIndex, cell.getColumnIndex());
                }

                for (Row row : workSheet) {

                    emitterName = workSheet.getSheetName();
                    QueryResult queryResult = new QueryResult();
                    List<GroupItem> groupItemList = new ArrayList<>();

                    if (row.getRowNum() > 0) {

                        for (int columnIndex = 0; columnIndex <= maxColumnIndex; ++columnIndex) {
                            Cell cell = row.getCell(columnIndex);
                            try {
                                switch (columnIndex) {
                                    case 0: // FineItem
                                        String stringCellValue = null;
                                        try {
                                            stringCellValue = getStringCellValue(cell);
                                        } catch (NullPointerException e) {}
                                        queryResult.setFineItemCode(stringCellValue != null && !stringCellValue.isEmpty() ? stringCellValue : "TECH$BLANC");
                                        break;
                                    case 1: // HierPureItemPath
                                        queryResult.setHierPureItemPath(getStringCellValue(cell));
                                        break;/*
                                    case 2: // Level
                                        queryResult.setLevel(getIntCellValue(cell));
                                        break;*/
                                    case 2: // Index
                                        queryResult.setIndex(getIntCellValue(cell));
                                        break;
                                    case 3: // Count
                                        queryResult.setCnt(getIntCellValue(cell));
                                        break;
                                    default: // Показатели
                                        groupItemList.add(new GroupItem(getStringCellValue(firstRow.getCell(columnIndex)), getIntCellValue(cell)));
                                        break;
                                }
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }
                        queryResult.setGroupItemList(groupItemList);
                        queryResultList.add(queryResult);
                    }
                }
                emitterQueryResultMap.put(emitterName, queryResultList);

            }
            queryResultMap.put("BALANCE", emitterQueryResultMap);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void readCommonReport(String reportPath) {

        try (FileInputStream inputStream = new FileInputStream(new File(reportPath))) {

            Map<String, List<QueryResult>> emitterQueryResultMap = new HashMap<>();

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("Emitter List") || workSheet.getSheetName().equals("Fine Item Dictionary")) {
                    continue;
                }
                
                String emitterName = null;
                List<QueryResult> queryResultList = new ArrayList<>();
                int maxColumnIndex = 0;
                Row firstRow = workSheet.getRow(0);

                for (Cell cell : firstRow) {
                    maxColumnIndex = Math.max(maxColumnIndex, cell.getColumnIndex());
                }

                for (Row row : workSheet) {

                    emitterName = workSheet.getSheetName();
                    QueryResult queryResult = new QueryResult();
                    List<GroupItem> groupItemList = new ArrayList<>();

                    if (row.getRowNum() > 0) {

                        for (int columnIndex = 0; columnIndex <= maxColumnIndex; ++columnIndex) {
                            Cell cell = row.getCell(columnIndex);
                            String stringCellValue = null;
                            try {
                                switch (columnIndex) {
                                    case 0: // FineItem
                                        try {
                                            stringCellValue = getStringCellValue(cell);
                                        } catch (NullPointerException e) {}
                                        queryResult.setFineItemCode(stringCellValue != null && !stringCellValue.isEmpty() ? stringCellValue : "TECH$BLANC");
                                        break;
                                    case 1: // HierPureItemPath
                                        queryResult.setHierPureItemPath(getStringCellValue(cell));
                                        break;
                                    case 2: // Index
                                        queryResult.setIndex(getIntCellValue(cell));
                                        break;
                                    case 3: // Count
                                        queryResult.setCnt(getIntCellValue(cell));
                                        break;
                                    default: // Показатели
                                        try {
                                            stringCellValue = getStringCellValue(cell);
                                        } catch (RuntimeException e) {
                                            stringCellValue = Integer.valueOf(getIntCellValue(cell)).toString();
                                        }
                                        groupItemList.add(new GroupItem(getStringCellValue(firstRow.getCell(columnIndex)), stringCellValue));
                                        break;
                                }
                            } catch (RuntimeException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        queryResult.setGroupItemList(groupItemList);
                        queryResultList.add(queryResult);
                    }
                }
                emitterQueryResultMap.put(emitterName, queryResultList);

            }
            String reportTypeCode = Paths.get(reportPath).getFileName().toString().replace(".xlsx", "").toUpperCase();
            queryResultMap.put(reportTypeCode, emitterQueryResultMap);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void readCommonReports(String reportPath) {
        try {
            
            Stream<Path> stream = Files.find(Paths.get(reportPath), 1, (p, a) -> !p.toString().equals("balance.xlsx") && p.toString().endsWith(".xlsx"));
//            List<Path>  files = stream.collect(Collectors.toList());
            stream.forEach(p -> readCommonReport(p.toString()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readSource(String sourcePath) {
        readBalanceReport(sourcePath);
        readCommonReports(sourcePath);
    }

    public ReadExcelFine() {
        readSource(rb.getString("import_directory"));
    }
}
