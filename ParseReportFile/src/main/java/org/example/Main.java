package org.example;

import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.stream.Collectors;

public class Main {

    static ResourceBundle rb;
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static {
        rb = ResourceBundle.getBundle("application");
    }

    public static void main(String[] args) throws IOException{

        List<FileInfo> rawFileInfoList = new ArrayList<>();
        List<FileInfo> richFileInfoList = new ArrayList<>();

        /*Comparator<PossibleParent> byLevDistance = new Comparator<PossibleParent>() {
            @Override
            public int compare(PossibleParent o1, PossibleParent o2) {
                return o1.lDistance - o2.lDistance;
            }
        };*/
/*        Comparator<ItemInfo> byIndex = new Comparator<ItemInfo>() {
            @Override
            public int compare(ItemInfo o1, ItemInfo o2) {
                return o2.itemIndex - o1.itemIndex;
            }
        };*/

//        System.out.println(new String(rb.getString("source_directory").getBytes(StandardCharsets.ISO_8859_1), Charset.forName("UTF-8")));

        Files.walk(Paths.get(new String(rb.getString("source_directory").getBytes("ISO-8859-1"), Charset.forName("UTF-8"))))
            .filter(p -> p.toString().endsWith(".xlsx"))
            .filter(p -> !p.toString().contains("cmn"))
            .filter(p -> !p.toString().contains("~$"))
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
/*        System.out.println(";");
        System.out.println(Paths.get(sqlDeletePath, rb.getString("item_info")));
        System.out.println(getQuery(Paths.get(sqlDeletePath, rb.getString("item_info"))));
        System.out.println(";");*/
        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);



            try (PreparedStatement pstmtFileDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, rb.getString("file_info"))));
                 PreparedStatement pstmtItemDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, rb.getString("item_info"))));
                 PreparedStatement pstmtReportDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, rb.getString("report_info"))))) {

                pstmtFileDelete.execute();
                pstmtItemDelete.execute();
                pstmtReportDelete.execute();

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
