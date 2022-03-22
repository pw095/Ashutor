package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.item.BalanceItemInfo;
import org.example.item.CFItemInfo;
import org.example.item.CapitalItemInfo;
import org.example.item.PLItemInfo;
import org.example.report.DoubleDimensionReportInfo;
import org.example.report.ReportInfo;
import org.example.report.SingleDimensionReportInfo;
import org.example.sheet.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    static ResourceBundle rb;
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static {
        rb = ResourceBundle.getBundle("application");
    }

    public static class GroupItem {
        String reportDate;
        int id;

        GroupItem(String str) {
            String[] arr = str.split(": ");
//            new GroupItem(arr[0], Integer.valueOf(arr[1]));
            reportDate = arr[0];
            id = Integer.parseInt(arr[1]);
        }
        GroupItem(String reportDate, int id) {
            this.reportDate = reportDate;
            this.id = id;
        }

    }

    public static class QueryResult {
        String fineItemCode;
        String hierPureItemPath;
        int level;
        int index;
        int cnt;
        String groupCnct;
        List<GroupItem> groupItemList;

        QueryResult() {

        }

        QueryResult(String fineItemCode, String hierPureItemPath, int index, int cnt, String groupCnct) {

            this.fineItemCode = fineItemCode;
            this.hierPureItemPath = hierPureItemPath;
            this.index = index;
            this.cnt = cnt;
            this.groupCnct = groupCnct;

            List<String> list = Arrays.asList(groupCnct.split(", "));
            groupItemList = new ArrayList<>();

            for (String elt : list) {
                groupItemList.add(new GroupItem(elt));
            }
        }

        QueryResult(String fineItemCode, String hierPureItemPath, int level, int index, int cnt, String groupCnct) {

            this.fineItemCode = fineItemCode;
            this.hierPureItemPath = hierPureItemPath;
            this.level = level;
            this.index = index;
            this.cnt = cnt;
            this.groupCnct = groupCnct;

            List<String> list = Arrays.asList(groupCnct.split(", "));
            groupItemList = new ArrayList<>();

            for (String elt : list) {
                groupItemList.add(new GroupItem(elt));
            }
        }
    }

    public static class FineItemInfo {
        String fineItemCode;
        String fineItemName;
        String hierPureItemPath;

        FineItemInfo() {}

        FineItemInfo(String fineItemCode, String fineItemName, String hierPureItemPath) {
            this.fineItemCode = fineItemCode;
            this.fineItemName = fineItemName;
            this.hierPureItemPath = hierPureItemPath;
        }
    }

    public static void get() {

        String sqlGetPath = Paths.get(rb.getString("get_directory"), "export").toString();

        Map<String, List<QueryResult>> emitterBalanceInfo = new HashMap<>();
        Map<String, List<QueryResult>> emitterPLInfo = new HashMap<>();
        Map<String, List<QueryResult>> emitterCFInfo = new HashMap<>();
        Map<String, List<QueryResult>> emitterCapitalHorizontalInfo = new HashMap<>();
        Map<String, List<QueryResult>> emitterCapitalVerticalInfo = new HashMap<>();
        List<FineItemInfo> fineItemBalanceInfoList = new ArrayList<>();
        List<FineItemInfo> fineItemPLInfoList = new ArrayList<>();
        List<FineItemInfo> fineItemCFInfoList = new ArrayList<>();
        List<FineItemInfo> fineItemCapitalHorizontalInfoList = new ArrayList<>();
        List<FineItemInfo> fineItemCapitalVerticalInfoList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {
            try (PreparedStatement pstmtFineItemBalance = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_fine_item_balance.sql")));
                 ResultSet rs = pstmtFineItemBalance.executeQuery()) {
                while (rs.next()) {
                    String fineItemCode = rs.getString("fine_item_code");
                    String fineItemName = rs.getString("fine_item_name");
                    String hierPureItemPath = rs.getString("hier_pure_item_path");
                    fineItemBalanceInfoList.add(new FineItemInfo(fineItemCode, fineItemName, hierPureItemPath));
                }
            }

            try (PreparedStatement pstmtFineItemPL = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_fine_item_pl.sql")));
                 ResultSet rs = pstmtFineItemPL.executeQuery()) {
                while (rs.next()) {
                    String fineItemCode = rs.getString("fine_item_code");
                    String fineItemName = rs.getString("fine_item_name");
                    String hierPureItemPath = rs.getString("hier_pure_item_path");
                    fineItemPLInfoList.add(new FineItemInfo(fineItemCode, fineItemName, hierPureItemPath));
                }
            }

            try (PreparedStatement pstmtFineItemPL = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_fine_item_cf.sql")));
                 ResultSet rs = pstmtFineItemPL.executeQuery()) {
                while (rs.next()) {
                    String fineItemCode = rs.getString("fine_item_code");
                    String fineItemName = rs.getString("fine_item_name");
                    String hierPureItemPath = rs.getString("hier_pure_item_path");
                    fineItemCFInfoList.add(new FineItemInfo(fineItemCode, fineItemName, hierPureItemPath));
                }
            }

            try (PreparedStatement pstmtFineItemPL = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_fine_item_capital_horizontal.sql")));
                 ResultSet rs = pstmtFineItemPL.executeQuery()) {
                while (rs.next()) {
                    String fineItemCode = rs.getString("fine_item_code");
                    String fineItemName = rs.getString("fine_item_name");
                    String hierPureItemPath = rs.getString("hier_pure_item_path");
                    fineItemCapitalHorizontalInfoList.add(new FineItemInfo(fineItemCode, fineItemName, hierPureItemPath));
                }
            }

            try (PreparedStatement pstmtFineItemPL = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_fine_item_capital_vertical.sql")));
                 ResultSet rs = pstmtFineItemPL.executeQuery()) {
                while (rs.next()) {
                    String fineItemCode = rs.getString("fine_item_code");
                    String fineItemName = rs.getString("fine_item_name");
                    String hierPureItemPath = rs.getString("hier_pure_item_path");
                    fineItemCapitalVerticalInfoList.add(new FineItemInfo(fineItemCode, fineItemName, hierPureItemPath));
                }
            }

            try (PreparedStatement pstmtEmitter = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_emitters.sql")));
                 PreparedStatement pstmtBalanceGet = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_pure_x_fine_item_match_balance.sql")));
                 PreparedStatement pstmtPLGet = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_pure_x_fine_item_match_pl.sql")));
                 PreparedStatement pstmtCFGet = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_pure_x_fine_item_match_cf.sql")));
                 PreparedStatement pstmtCapitalHorizontalGet = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_pure_x_fine_item_match_capital_horizontal.sql")));
                 PreparedStatement pstmtCapitalVerticalGet = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "export_pure_x_fine_item_match_capital_vertical.sql")));
                 ResultSet rs = pstmtEmitter.executeQuery()) {

                while (rs.next()) {
                    String emitterName = rs.getString("emitter_name");

                    pstmtBalanceGet.setString(1, emitterName);
                    List<QueryResult> queryResultBalanceList = new ArrayList<>();
                    try (ResultSet rsGet = pstmtBalanceGet.executeQuery()) {
                        while (rsGet.next()) {
                            String fineItemCode = rsGet.getString("fine_item_code");
                            String hierPureItemPath = rsGet.getString("hier_pure_item_path");
                            int level = rsGet.getInt("level");
                            int index = rsGet.getInt("ifb_index");
                            int cnt = rsGet.getInt("cnt");
                            String groupCnct = rsGet.getString("group_cnct");
                            queryResultBalanceList.add(new QueryResult(fineItemCode, hierPureItemPath, level, index, cnt, groupCnct));
                        }
                    }
                    emitterBalanceInfo.put(emitterName, queryResultBalanceList);

                    pstmtPLGet.setString(1, emitterName);
                    List<QueryResult> queryResultPLList = new ArrayList<>();
                    try (ResultSet rsGet = pstmtPLGet.executeQuery()) {
                        while (rsGet.next()) {
                            String fineItemCode = rsGet.getString("fine_item_code");
                            String hierPureItemPath = rsGet.getString("hier_pure_item_path");
                            int index = rsGet.getInt("ifpl_index");
                            int cnt = rsGet.getInt("cnt");
                            String groupCnct = rsGet.getString("group_cnct");
                            queryResultPLList.add(new QueryResult(fineItemCode, hierPureItemPath, index, cnt, groupCnct));
                        }
                    }
                    emitterPLInfo.put(emitterName, queryResultPLList);

                    pstmtCFGet.setString(1, emitterName);
                    List<QueryResult> queryResultCFList = new ArrayList<>();
                    try (ResultSet rsGet = pstmtCFGet.executeQuery()) {
                        while (rsGet.next()) {
                            String fineItemCode = rsGet.getString("fine_item_code");
                            String hierPureItemPath = rsGet.getString("hier_pure_item_path");
                            int index = rsGet.getInt("ifcf_index");
                            int cnt = rsGet.getInt("cnt");
                            String groupCnct = rsGet.getString("group_cnct");
                            queryResultCFList.add(new QueryResult(fineItemCode, hierPureItemPath, index, cnt, groupCnct));
                        }
                    }
                    emitterCFInfo.put(emitterName, queryResultCFList);

                    pstmtCapitalHorizontalGet.setString(1, emitterName);
                    List<QueryResult> queryResultCapitalHorizontalList = new ArrayList<>();
                    try (ResultSet rsGet = pstmtCapitalHorizontalGet.executeQuery()) {
                        while (rsGet.next()) {
                            String fineItemCode = rsGet.getString("fine_item_code");
                            String hierPureItemPath = rsGet.getString("hier_pure_item_path");
                            int index = rsGet.getInt("ifc_index");
                            int cnt = rsGet.getInt("cnt");
                            String groupCnct = rsGet.getString("group_cnct");
                            queryResultCapitalHorizontalList.add(new QueryResult(fineItemCode, hierPureItemPath, index, cnt, groupCnct));
                        }
                    }
                    emitterCapitalHorizontalInfo.put(emitterName, queryResultCapitalHorizontalList);

                    pstmtCapitalVerticalGet.setString(1, emitterName);
                    List<QueryResult> queryResultCapitalVerticalList = new ArrayList<>();
                    try (ResultSet rsGet = pstmtCapitalVerticalGet.executeQuery()) {
                        while (rsGet.next()) {
                            String fineItemCode = rsGet.getString("fine_item_code");
                            String hierPureItemPath = rsGet.getString("hier_pure_item_path");
                            int index = rsGet.getInt("ifc_index");
                            int cnt = rsGet.getInt("cnt");
                            String groupCnct = rsGet.getString("group_cnct");
                            queryResultCapitalVerticalList.add(new QueryResult(fineItemCode, hierPureItemPath, index, cnt, groupCnct));
                        }
                    }
                    emitterCapitalVerticalInfo.put(emitterName, queryResultCapitalVerticalList);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (FileOutputStream file = new FileOutputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\balance.xlsx"))) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

            int rowNum = 0;
            Row headerRow = emitterListSheet.createRow(rowNum);
            headerRow.createCell(0).setCellValue("EmitterList");

            for (String emitterName : emitterBalanceInfo.keySet()) {
                Row row = emitterListSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(emitterName);
            }

            XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

            rowNum = 0;
            headerRow = fineItemDictSheet.createRow(rowNum);

            headerRow.createCell(0).setCellValue("fine_item_code");
            headerRow.createCell(1).setCellValue("fine_item_name");
            headerRow.createCell(2).setCellValue("hier_pure_item_path");

            for (FineItemInfo fineItemInfo : fineItemBalanceInfoList) {
                Row row = fineItemDictSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(fineItemInfo.fineItemCode);
                row.createCell(1).setCellValue(fineItemInfo.fineItemName);
                row.createCell(2).setCellValue(fineItemInfo.hierPureItemPath);
            }

            for (String emitterName : emitterBalanceInfo.keySet()) {
                List<QueryResult> queryResultList = emitterBalanceInfo.get(emitterName);
                List<String> arrayList = queryResultList.stream()
                    .map(p -> p.groupItemList)
                    .flatMap(Collection::stream)
                    .map(p -> p.reportDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                arrayList.forEach(System.out::println);

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
                    newRow.createCell(0).setCellValue(queryResultList.get(jnd).fineItemCode);
                    newRow.createCell(1).setCellValue(queryResultList.get(jnd).hierPureItemPath);
                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).level);
                    newRow.createCell(3).setCellValue(queryResultList.get(jnd).index);
                    newRow.createCell(4).setCellValue(queryResultList.get(jnd).cnt);

                    for (int ind=0; ind < queryResultList.get(jnd).groupItemList.size(); ++ind) {
                        int knd = arrayList.indexOf(queryResultList.get(jnd).groupItemList.get(ind).reportDate);
                        newRow.createCell(5+knd).setCellValue(queryResultList.get(jnd).groupItemList.get(ind).id);
                    }
                }
            }
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream file = new FileOutputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\pl.xlsx"))) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

            int rowNum = 0;
            Row headerRow = emitterListSheet.createRow(rowNum);
            headerRow.createCell(0).setCellValue("EmitterList");

            for (String emitterName : emitterPLInfo.keySet()) {
                Row row = emitterListSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(emitterName);
            }

            XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

            rowNum = 0;
            headerRow = fineItemDictSheet.createRow(rowNum);

            headerRow.createCell(0).setCellValue("fine_item_code");
            headerRow.createCell(1).setCellValue("fine_item_name");
            headerRow.createCell(2).setCellValue("hier_pure_item_path");

            for (FineItemInfo fineItemInfo : fineItemPLInfoList) {
                Row row = fineItemDictSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(fineItemInfo.fineItemCode);
                row.createCell(1).setCellValue(fineItemInfo.fineItemName);
                row.createCell(2).setCellValue(fineItemInfo.hierPureItemPath);
            }

            for (String emitterName : emitterPLInfo.keySet()) {
                List<QueryResult> queryResultList = emitterPLInfo.get(emitterName);
                List<String> arrayList = queryResultList.stream()
                    .map(p -> p.groupItemList)
//                    .peek(p -> System.out.println(p.size()))
                    .flatMap(Collection::stream)
//                    .peek(p -> System.out.println(p.ifbId + " " + p.reportDate))
                    .map(p -> p.reportDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                arrayList.forEach(System.out::println);

                XSSFSheet emitterPLInfoSheet = workbook.createSheet(emitterName);

                rowNum = 0;

                Row row = emitterPLInfoSheet.createRow(rowNum);

                row.createCell(0).setCellValue("FineItemCode");
                row.createCell(1).setCellValue("HierPureItemPath");
//                row.createCell(2).setCellValue("Level");
                row.createCell(2).setCellValue("Index");
                row.createCell(3).setCellValue("Count");

                for (int ind=0; ind < arrayList.size(); ++ind) {
                    row.createCell(4+ind).setCellValue(arrayList.get(ind));
                }

                for (int jnd=0; jnd < queryResultList.size(); ++jnd) {
                    Row newRow = emitterPLInfoSheet.createRow(jnd+1);
                    newRow.createCell(0).setCellValue(queryResultList.get(jnd).fineItemCode);
                    newRow.createCell(1).setCellValue(queryResultList.get(jnd).hierPureItemPath);
//                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).level);
                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).index);
                    newRow.createCell(3).setCellValue(queryResultList.get(jnd).cnt);

                    for (int ind=0; ind < queryResultList.get(jnd).groupItemList.size(); ++ind) {
                        int knd = arrayList.indexOf(queryResultList.get(jnd).groupItemList.get(ind).reportDate);
                        newRow.createCell(4+knd).setCellValue(queryResultList.get(jnd).groupItemList.get(ind).id);
                    }
                }
            }
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream file = new FileOutputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\cf.xlsx"))) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

            int rowNum = 0;
            Row headerRow = emitterListSheet.createRow(rowNum);
            headerRow.createCell(0).setCellValue("EmitterList");

            for (String emitterName : emitterCFInfo.keySet()) {
                Row row = emitterListSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(emitterName);
            }

            XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

            rowNum = 0;
            headerRow = fineItemDictSheet.createRow(rowNum);

            headerRow.createCell(0).setCellValue("fine_item_code");
            headerRow.createCell(1).setCellValue("fine_item_name");
            headerRow.createCell(2).setCellValue("hier_pure_item_path");

            for (FineItemInfo fineItemInfo : fineItemCFInfoList) {
                Row row = fineItemDictSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(fineItemInfo.fineItemCode);
                row.createCell(1).setCellValue(fineItemInfo.fineItemName);
                row.createCell(2).setCellValue(fineItemInfo.hierPureItemPath);
            }

            for (String emitterName : emitterCFInfo.keySet()) {
                List<QueryResult> queryResultList = emitterCFInfo.get(emitterName);
                List<String> arrayList = queryResultList.stream()
                    .map(p -> p.groupItemList)
//                    .peek(p -> System.out.println(p.size()))
                    .flatMap(Collection::stream)
//                    .peek(p -> System.out.println(p.ifbId + " " + p.reportDate))
                    .map(p -> p.reportDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                arrayList.forEach(System.out::println);

                XSSFSheet emitterCFInfoSheet = workbook.createSheet(emitterName);

                rowNum = 0;

                Row row = emitterCFInfoSheet.createRow(rowNum);

                row.createCell(0).setCellValue("FineItemCode");
                row.createCell(1).setCellValue("HierPureItemPath");
//                row.createCell(2).setCellValue("Level");
                row.createCell(2).setCellValue("Index");
                row.createCell(3).setCellValue("Count");

                for (int ind=0; ind < arrayList.size(); ++ind) {
                    row.createCell(4+ind).setCellValue(arrayList.get(ind));
                }

                for (int jnd=0; jnd < queryResultList.size(); ++jnd) {
                    Row newRow = emitterCFInfoSheet.createRow(jnd+1);
                    newRow.createCell(0).setCellValue(queryResultList.get(jnd).fineItemCode);
                    newRow.createCell(1).setCellValue(queryResultList.get(jnd).hierPureItemPath);
//                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).level);
                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).index);
                    newRow.createCell(3).setCellValue(queryResultList.get(jnd).cnt);

                    for (int ind=0; ind < queryResultList.get(jnd).groupItemList.size(); ++ind) {
                        int knd = arrayList.indexOf(queryResultList.get(jnd).groupItemList.get(ind).reportDate);
                        newRow.createCell(4+knd).setCellValue(queryResultList.get(jnd).groupItemList.get(ind).id);
                    }
                }
            }
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream file = new FileOutputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\capital_horizontal.xlsx"))) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

            int rowNum = 0;
            Row headerRow = emitterListSheet.createRow(rowNum);
            headerRow.createCell(0).setCellValue("EmitterList");

            for (String emitterName : emitterCapitalHorizontalInfo.keySet()) {
                Row row = emitterListSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(emitterName);
            }

            XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

            rowNum = 0;
            headerRow = fineItemDictSheet.createRow(rowNum);

            headerRow.createCell(0).setCellValue("fine_item_code");
            headerRow.createCell(1).setCellValue("fine_item_name");
            headerRow.createCell(2).setCellValue("hier_pure_item_path");

            for (FineItemInfo fineItemInfo : fineItemCapitalHorizontalInfoList) {
                Row row = fineItemDictSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(fineItemInfo.fineItemCode);
                row.createCell(1).setCellValue(fineItemInfo.fineItemName);
                row.createCell(2).setCellValue(fineItemInfo.hierPureItemPath);
            }

            for (String emitterName : emitterPLInfo.keySet()) {
                List<QueryResult> queryResultList = emitterCapitalHorizontalInfo.get(emitterName);
                List<String> arrayList = queryResultList.stream()
                    .map(p -> p.groupItemList)
//                    .peek(p -> System.out.println(p.size()))
                    .flatMap(Collection::stream)
//                    .peek(p -> System.out.println(p.ifbId + " " + p.reportDate))
                    .map(p -> p.reportDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                arrayList.forEach(System.out::println);

                XSSFSheet emitterCapitalHorizontalInfoSheet = workbook.createSheet(emitterName);

                rowNum = 0;

                Row row = emitterCapitalHorizontalInfoSheet.createRow(rowNum);

                row.createCell(0).setCellValue("FineItemCode");
                row.createCell(1).setCellValue("HierPureItemPath");
//                row.createCell(2).setCellValue("Level");
                row.createCell(2).setCellValue("Index");
                row.createCell(3).setCellValue("Count");

                for (int ind=0; ind < arrayList.size(); ++ind) {
                    row.createCell(4+ind).setCellValue(arrayList.get(ind));
                }

                for (int jnd=0; jnd < queryResultList.size(); ++jnd) {
                    Row newRow = emitterCapitalHorizontalInfoSheet.createRow(jnd+1);
                    newRow.createCell(0).setCellValue(queryResultList.get(jnd).fineItemCode);
                    newRow.createCell(1).setCellValue(queryResultList.get(jnd).hierPureItemPath);
//                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).level);
                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).index);
                    newRow.createCell(3).setCellValue(queryResultList.get(jnd).cnt);

                    for (int ind=0; ind < queryResultList.get(jnd).groupItemList.size(); ++ind) {
                        int knd = arrayList.indexOf(queryResultList.get(jnd).groupItemList.get(ind).reportDate);
                        newRow.createCell(4+knd).setCellValue(queryResultList.get(jnd).groupItemList.get(ind).id);
                    }
                }
            }
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream file = new FileOutputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\capital_horizontal.xlsx"))) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

            int rowNum = 0;
            Row headerRow = emitterListSheet.createRow(rowNum);
            headerRow.createCell(0).setCellValue("EmitterList");

            for (String emitterName : emitterCapitalHorizontalInfo.keySet()) {
                Row row = emitterListSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(emitterName);
            }

            XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

            rowNum = 0;
            headerRow = fineItemDictSheet.createRow(rowNum);

            headerRow.createCell(0).setCellValue("fine_item_code");
            headerRow.createCell(1).setCellValue("fine_item_name");
            headerRow.createCell(2).setCellValue("hier_pure_item_path");

            for (FineItemInfo fineItemInfo : fineItemCapitalHorizontalInfoList) {
                Row row = fineItemDictSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(fineItemInfo.fineItemCode);
                row.createCell(1).setCellValue(fineItemInfo.fineItemName);
                row.createCell(2).setCellValue(fineItemInfo.hierPureItemPath);
            }

            for (String emitterName : emitterPLInfo.keySet()) {
                List<QueryResult> queryResultList = emitterCapitalHorizontalInfo.get(emitterName);
                List<String> arrayList = queryResultList.stream()
                    .map(p -> p.groupItemList)
//                    .peek(p -> System.out.println(p.size()))
                    .flatMap(Collection::stream)
//                    .peek(p -> System.out.println(p.ifbId + " " + p.reportDate))
                    .map(p -> p.reportDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                arrayList.forEach(System.out::println);

                XSSFSheet emitterCapitalHorizontalInfoSheet = workbook.createSheet(emitterName);

                rowNum = 0;

                Row row = emitterCapitalHorizontalInfoSheet.createRow(rowNum);

                row.createCell(0).setCellValue("FineItemCode");
                row.createCell(1).setCellValue("HierPureItemPath");
//                row.createCell(2).setCellValue("Level");
                row.createCell(2).setCellValue("Index");
                row.createCell(3).setCellValue("Count");

                for (int ind=0; ind < arrayList.size(); ++ind) {
                    row.createCell(4+ind).setCellValue(arrayList.get(ind));
                }

                for (int jnd=0; jnd < queryResultList.size(); ++jnd) {
                    Row newRow = emitterCapitalHorizontalInfoSheet.createRow(jnd+1);
                    newRow.createCell(0).setCellValue(queryResultList.get(jnd).fineItemCode);
                    newRow.createCell(1).setCellValue(queryResultList.get(jnd).hierPureItemPath);
//                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).level);
                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).index);
                    newRow.createCell(3).setCellValue(queryResultList.get(jnd).cnt);

                    for (int ind=0; ind < queryResultList.get(jnd).groupItemList.size(); ++ind) {
                        int knd = arrayList.indexOf(queryResultList.get(jnd).groupItemList.get(ind).reportDate);
                        newRow.createCell(4+knd).setCellValue(queryResultList.get(jnd).groupItemList.get(ind).id);
                    }
                }
            }
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream file = new FileOutputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\capital_vertical.xlsx"))) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

            int rowNum = 0;
            Row headerRow = emitterListSheet.createRow(rowNum);
            headerRow.createCell(0).setCellValue("EmitterList");

            for (String emitterName : emitterCapitalVerticalInfo.keySet()) {
                Row row = emitterListSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(emitterName);
            }

            XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

            rowNum = 0;
            headerRow = fineItemDictSheet.createRow(rowNum);

            headerRow.createCell(0).setCellValue("fine_item_code");
            headerRow.createCell(1).setCellValue("fine_item_name");
            headerRow.createCell(2).setCellValue("hier_pure_item_path");

            for (FineItemInfo fineItemInfo : fineItemCapitalVerticalInfoList) {
                Row row = fineItemDictSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(fineItemInfo.fineItemCode);
                row.createCell(1).setCellValue(fineItemInfo.fineItemName);
                row.createCell(2).setCellValue(fineItemInfo.hierPureItemPath);
            }

            for (String emitterName : emitterPLInfo.keySet()) {
                List<QueryResult> queryResultList = emitterCapitalVerticalInfo.get(emitterName);
                List<String> arrayList = queryResultList.stream()
                    .map(p -> p.groupItemList)
//                    .peek(p -> System.out.println(p.size()))
                    .flatMap(Collection::stream)
//                    .peek(p -> System.out.println(p.ifbId + " " + p.reportDate))
                    .map(p -> p.reportDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

                arrayList.forEach(System.out::println);

                XSSFSheet emitterCapitalVerticalInfoSheet = workbook.createSheet(emitterName);

                rowNum = 0;

                Row row = emitterCapitalVerticalInfoSheet.createRow(rowNum);

                row.createCell(0).setCellValue("FineItemCode");
                row.createCell(1).setCellValue("HierPureItemPath");
//                row.createCell(2).setCellValue("Level");
                row.createCell(2).setCellValue("Index");
                row.createCell(3).setCellValue("Count");

                for (int ind=0; ind < arrayList.size(); ++ind) {
                    row.createCell(4+ind).setCellValue(arrayList.get(ind));
                }

                for (int jnd=0; jnd < queryResultList.size(); ++jnd) {
                    Row newRow = emitterCapitalVerticalInfoSheet.createRow(jnd+1);
                    newRow.createCell(0).setCellValue(queryResultList.get(jnd).fineItemCode);
                    newRow.createCell(1).setCellValue(queryResultList.get(jnd).hierPureItemPath);
//                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).level);
                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).index);
                    newRow.createCell(3).setCellValue(queryResultList.get(jnd).cnt);

                    for (int ind=0; ind < queryResultList.get(jnd).groupItemList.size(); ++ind) {
                        int knd = arrayList.indexOf(queryResultList.get(jnd).groupItemList.get(ind).reportDate);
                        newRow.createCell(4+knd).setCellValue(queryResultList.get(jnd).groupItemList.get(ind).id);
                    }
                }
            }
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException{

        List<FileInfo> fileInfoList = new ArrayList<>();
        get();
        if (2>1) {
            return;
        }

        Files.walk(Paths.get(new String(rb.getString("source_directory").getBytes("ISO-8859-1"), Charset.forName("UTF-8"))))
            .filter(p -> p.toString().endsWith(".xlsx"))
            .filter(p -> !p.toString().contains("cmn"))
            .filter(p -> !p.toString().contains("~$"))
            .filter(p -> !(p.toString().contains("balance.xlsx") || p.toString().contains("pl.xlsx") || p.toString().contains("cf.xlsx")))
            .filter(p -> p.toString().contains("Детский мир"))
            .filter(p -> p.toString().contains("2020"))
            .peek(p -> System.out.println(p.toAbsolutePath()))
            .map(FileInfo::new)
//            .peek(p -> System.out.println(p.emitterName + " " + p.fileName))
            .forEach(fileInfoList::add);
//
//        fileInfoList.get(0).sheetInfoMap.get("CAPITAL").reportDateList.forEach(System.out::println);

        System.out.println("Size = " + fileInfoList.size());
        fileInfoList.stream().peek(p -> System.out.println(p.emitterName + " " + p.fileDate)).forEach(FileInfo::getRich);
/*        if (2> 1) {
            return;
        }*/

        String sqlStageTmpDeletePath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlStageTmpInsertPath = Paths.get(rb.getString("stage_tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlTransformLoadTmpDeletePath = Paths.get(rb.getString("transform_load_tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlTransformLoadTmpInsertPath = Paths.get(rb.getString("transform_load_tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlTransformLoadPath = Paths.get(rb.getString("transform_load_directory")).toString();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFileDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_file.sql")));

                 PreparedStatement pstmtItemBalanceDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_item_balance.sql")));
                 PreparedStatement pstmtReportBalanceDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_report_balance.sql")));
                 PreparedStatement pstmtItemFileBalanceDelete = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadTmpDeletePath, "tmp_item_file_balance.sql")));

                 PreparedStatement pstmtItemPLDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_item_pl.sql")));
                 PreparedStatement pstmtReportPLDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_report_pl.sql")));
                 PreparedStatement pstmtItemFilePLDelete = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadTmpDeletePath, "tmp_item_file_pl.sql")));

                 PreparedStatement pstmtItemCFDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_item_cf.sql")));
                 PreparedStatement pstmtReportCFDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_report_cf.sql")));
                 PreparedStatement pstmtItemFileCFDelete = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadTmpDeletePath, "tmp_item_file_cf.sql")));

                 PreparedStatement pstmtItemCapitalDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_item_capital.sql")));
                 PreparedStatement pstmtReportCapitalDelete = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpDeletePath, "tmp_report_capital.sql")));
                 PreparedStatement pstmtItemFileCapitalDelete = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadTmpDeletePath, "tmp_item_file_capital.sql")))) {

                pstmtFileDelete.execute();

                pstmtItemBalanceDelete.execute();
                pstmtReportBalanceDelete.execute();
                pstmtItemFileBalanceDelete.execute();

                pstmtItemPLDelete.execute();
                pstmtReportPLDelete.execute();
                pstmtItemFilePLDelete.execute();

                pstmtItemCFDelete.execute();
                pstmtReportCFDelete.execute();
                pstmtItemFileCFDelete.execute();

                pstmtItemCapitalDelete.execute();
                pstmtReportCapitalDelete.execute();
                pstmtItemFileCapitalDelete.execute();

            }

            conn.commit();

            for (FileInfo fileInfo : fileInfoList) {

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_file.sql")))) {
                    pstmtInsert.setString(1, fileInfo.emitterName);
                    pstmtInsert.setString(2, fileInfo.fileName);
                    pstmtInsert.setString(3, fileInfo.fileDate.format(dateFormat));
                    pstmtInsert.setString(4, fileInfo.fileCurrency);
                    pstmtInsert.setInt(5, fileInfo.fileFactor);
                    pstmtInsert.execute();
                }

                BalanceSheetInfo balanceSheetInfo = (BalanceSheetInfo) fileInfo.sheetInfoMap.get("RICH_BALANCE");

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_item_balance.sql")))) {
                    for (BalanceItemInfo balanceItemInfo : balanceSheetInfo.balanceItemInfoList) {
                        pstmtInsert.setString(1, fileInfo.emitterName);
                        pstmtInsert.setString(2, fileInfo.fileName);
                        pstmtInsert.setInt(3, balanceItemInfo.itemIndex);
                        pstmtInsert.setInt(4, balanceItemInfo.parentItemIndex);
                        pstmtInsert.setString(5, balanceItemInfo.itemSubtotalFlag ? "subtotal" : "not_subtotal");
                        pstmtInsert.setString(6, balanceItemInfo.itemHeaderFlag ? "header" : "not_header");
                        pstmtInsert.setInt(7, balanceItemInfo.itemLevel);
                        pstmtInsert.setString(8, balanceItemInfo.itemName);
                        pstmtInsert.setString(9, balanceItemInfo.itemPureName);
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_report_balance.sql")))) {
                    for (ReportInfo reportInfo : balanceSheetInfo.reportInfoList) {
                        pstmtInsert.setString(1, fileInfo.emitterName);
                        pstmtInsert.setString(2, fileInfo.fileName);
                        pstmtInsert.setInt(3, ((SingleDimensionReportInfo) reportInfo).reportItemIndex);
                        pstmtInsert.setString(4, ((SingleDimensionReportInfo) reportInfo).reportDate.format(dateFormat));
                        pstmtInsert.setInt(5, reportInfo.reportValue);
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }

                PLSheetInfo plSheetInfo = (PLSheetInfo) fileInfo.sheetInfoMap.get("RICH_PL");

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_item_pl.sql")))) {
//                    System.out.println(plSheetInfo.plItemInfoList.size());
                    for (PLItemInfo plItemInfo : plSheetInfo.plItemInfoList) {
//                        System.out.println("itemName = " + plItemInfo.itemName + ", itemPureName = " + plItemInfo.itemPureName);
                        pstmtInsert.setString(1, fileInfo.emitterName);
                        pstmtInsert.setString(2, fileInfo.fileName);
                        pstmtInsert.setInt(3, plItemInfo.itemIndex);
                        pstmtInsert.setString(4, plItemInfo.itemName);
                        pstmtInsert.setString(5, plItemInfo.itemPureName);
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_report_pl.sql")))) {
//                            System.out.println("emitterName = " + richFileInfo.emitterName + " fileName = " + richFileInfo.fileName + " fileDate = " + richFileInfo.fileDate);
                    for (ReportInfo reportInfo : plSheetInfo.reportInfoList) {
                        pstmtInsert.setString(1, fileInfo.emitterName);
                        pstmtInsert.setString(2, fileInfo.fileName);
                        pstmtInsert.setInt(3, ((SingleDimensionReportInfo) reportInfo).reportItemIndex);
                        pstmtInsert.setString(4, ((SingleDimensionReportInfo) reportInfo).reportDate.format(dateFormat));
                        pstmtInsert.setInt(5, reportInfo.reportValue);
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }

                CFSheetInfo cfSheetInfo = (CFSheetInfo) fileInfo.sheetInfoMap.get("RICH_CF");

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_item_cf.sql")))) {
//                    System.out.println(plSheetInfo.plItemInfoList.size());
                    for (CFItemInfo cfItemInfo : cfSheetInfo.cfItemInfoList) {
//                        System.out.println("itemName = " + plItemInfo.itemName + ", itemPureName = " + plItemInfo.itemPureName);
                        pstmtInsert.setString(1, fileInfo.emitterName);
                        pstmtInsert.setString(2, fileInfo.fileName);
                        pstmtInsert.setInt(3, cfItemInfo.itemIndex);
                        pstmtInsert.setString(4, cfItemInfo.itemName);
                        pstmtInsert.setString(5, cfItemInfo.itemPureName);
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_report_cf.sql")))) {
//                            System.out.println("emitterName = " + richFileInfo.emitterName + " fileName = " + richFileInfo.fileName + " fileDate = " + richFileInfo.fileDate);
                    for (ReportInfo reportInfo : cfSheetInfo.reportInfoList) {
                        pstmtInsert.setString(1, fileInfo.emitterName);
                        pstmtInsert.setString(2, fileInfo.fileName);
                        pstmtInsert.setInt(3, ((SingleDimensionReportInfo) reportInfo).reportItemIndex);
                        pstmtInsert.setString(4, ((SingleDimensionReportInfo) reportInfo).reportDate.format(dateFormat));
                        pstmtInsert.setInt(5, reportInfo.reportValue);
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }

                CapitalSheetInfo capitalSheetInfo = (CapitalSheetInfo) fileInfo.sheetInfoMap.get("RICH_CAPITAL");

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_item_capital.sql")))) {
                    for (CapitalItemInfo capitalItemInfo : capitalSheetInfo.capitalItemInfoList) {
                        pstmtInsert.setString(1, fileInfo.emitterName);
                        pstmtInsert.setString(2, fileInfo.fileName);
                        pstmtInsert.setInt(3, capitalItemInfo.horizontalItemInfo.itemIndex);
                        pstmtInsert.setString(4, capitalItemInfo.horizontalItemInfo.itemName);
                        pstmtInsert.setString(5, capitalItemInfo.horizontalItemInfo.itemPureName);
                        pstmtInsert.setInt(6, capitalItemInfo.verticalItemInfo.itemIndex);
                        pstmtInsert.setString(7, capitalItemInfo.verticalItemInfo.itemName);
                        pstmtInsert.setString(8, capitalItemInfo.verticalItemInfo.itemPureName);
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlStageTmpInsertPath, "tmp_report_capital.sql")))) {
                    for (ReportInfo reportInfo : capitalSheetInfo.reportInfoList) {
                        pstmtInsert.setString(1, fileInfo.emitterName);
                        pstmtInsert.setString(2, fileInfo.fileName);
                        pstmtInsert.setInt(3, ((DoubleDimensionReportInfo) reportInfo).reportHorizontalItemIndex);
                        pstmtInsert.setInt(4, ((DoubleDimensionReportInfo) reportInfo).reportVerticalItemIndex);
                        pstmtInsert.setString(5, ((DoubleDimensionReportInfo) reportInfo).reportDate.format(dateFormat));
                        pstmtInsert.setInt(6, reportInfo.reportValue);
                        pstmtInsert.addBatch();
                    }
                    pstmtInsert.executeBatch();
                }
            }
            try (PreparedStatement pstmtEmitter = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_emitter.sql")));
                 PreparedStatement pstmtFile = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_file.sql")));
                 PreparedStatement pstmtPureItem = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_pure_item.sql")));
                 PreparedStatement pstmtItem = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item.sql")));
                 PreparedStatement pstmtFineItem = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_fine_item.sql")));

                 PreparedStatement pstmtTmpItemFileBalance = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadTmpInsertPath, "tmp_item_file_balance.sql")));
                 PreparedStatement pstmtTmpItemFilePL = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadTmpInsertPath, "tmp_item_file_pl.sql")));
                 PreparedStatement pstmtTmpItemFileCF = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadTmpInsertPath, "tmp_item_file_cf.sql")));
                 PreparedStatement pstmtTmpItemFileCapital = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadTmpInsertPath, "tmp_item_file_capital.sql")));

                 PreparedStatement pstmtItemFileBalance1 = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_balance_1.sql")));
                 PreparedStatement pstmtItemFileBalance2 = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_balance_2.sql")));
                 PreparedStatement pstmtItemFileBalanceStat = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_balance_statistic.sql")));

                 PreparedStatement pstmtItemFilePL1 = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_pl_1.sql")));
                 PreparedStatement pstmtItemFilePLStat = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_pl_statistic.sql")));

                PreparedStatement pstmtItemFileCF1 = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_cf_1.sql")));
                PreparedStatement pstmtItemFileCFStat = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_cf_statistic.sql")));

                PreparedStatement pstmtItemFileCapital1 = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_capital_1.sql")));
                PreparedStatement pstmtItemFileCapitalStat = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, "tbl_item_file_capital_statistic.sql")))) {

                pstmtEmitter.execute();
                pstmtFile.execute();
                pstmtPureItem.execute();
                pstmtItem.execute();
                pstmtFineItem.execute();

                pstmtTmpItemFileBalance.execute();
                pstmtTmpItemFilePL.execute();
                pstmtTmpItemFileCF.execute();
                pstmtTmpItemFileCapital.execute();

                pstmtItemFileBalance1.execute();
                pstmtItemFileBalance2.execute();
                pstmtItemFileBalanceStat.execute();

                pstmtItemFilePL1.execute();
                pstmtItemFilePLStat.execute();

                pstmtItemFileCF1.execute();
                pstmtItemFileCFStat.execute();

                pstmtItemFileCapital1.execute();
                pstmtItemFileCapitalStat.execute();
            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String getQuery(Path filePath) {
        String queryString = null;

        try {
            queryString = Files.lines(Paths.get(rb.getString("sql_directory"), filePath.toString()))
                            .collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return queryString;
    }
}
