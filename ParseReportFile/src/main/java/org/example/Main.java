package org.example;

import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {

    static ResourceBundle rb;
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static {
        rb = ResourceBundle.getBundle("application");
    }

    public static class GroupItem {
        String reportDate;
        int ifbId;

        GroupItem(String str) {
            String[] arr = str.split(": ");
            reportDate = arr[0];
            ifbId = Integer.parseInt(arr[1]);
        }
        GroupItem(String reportDate, int ifbId) {
            this.reportDate = reportDate;
            this.ifbId = ifbId;
        }

    }

    public static class QueryResult {
        String fineItemCode;
        String hierPureItemPath;
        int level;
        int ifbIndex;
        int cnt;
        String groupCnct;
        List<GroupItem> groupItemList;

        QueryResult() {

        }

        QueryResult(String fineItemCode, String hierPureItemPath, int level, int ifbIndex, int cnt, String groupCnct) {

            this.fineItemCode = fineItemCode;
            this.hierPureItemPath = hierPureItemPath;
            this.level = level;
            this.ifbIndex = ifbIndex;
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

        String sqlGetPath = Paths.get(rb.getString("get_directory")).toString();

        Map<String, List<QueryResult>> emitterBalanceInfo = new HashMap<>();
        List<FineItemInfo> fineItemInfoList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {
            try (PreparedStatement pstmtFineItemInfo = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, rb.getString("get_fine_item_info"))));
                 ResultSet rs = pstmtFineItemInfo.executeQuery()) {
                while (rs.next()) {
                    String fineItemCode = rs.getString("fine_item_code");
                    String fineItemName = rs.getString("fine_item_name");
                    String hierPureItemPath = rs.getString("hier_pure_item_path");
                    fineItemInfoList.add(new FineItemInfo(fineItemCode, fineItemName, hierPureItemPath));
                }
            }

            try (PreparedStatement pstmtEmitter = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, "get_emitters.sql")));
                 PreparedStatement pstmtGet = conn.prepareStatement(getQuery(Paths.get(sqlGetPath, rb.getString("get_agg_item"))));
                 ResultSet rs = pstmtEmitter.executeQuery()) {

                while (rs.next()) {
                    String emitterName = rs.getString("emitter_name");

                    pstmtGet.setString(1, emitterName);
                    List<QueryResult> queryResultList = new ArrayList<>();
                    try (ResultSet rsGet = pstmtGet.executeQuery()) {
                        while (rsGet.next()) {
                            String fineItemCode = rsGet.getString("fine_item_code");
                            String hierPureItemPath = rsGet.getString("hier_pure_item_path");
                            int level = rsGet.getInt("level");
                            int ifbIndex = rsGet.getInt("ifb_index");
                            int cnt = rsGet.getInt("cnt");
                            String groupCnct = rsGet.getString("group_cnct");
                            queryResultList.add(new QueryResult(fineItemCode, hierPureItemPath, level, ifbIndex, cnt, groupCnct));
                        }
                    }
                    emitterBalanceInfo.put(emitterName, queryResultList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (FileOutputStream file = new FileOutputStream(new File("C:\\Users\\pw095\\Documents\\Git\\Alamedin\\temp_merge_file.xlsx"))) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet emitterListSheet = workbook.createSheet("Emitter List");

            int rowNum = 0;
            Row headerRow = emitterListSheet.createRow(rowNum);
            headerRow.createCell(0).setCellValue("EmitterList");
            ++rowNum;

            for (String emitterName : emitterBalanceInfo.keySet()) {
                Row row = emitterListSheet.createRow(rowNum);
                row.createCell(0).setCellValue(emitterName);
            }

            XSSFSheet fineItemDictSheet = workbook.createSheet("Fine Item Dictionary");

            rowNum = 0;
            headerRow = fineItemDictSheet.createRow(rowNum);

            headerRow.createCell(0).setCellValue("fine_item_code");
            headerRow.createCell(1).setCellValue("fine_item_name");
            headerRow.createCell(2).setCellValue("hier_pure_item_path");

            for (FineItemInfo fineItemInfo : fineItemInfoList) {
                Row row = fineItemDictSheet.createRow(++rowNum);
                row.createCell(0).setCellValue(fineItemInfo.fineItemCode);
                row.createCell(1).setCellValue(fineItemInfo.fineItemName);
                row.createCell(2).setCellValue(fineItemInfo.hierPureItemPath);
            }

            for (String emitterName : emitterBalanceInfo.keySet()) {
                List<QueryResult> queryResultList = emitterBalanceInfo.get(emitterName);
                List<String> arrayList = queryResultList.stream()
                    .map(p -> p.groupItemList)
                    .flatMap(p -> p.stream())
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
                row.createCell(3).setCellValue("IfbIndex");
                row.createCell(4).setCellValue("Count");

                for (int ind=0; ind < arrayList.size(); ++ind) {
                    row.createCell(5+ind).setCellValue(arrayList.get(ind));
                }

                for (int jnd=0; jnd < queryResultList.size(); ++jnd) {
                    Row newRow = emitterBalaceInfoSheet.createRow(jnd+1);
                    newRow.createCell(0).setCellValue(queryResultList.get(jnd).fineItemCode);
                    newRow.createCell(1).setCellValue(queryResultList.get(jnd).hierPureItemPath);
                    newRow.createCell(2).setCellValue(queryResultList.get(jnd).level);
                    newRow.createCell(3).setCellValue(queryResultList.get(jnd).ifbIndex);
                    newRow.createCell(4).setCellValue(queryResultList.get(jnd).cnt);

                    for (int ind=0; ind < queryResultList.get(jnd).groupItemList.size(); ++ind) {
                        int knd = arrayList.indexOf(queryResultList.get(jnd).groupItemList.get(ind).reportDate);
                        newRow.createCell(5+knd).setCellValue(queryResultList.get(jnd).groupItemList.get(ind).ifbId);
                    }
                }
            }
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





//        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
//        queryRusultList;

/*
        class PreMatchQueryResult
        {

            int ifbID;
            int parentIfbID;
            int parentReport;
            int ifbIndex;
            int level;
            String hierFineItemPath;
            String hierPureItemPath;

            PreMatchQueryResult(int ifbID, int ifbIndex, int level, String hierFineItemPath, String hierPureItemPath) {
                this.ifbID = ifbID;
                this.ifbIndex = ifbIndex;
                this.level = level;
                this.hierFineItemPath = hierFineItemPath;
                this.hierPureItemPath = hierPureItemPath;
            }

            PreMatchQueryResult(int parentIfbID, int parentReport, PreMatchQueryResult preMatchQueryResult) {
                this.ifbID = preMatchQueryResult.ifbID;
                this.parentIfbID = parentIfbID;
                this.parentReport = parentReport;
                this.ifbIndex = preMatchQueryResult.ifbIndex;
                this.level = preMatchQueryResult.level;
                this.hierFineItemPath = preMatchQueryResult.hierFineItemPath;
                this.hierPureItemPath = preMatchQueryResult.hierPureItemPath;
            }
        }

        List<List<PreMatchQueryResult>> aa = new ArrayList<>();

        List<PreMatchQueryResult> bb = new ArrayList<>();
        bb.add(new PreMatchQueryResult(42, 0, 0, "", "активы"));
        bb.add(new PreMatchQueryResult(43, 1, 1, "", "активы > внеоборотные активы"));
        bb.add(new PreMatchQueryResult(44, 2, 2, "", "активы > внеоборотные активы > основные средства"));
        bb.add(new PreMatchQueryResult(45, 3, 2, "", "активы > внеоборотные активы > нематериальные активы"));
        bb.add(new PreMatchQueryResult(46, 3, 2, "", "активы > внеоборотные активы > инвестиция в зависимую компанию"));
        bb.add(new PreMatchQueryResult(47, 3, 2, "", "активы > внеоборотные активы > долгосрочный заем выданный"));
        bb.add(new PreMatchQueryResult(48, 3, 2, "", "активы > внеоборотные активы > отложенные налоговые активы"));
        bb.add(new PreMatchQueryResult(49, 3, 2, "", "активы > внеоборотные активы > прочие внеоборотные активы"));
        bb.add(new PreMatchQueryResult(50, 3, 2, "", "активы > внеоборотные активы > итого внеоборотные активы"));

        aa.add(bb);

        List<PreMatchQueryResult> cc = new ArrayList<>();
        cc.add(new PreMatchQueryResult(198, 0, 0, "", "активы"));
        cc.add(new PreMatchQueryResult(199, 1, 1, "", "активы > внеоборотные активы"));
        cc.add(new PreMatchQueryResult(200, 2, 2, "", "активы > внеоборотные активы > основные средства"));
        cc.add(new PreMatchQueryResult(201, 3, 2, "", "активы > внеоборотные активы > нематериальные активы"));
        cc.add(new PreMatchQueryResult(202, 3, 2, "", "активы > внеоборотные активы > активы в форме права пользования"));
        cc.add(new PreMatchQueryResult(203, 3, 2, "", "активы > внеоборотные активы > отложенные налоговые активы"));
        cc.add(new PreMatchQueryResult(204, 3, 2, "", "активы > внеоборотные активы > прочие внеоборотные активы"));
        cc.add(new PreMatchQueryResult(205, 3, 2, "", "активы > внеоборотные активы > итого внеоборотные активы"));

        aa.add(cc);

        for (int ind=0; ind < aa.size(); ++ind) {
            if (ind == 0) {
                continue;
            } else {
                List<PreMatchQueryResult> result1 = aa.get(ind); // 1 -- наш очередной отчёт, перебираем все отчёты, начиная со второго
                // в него надо записать ссылку, на родительский отчёт
                outerloop:
                for (int jnd=0; jnd < ind; ++jnd) {
                    List<PreMatchQueryResult> result2 = aa.get(jnd); // 0 -- все предшествующие отчёты по очереди
                    for (int xnd = 0; xnd < result1.size(); ++xnd) {
                        for (int ynd = 0; ynd < result2.size(); ++ynd) {
                            if (result1.get(xnd).hierPureItemPath.equals(result2.get(ynd).hierPureItemPath)) {
                                result1.get(xnd).parentIfbID = result2.get(ynd).ifbID;
                                result1.get(xnd).parentReport = jnd;
                                break outerloop;
                            }
                        }
                    }
                }
            }

        }*/

    public static void main(String[] args) throws IOException{

        List<FileInfo> rawFileInfoList = new ArrayList<>();
        List<FileInfo> richFileInfoList = new ArrayList<>();

/*        get();

        if (2>1) {
            return;
        }*/

        Files.walk(Paths.get(new String(rb.getString("source_directory").getBytes("ISO-8859-1"), Charset.forName("UTF-8"))))
            .filter(p -> p.toString().endsWith(".xlsx"))
            .filter(p -> !p.toString().contains("cmn"))
            .filter(p -> !p.toString().contains("~$"))
            .filter(p -> p.toString().contains("36,6") || p.toString().contains("Детский мир"))
//            .filter(p -> p.toString().contains("2020"))
            .map(FileInfo::new)
            .forEach(rawFileInfoList::add);

//        System.out.println("size = " + ((RawBalanceSheetInfo) rawFileInfoList.get(0).balanceSheetInfoList.get(0)).itemList.size());
/*        for (FileInfo rawFileInfo : rawFileInfoList) {
            System.out.println(rawFileInfo.emitterName + " " + rawFileInfo.fileName);
            FileInfo.getRich(rawFileInfo);
        }*/
//        rawFileInfoList.stream().map(FileInfo::getRich).forEach(p -> System.out.println(p.fileName));
/*        if (1 < 2) {
            return;
        }*/
        rawFileInfoList.stream()
            .map(FileInfo::getRich)
            .forEach(richFileInfoList::add);

/*
        for (ItemInfo itemInfo : ((RichBalanceSheetInfo) richFileInfoList.get(0).balanceSheetInfoList.get(0)).itemInfoList) {
//            if (itemInfo.itemHeaderFlag || itemInfo.itemSubtotalFlag) {
            System.out.println("itemIndex = " + itemInfo.itemIndex + ", itemParentIndex = " + itemInfo.parentItemIndex + " " + itemInfo.itemHeaderFlag + " " + itemInfo.itemSubtotalFlag + " " + itemInfo.itemName + " |||| " + itemInfo.itemPureName);
//            }
        }
*/


//        for (int ii = 0; ii < richBalanceSheetInfo.itemInfoList.size(); ++ii) {


//            if (richBalanceSheetInfo.itemInfoList.get(ii).itemHeaderFlag) {
////                parentIndex = richBalanceSheetInfo.itemInfoList.get(ii).itemIndex;
//                parentIndex = richBalanceSheetInfo.itemInfoList.subList(indStart,ii).stream().filter(p -> p.itemHeaderFlag).sorted(byIndex)
//                    .skip(kk/*>0 ? kk-1 : 0*/).map(p -> p.itemIndex).findFirst().orElse(-4);
//                System.out.println("itemName = " + richBalanceSheetInfo.itemInfoList.get(ii).itemName + ", kk = " + kk + ", parent = " + parentIndex);
//                richBalanceSheetInfo.itemInfoList.subList(indStart,ii).stream().filter(p -> p.itemHeaderFlag).sorted(byIndex).forEach(p -> System.out.println(p.itemIndex + " " + p.itemName));
//            }
//            if (ii > 0
//                && richBalanceSheetInfo.itemInfoList.get(ii).parentItemIndex == -1 // Родительский index не задан
//                && /*(richBalanceSheetInfo.itemInfoList.get(ii-1).parentItemIndex != 0 && richBalanceSheetInfo.itemInfoList.get(ii).itemHeaderFlag
//                || !*/richBalanceSheetInfo.itemInfoList.get(ii).itemHeaderFlag) {
//                richBalanceSheetInfo.itemInfoList.get(ii).parentItemIndex = parentIndex;
//            }
///*            if (richBalanceSheetInfo.itemInfoList.get(ii).itemHeaderFlag) {
//                ++kk;
//            }*/
//            if (richBalanceSheetInfo.itemInfoList.get(ii).itemSubtotalFlag) {
//                ++kk;
//            }
//            if (richBalanceSheetInfo.itemInfoList.get(ii).parentItemIndex == 0 && richBalanceSheetInfo.itemInfoList.get(ii).itemSubtotalFlag) {
//                kk = 0;
//                indStart = ii + 1;
//            }
//        }


        String sqlInsertPath = Paths.get(rb.getString("tmp_directory"), rb.getString("insert_directory")).toString();
        String sqlDeletePath = Paths.get(rb.getString("tmp_directory"), rb.getString("delete_directory")).toString();
        String sqlTransformLoadPath = Paths.get("transform_load").toString();
        /*        System.out.println(";");
        System.out.println(Paths.get(sqlDeletePath, rb.getString("item_info")));
        System.out.println(getQuery(Paths.get(sqlDeletePath, rb.getString("item_info"))));
        System.out.println(";");*/
        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFileDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, rb.getString("file_info"))));
                 PreparedStatement pstmtItemDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, rb.getString("item_info"))));
                 PreparedStatement pstmtReportDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, rb.getString("report_info"))));
                 PreparedStatement pstmtItemFileBalanceDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, "tmp_item_file_balance.sql")))) {

                pstmtFileDelete.execute();
                pstmtItemDelete.execute();
                pstmtReportDelete.execute();
                pstmtItemFileBalanceDelete.execute();

            }

            conn.commit();

            for (FileInfo richFileInfo : richFileInfoList) {

                try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlInsertPath, rb.getString("file_info"))))) {
                    pstmtInsert.setString(1, richFileInfo.emitterName);
                    pstmtInsert.setString(2, richFileInfo.fileName);
                    pstmtInsert.setString(3, richFileInfo.fileDate.format(dateFormat));
                    pstmtInsert.setString(4, richFileInfo.fileCurrency);
                    pstmtInsert.setInt(5, richFileInfo.fileFactor);
                    pstmtInsert.execute();
                }

                for (SheetInfo sheetInfo : richFileInfo.balanceSheetInfoList) {

                    RichBalanceSheetInfo richBalanceSheetInfo = (RichBalanceSheetInfo) sheetInfo;

                    try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlInsertPath, rb.getString("item_info"))))) {
                        for (ItemInfo itemInfo : richBalanceSheetInfo.itemInfoList) {
                            pstmtInsert.setString(1, richFileInfo.emitterName);
                            pstmtInsert.setString(2, richFileInfo.fileName);
                            pstmtInsert.setString(3, richFileInfo.fileDate.format(dateFormat));
                            pstmtInsert.setInt(4, itemInfo.itemIndex);
                            pstmtInsert.setInt(5, itemInfo.parentItemIndex);
                            pstmtInsert.setString(6, itemInfo.itemSubtotalFlag ? "subtotal" : "not_subtotal");
                            pstmtInsert.setString(7, itemInfo.itemHeaderFlag ? "header" : "not_header");
                            pstmtInsert.setInt(8, itemInfo.itemLevel);
                            pstmtInsert.setString(9, itemInfo.itemName);
                            pstmtInsert.setString(10, itemInfo.itemPureName);
                            pstmtInsert.addBatch();
                        }
                        pstmtInsert.executeBatch();
                    }

                    try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlInsertPath, rb.getString("report_info"))))) {
                        System.out.println("emitterName = " + richFileInfo.emitterName + " fileName = " + richFileInfo.fileName + " fileDate = " + richFileInfo.fileDate);
                        for (ReportInfo reportInfo : richBalanceSheetInfo.reportInfoList) {
                            pstmtInsert.setString(1, richFileInfo.emitterName);
                            pstmtInsert.setString(2, richFileInfo.fileName);
                            pstmtInsert.setString(3, richFileInfo.fileDate.format(dateFormat));
                            pstmtInsert.setInt(4, reportInfo.reportItemIndex);
                            pstmtInsert.setString(5, reportInfo.reportDate.format(dateFormat));
                            pstmtInsert.setInt(6, reportInfo.reportValue);
                            pstmtInsert.addBatch();
                        }
                        pstmtInsert.executeBatch();
                    }
                }
            }
            try (PreparedStatement pstmtEmitter = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, rb.getString("emitter"))));
                 PreparedStatement pstmtFile = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, rb.getString("file"))));
                 PreparedStatement pstmtPureItem = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, rb.getString("pure_item"))));
                 PreparedStatement pstmtItem = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, rb.getString("item"))));
                 PreparedStatement pstmtFineItem = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, rb.getString("fine_item"))));
                 PreparedStatement pstmtTmpItemFileBalance = conn.prepareStatement(getQuery(Paths.get(sqlInsertPath, "tmp_item_file_balance.sql")));
//                 PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlInsertPath, rb.getString("item_file_balance_2"))));
                 PreparedStatement pstmtItemFileBalance1 = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, rb.getString("item_file_balance_1"))));
                 PreparedStatement pstmtItemFileBalance2 = conn.prepareStatement(getQuery(Paths.get(sqlTransformLoadPath, rb.getString("item_file_balance_2"))))) {

                pstmtEmitter.execute();
                pstmtFile.execute();
                pstmtPureItem.execute();
                pstmtItem.execute();
                pstmtFineItem.execute();
                pstmtTmpItemFileBalance.execute();
                pstmtItemFileBalance1.execute();
                pstmtItemFileBalance2.execute();

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
