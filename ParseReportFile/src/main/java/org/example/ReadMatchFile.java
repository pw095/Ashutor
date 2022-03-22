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

    private static void readCapitalHorizontal() {
        Map<String, List<Main.QueryResult>> emitterInfo = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\capital_horizontal.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("Emitter List") || workSheet.getSheetName().equals("Fine Item Dictionary")) {
                    continue;
                }
                String emitterName = null;
                List<Main.QueryResult> queryResultList = new ArrayList<>();
                int maxColumnIndex = 0;
                Row firstRow = workSheet.getRow(0);

//                System.out.println(workSheet.getSheetName());
                for (Cell cell : firstRow) {
                    maxColumnIndex = Math.max(maxColumnIndex, cell.getColumnIndex());
                }
//                System.out.println("maxColumnIndex = " + maxColumnIndex);
                for (Row row : workSheet) {

                    Main.QueryResult queryResult = new Main.QueryResult();
                    List<Main.GroupItem> groupItemList = new ArrayList<>();

                    if (row.getRowNum() > 0) {

                        for (int columnIndex=0; columnIndex <= maxColumnIndex; ++columnIndex) {
                            Cell cell = row.getCell(columnIndex);

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
                                    case 2: // Index
                                        queryResult.index = (int) cell.getNumericCellValue();
                                        break;
                                    case 3: // Count
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
//                                System.out.println("Sheet = " + workSheet.getSheetName() + "Cell type = " + cell.getCellType() + ", column = " + cell.getColumnIndex() + ", row = " + cell.getRowIndex());
                                throw new RuntimeException(e);
                            }
                        }
                        queryResult.groupItemList = groupItemList;
                        queryResultList.add(queryResult);
                    }
                }
                emitterInfo.put(emitterName, queryResultList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return emitterPLInfo;
        String sqlStageTmpDeletePath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlStageTmpInsertPath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlTransformLoadPath = Paths.get(rb.getString("transform_load_directory")).toString();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFineItemFileMatchDelete = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_fine_item_file_capital_match.sql")))) {

                pstmtFineItemFileMatchDelete.execute();

            }
            conn.commit();

            try (PreparedStatement pstmtFineItemFileMatchInsert = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_fine_item_file_capital_match.sql")))) {

                for (String emitterName : emitterInfo.keySet()) {
                    System.out.println("emitterName = " + emitterName);
                    List<Main.QueryResult> queryResultList = emitterInfo.get(emitterName);
                    for (Main.QueryResult queryResult : queryResultList) {
                        for (Main.GroupItem groupItem : queryResult.groupItemList) {
                            pstmtFineItemFileMatchInsert.setInt(1, groupItem.id);
                            pstmtFineItemFileMatchInsert.setString(2, queryResult.fineItemCode);
                            pstmtFineItemFileMatchInsert.setString(3, emitterName);
                            pstmtFineItemFileMatchInsert.setString(4, groupItem.reportDate);
                            pstmtFineItemFileMatchInsert.addBatch();
                        }
                    }
                }
                pstmtFineItemFileMatchInsert.executeBatch();
            }
            conn.commit();

            try (PreparedStatement pstmtFineItem = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_fine_item__capital.sql")));
                 PreparedStatement pstmtItemFile = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_capital_horizontal_3.sql")))) {

                pstmtFineItem.executeUpdate();
                pstmtItemFile.executeUpdate();

            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private static void readCapitalVertical() {
        Map<String, List<Main.QueryResult>> emitterInfo = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\capital_vertical.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("Emitter List") || workSheet.getSheetName().equals("Fine Item Dictionary")) {
                    continue;
                }
                String emitterName = null;
                List<Main.QueryResult> queryResultList = new ArrayList<>();
                int maxColumnIndex = 0;
                Row firstRow = workSheet.getRow(0);

//                System.out.println(workSheet.getSheetName());
                for (Cell cell : firstRow) {
                    maxColumnIndex = Math.max(maxColumnIndex, cell.getColumnIndex());
                }
//                System.out.println("maxColumnIndex = " + maxColumnIndex);
                for (Row row : workSheet) {

                    Main.QueryResult queryResult = new Main.QueryResult();
                    List<Main.GroupItem> groupItemList = new ArrayList<>();

                    if (row.getRowNum() > 0) {

                        for (int columnIndex=0; columnIndex <= maxColumnIndex; ++columnIndex) {
                            Cell cell = row.getCell(columnIndex);

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
                                    case 2: // Index
                                        queryResult.index = (int) cell.getNumericCellValue();
                                        break;
                                    case 3: // Count
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
//                                System.out.println("Sheet = " + workSheet.getSheetName() + "Cell type = " + cell.getCellType() + ", column = " + cell.getColumnIndex() + ", row = " + cell.getRowIndex());
                                throw new RuntimeException(e);
                            }
                        }
                        queryResult.groupItemList = groupItemList;
                        queryResultList.add(queryResult);
                    }
                }
                emitterInfo.put(emitterName, queryResultList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return emitterPLInfo;
        String sqlStageTmpDeletePath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlStageTmpInsertPath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlTransformLoadPath = Paths.get(rb.getString("transform_load_directory")).toString();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFineItemFileMatchDelete = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_fine_item_file_capital_match.sql")))) {

                pstmtFineItemFileMatchDelete.execute();

            }
            conn.commit();

            try (PreparedStatement pstmtFineItemFileMatchInsert = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_fine_item_file_capital_match.sql")))) {

                for (String emitterName : emitterInfo.keySet()) {
                    System.out.println("emitterName = " + emitterName);
                    List<Main.QueryResult> queryResultList = emitterInfo.get(emitterName);
                    for (Main.QueryResult queryResult : queryResultList) {
                        for (Main.GroupItem groupItem : queryResult.groupItemList) {
                            pstmtFineItemFileMatchInsert.setInt(1, groupItem.id);
                            pstmtFineItemFileMatchInsert.setString(2, queryResult.fineItemCode);
                            pstmtFineItemFileMatchInsert.setString(3, emitterName);
                            pstmtFineItemFileMatchInsert.setString(4, groupItem.reportDate);
                            pstmtFineItemFileMatchInsert.addBatch();
                        }
                    }
                }
                pstmtFineItemFileMatchInsert.executeBatch();
            }
            conn.commit();

            try (PreparedStatement pstmtFineItem = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_fine_item__capital.sql")));
                 PreparedStatement pstmtItemFile = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_capital_vertical_3.sql")))) {

                pstmtFineItem.executeUpdate();
                pstmtItemFile.executeUpdate();

            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private static void readCF() {
        Map<String, List<Main.QueryResult>> emitterInfo = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\cf.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("Emitter List") || workSheet.getSheetName().equals("Fine Item Dictionary")) {
                    continue;
                }
                String emitterName = null;
                List<Main.QueryResult> queryResultList = new ArrayList<>();
                int maxColumnIndex = 0;
                Row firstRow = workSheet.getRow(0);

//                System.out.println(workSheet.getSheetName());
                for (Cell cell : firstRow) {
                    maxColumnIndex = Math.max(maxColumnIndex, cell.getColumnIndex());
                }
//                System.out.println("maxColumnIndex = " + maxColumnIndex);
                for (Row row : workSheet) {

                    Main.QueryResult queryResult = new Main.QueryResult();
                    List<Main.GroupItem> groupItemList = new ArrayList<>();

                    if (row.getRowNum() > 0) {

                        for (int columnIndex=0; columnIndex <= maxColumnIndex; ++columnIndex) {
                            Cell cell = row.getCell(columnIndex);

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
                                    case 2: // Index
                                        queryResult.index = (int) cell.getNumericCellValue();
                                        break;
                                    case 3: // Count
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
//                                System.out.println("Sheet = " + workSheet.getSheetName() + "Cell type = " + cell.getCellType() + ", column = " + cell.getColumnIndex() + ", row = " + cell.getRowIndex());
                                throw new RuntimeException(e);
                            }
                        }
                        queryResult.groupItemList = groupItemList;
                        queryResultList.add(queryResult);
                    }
                }
                emitterInfo.put(emitterName, queryResultList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return emitterPLInfo;
        String sqlStageTmpDeletePath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlStageTmpInsertPath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlTransformLoadPath = Paths.get(rb.getString("transform_load_directory")).toString();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFineItemFilePlMatchDelete = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_fine_item_file_cf_match.sql")))) {

                pstmtFineItemFilePlMatchDelete.execute();

            }
            conn.commit();

            try (PreparedStatement pstmtFineItemFileCFMatchInsert = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_fine_item_file_cf_match.sql")))) {

                for (String emitterName : emitterInfo.keySet()) {
                    System.out.println("emitterName = " + emitterName);
                    List<Main.QueryResult> queryResultList = emitterInfo.get(emitterName);
                    for (Main.QueryResult queryResult : queryResultList) {
                        for (Main.GroupItem groupItem : queryResult.groupItemList) {
                            pstmtFineItemFileCFMatchInsert.setInt(1, groupItem.id);
                            pstmtFineItemFileCFMatchInsert.setString(2, queryResult.fineItemCode);
                            pstmtFineItemFileCFMatchInsert.setString(3, emitterName);
                            pstmtFineItemFileCFMatchInsert.setString(4, groupItem.reportDate);
                            pstmtFineItemFileCFMatchInsert.addBatch();
                        }
                    }
                }
                pstmtFineItemFileCFMatchInsert.executeBatch();
            }
            conn.commit();

            try (PreparedStatement pstmtFineItem = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_fine_item__cf.sql")));
                 PreparedStatement pstmtItemFilePl = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_cf_3.sql")))) {

                pstmtFineItem.executeUpdate();
                pstmtItemFilePl.executeUpdate();

            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private static void readPL() {
        Map<String, List<Main.QueryResult>> emitterInfo = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\pl.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("Emitter List") || workSheet.getSheetName().equals("Fine Item Dictionary")) {
                    continue;
                }
                String emitterName = null;
                List<Main.QueryResult> queryResultList = new ArrayList<>();
                int maxColumnIndex = 0;
                Row firstRow = workSheet.getRow(0);

//                System.out.println(workSheet.getSheetName());
                for (Cell cell : firstRow) {
                    maxColumnIndex = Math.max(maxColumnIndex, cell.getColumnIndex());
                }
//                System.out.println("maxColumnIndex = " + maxColumnIndex);
                for (Row row : workSheet) {

                    Main.QueryResult queryResult = new Main.QueryResult();
                    List<Main.GroupItem> groupItemList = new ArrayList<>();

                    if (row.getRowNum() > 0) {

                        for (int columnIndex=0; columnIndex <= maxColumnIndex; ++columnIndex) {
                            Cell cell = row.getCell(columnIndex);

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
                                    case 2: // Index
                                        queryResult.index = (int) cell.getNumericCellValue();
                                        break;
                                    case 3: // Count
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
//                                System.out.println("Sheet = " + workSheet.getSheetName() + "Cell type = " + cell.getCellType() + ", column = " + cell.getColumnIndex() + ", row = " + cell.getRowIndex());
                                throw new RuntimeException(e);
                            }
                        }
                        queryResult.groupItemList = groupItemList;
                        queryResultList.add(queryResult);
                    }
                }
                emitterInfo.put(emitterName, queryResultList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return emitterPLInfo;
        String sqlStageTmpDeletePath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlStageTmpInsertPath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlTransformLoadPath = Paths.get(rb.getString("transform_load_directory")).toString();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFineItemFilePlMatchDelete = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_fine_item_file_pl_match.sql")))) {

                pstmtFineItemFilePlMatchDelete.execute();

            }
            conn.commit();

            try (PreparedStatement pstmtFineItemFilePlMatchInsert = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_fine_item_file_pl_match.sql")))) {

                for (String emitterName : emitterInfo.keySet()) {
                    System.out.println("emitterName = " + emitterName);
                    List<Main.QueryResult> queryResultList = emitterInfo.get(emitterName);
                    for (Main.QueryResult queryResult : queryResultList) {
                        for (Main.GroupItem groupItem : queryResult.groupItemList) {
                            pstmtFineItemFilePlMatchInsert.setInt(1, groupItem.id);
                            pstmtFineItemFilePlMatchInsert.setString(2, queryResult.fineItemCode);
                            pstmtFineItemFilePlMatchInsert.setString(3, emitterName);
                            pstmtFineItemFilePlMatchInsert.setString(4, groupItem.reportDate);
                            pstmtFineItemFilePlMatchInsert.addBatch();
                        }
                    }
                }
                pstmtFineItemFilePlMatchInsert.executeBatch();
            }
            conn.commit();

            try (PreparedStatement pstmtFineItem = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_fine_item__pl.sql")));
                 PreparedStatement pstmtItemFilePl = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_pl_3.sql")))) {

                pstmtFineItem.executeUpdate();
                pstmtItemFilePl.executeUpdate();

            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void readBalance() {
        Map<String, List<Main.QueryResult>> emitterBalanceInfo = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\balance.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("Emitter List") || workSheet.getSheetName().equals("Fine Item Dictionary")) {
                    continue;
                }
                String emitterName = null;
                List<Main.QueryResult> queryResultList = new ArrayList<>();
                int maxColumnIndex = 0;
                Row firstRow = workSheet.getRow(0);

//                System.out.println(workSheet.getSheetName());
                for (Cell cell : firstRow) {
                    maxColumnIndex = maxColumnIndex > cell.getColumnIndex() ? maxColumnIndex : cell.getColumnIndex();
                }
//                System.out.println("maxColumnIndex = " + maxColumnIndex);
                for (Row row : workSheet) {

                    Main.QueryResult queryResult = new Main.QueryResult();
                    List<Main.GroupItem> groupItemList = new ArrayList<>();

                    if (row.getRowNum() > 0) {

                        for (int columnIndex=0; columnIndex <= maxColumnIndex; ++columnIndex) {
                            Cell cell = row.getCell(columnIndex);

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
                                    case 3: // Index
                                        queryResult.index = (int) cell.getNumericCellValue();
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
        String sqlStageTmpDeletePath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlStageTmpInsertPath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlTransformLoadPath = Paths.get(rb.getString("transform_load_directory")).toString();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFineItemFileBalanceMatchDelete = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_fine_item_file_balance_match.sql")))) {
                pstmtFineItemFileBalanceMatchDelete.execute();
            }
            conn.commit();

            try (PreparedStatement pstmtFineItemFileBalanceMatchInsert = conn.prepareStatement(Main.getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_fine_item_file_balance_match.sql")))) {
                for (String emitterName : emitterBalanceInfo.keySet()) {
                    System.out.println("emitterName = " + emitterName);
                    List<Main.QueryResult> queryResultList = emitterBalanceInfo.get(emitterName);
                    for (Main.QueryResult queryResult : queryResultList) {
                        for (Main.GroupItem groupItem : queryResult.groupItemList) {
                            pstmtFineItemFileBalanceMatchInsert.setInt(1, groupItem.id);
                            pstmtFineItemFileBalanceMatchInsert.setString(2, queryResult.fineItemCode);
                            pstmtFineItemFileBalanceMatchInsert.setString(3, emitterName);
                            pstmtFineItemFileBalanceMatchInsert.setString(4, groupItem.reportDate);
                            pstmtFineItemFileBalanceMatchInsert.addBatch();
                        }
                    }
                }
                pstmtFineItemFileBalanceMatchInsert.executeBatch();
            }
            conn.commit();

            try (PreparedStatement pstmtFineItem = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_fine_item__balance.sql")));
                 PreparedStatement pstmtItemFileBalance = conn.prepareStatement(Main.getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_balance_3.sql")))) {

                pstmtFineItem.executeUpdate();
                pstmtItemFileBalance.executeUpdate();

            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        readBalance();
        readPL();
        readCF();
        readCapitalHorizontal();
        readCapitalVertical();

    }
}
