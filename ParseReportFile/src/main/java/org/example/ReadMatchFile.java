package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReadMatchFile {

    static ResourceBundle rb;
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static {
        rb = ResourceBundle.getBundle("application");
    }

    public static void main(String[] args) {

        Map<String, List<Main.QueryResult>> emitterBalanceInfo = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\temp_merge_file.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("Emitter List") || workSheet.getSheetName().equals("Fine Item Dictionary")) {
                    continue;
                }
                String emitterName = null;
                List<Main.QueryResult> queryResultList = new ArrayList<>();
                int maxColumnIndex = 0;
                Row firstRow = workSheet.getRow(0);

                System.out.println(workSheet.getSheetName());
                for (Cell cell : firstRow) {
                    maxColumnIndex = maxColumnIndex > cell.getColumnIndex() ? maxColumnIndex : cell.getColumnIndex();
                }
                System.out.println("maxColumnIndex = " + maxColumnIndex);
                for (Row row : workSheet) {

                    Main.QueryResult queryResult = new Main.QueryResult();
                    List<Main.GroupItem> groupItemList = new ArrayList<>();

                    if (row.getRowNum() > 0) {

//                        int columnIndex = 0;
                        for (int columnIndex=0; columnIndex <= maxColumnIndex; ++columnIndex) {
                            Cell cell = row.getCell(columnIndex);
//                        for (Cell cell : row) {

                            emitterName = workSheet.getSheetName();
                            int numberCellValue;
                            try {
                                switch (columnIndex) {
                                    case 0: // FineItem
                                        if (cell != null) {
                                            if (cell.getCellType() == CellType.STRING) {
                                                if (!(cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty())) {
                                                    queryResult.fineItemCode = cell.getStringCellValue();
                                                } else {
                                                    queryResult.fineItemCode = "TECH$BLANC";
                                                }
                                            } else {
                                                queryResult.fineItemCode = "TECH$BLANC";
                                            }
                                        } else {
                                            queryResult.fineItemCode = "TECH$BLANC";
                                        }
                                        break;
                                    case 1: // HierPureItemPath
                                        queryResult.hierPureItemPath = cell.getStringCellValue();
                                        break;
                                    case 2: // Level
                                        queryResult.level = (int) cell.getNumericCellValue();
                                        break;
                                    case 3: // IfbIndex
                                        queryResult.ifbIndex = (int) cell.getNumericCellValue();
                                        break;
                                    case 4: // Count
                                        queryResult.cnt = (int) cell.getNumericCellValue();
                                        break;
                                    default: // Показатели
                                        if (cell != null) {
                                            if (cell.getCellType() == CellType.NUMERIC) {
                                                numberCellValue = (int) cell.getNumericCellValue();
                                            } else {
                                                numberCellValue = 0;
                                            }
                                        } else {
                                            numberCellValue = 0;
                                        }
                                        groupItemList.add(new Main.GroupItem(firstRow.getCell(columnIndex).getStringCellValue(), numberCellValue));
                                        //                                    System.out.println("emitterName = " + emitterName + ", rowIndex = " + row.getRowNum() + ", queryResult = " + queryResult.fineItem + ", groupItem = " + firstRow.getCell(columnIndex).getStringCellValue() + " : " + numberCellValue);
                                        break;
                                }
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                System.out.println("Sheet = " + workSheet.getSheetName() + "Cell type = " + cell.getCellType() + ", column = " + cell.getColumnIndex() + ", row = " + cell.getRowIndex());
                                throw new RuntimeException(e);
                            }
                        }
                        queryResult.groupItemList = groupItemList;
                        queryResultList.add(queryResult);
                    }
                }
                emitterBalanceInfo.put(emitterName, queryResultList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sqlInsertPath = Paths.get(rb.getString("tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlDeletePath = Paths.get(rb.getString("tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlTransformLoadPath = Paths.get("transform_load").toString();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFinePureItemMatchDelete = conn.prepareStatement(Main.getQuery(Paths.get(sqlDeletePath, rb.getString("fine_item_match"))))) {

                pstmtFinePureItemMatchDelete.execute();

            }

            conn.commit();
            try (PreparedStatement pstmtInsert = conn.prepareStatement(Main.getQuery(Paths.get(sqlInsertPath, rb.getString("fine_item_match"))))) {

                for (String emitterName : emitterBalanceInfo.keySet()) {
                    System.out.println("emitterName = " + emitterName);
                    List<Main.QueryResult> queryResultList = emitterBalanceInfo.get(emitterName);
                    int rowIndex=0;
                    for (Main.QueryResult queryResult : queryResultList) {
//                        System.out.print(queryResult.fineItemCode);
//                        queryResult.groupItemList.stream().map(p -> p.reportDate).forEach(System.out::print);
//                        System.out.println();
                        for (Main.GroupItem groupItem : queryResult.groupItemList) {
                            pstmtInsert.setInt(1, rowIndex);
                            pstmtInsert.setString(2, queryResult.fineItemCode);
                            pstmtInsert.setString(3, emitterName);
                            pstmtInsert.setString(4, groupItem.reportDate);
                            pstmtInsert.setInt(5, groupItem.ifbId);
                            pstmtInsert.addBatch();
                            ++rowIndex;
                        }
                    }
                }
                pstmtInsert.executeBatch();
            }

            conn.commit();
            try (PreparedStatement pstmtFineItem = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, rb.getString("fine_item_1"))));
                 PreparedStatement pstmtItemFileBalance = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, rb.getString("item_file_balance_3"))))) {
                pstmtFineItem.executeUpdate();
                pstmtItemFileBalance.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

     }
}
