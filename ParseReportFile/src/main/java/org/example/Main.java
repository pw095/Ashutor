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
            .filter(p -> !p.toString().contains("~$"))
            .map(FileInfo::new)
            .forEach(rawFileInfoList::add);

//        System.out.println("size = " + ((RawBalanceSheetInfo) rawFileInfoList.get(0).balanceSheetInfoList.get(0)).itemList.size());
        /*for (FileInfo rawFileInfo : rawFileInfoList) {
            System.out.println(rawFileInfo.fileName);
            FileInfo.getRich(rawFileInfo);
        }*/
//        rawFileInfoList.stream().map(FileInfo::getRich).forEach(p -> System.out.println(p.fileName));
        rawFileInfoList.stream()
            .map(FileInfo::getRich)
            .forEach(richFileInfoList::add);


        for (ItemInfo itemInfo : ((RichBalanceSheetInfo) richFileInfoList.get(0).balanceSheetInfoList.get(0)).itemInfoList) {
//            if (itemInfo.itemHeaderFlag || itemInfo.itemSubtotalFlag) {
            System.out.println("itemIndex = " + itemInfo.itemIndex + ", itemParentIndex = " + itemInfo.parentItemIndex + " " + itemInfo.itemHeaderFlag + " " + itemInfo.itemSubtotalFlag + " " + itemInfo.itemName + " |||| " + itemInfo.itemPureName);
//            }
    }



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



        /*
        try (Connection conn = DriverManager.getConnection(rb.getString("url"))) {

            conn.setAutoCommit(false);

            String sqlInsertPath = Paths.get(rb.getString("tmp_directory"), rb.getString("insert_directory")).toString();
            String sqlDeletePath = Paths.get(rb.getString("tmp_directory"), rb.getString("delete_directory")).toString();

            String[] tableList = {"file_info", "report_info", "code_info", "word_info"};

            for (String tab : tableList) {
                try (PreparedStatement pstmtDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, rb.getString(tab)).toString()))) {
                    pstmtDelete.execute();
                }
            }

            conn.commit();

            for (ExcelInfo excelInfo : excelInfoList) {
                for (String tab : tableList) {
                    try (PreparedStatement pstmtInsert = conn.prepareStatement(getQuery(Paths.get(sqlInsertPath, rb.getString(tab)).toString()))) {
                        switch (tab) {
                            case "file_info":
                                pstmtInsert.setString(1, excelInfo.fileInfo.fileName);
                                pstmtInsert.setString(2, excelInfo.fileInfo.fileDate.format(dateFormat));
                                pstmtInsert.setString(3, excelInfo.fileInfo.fileCurrency);
                                pstmtInsert.setInt(4, excelInfo.fileInfo.fileFactor);
                                pstmtInsert.addBatch();
                                break;
                            case "report_info":
                                for (ReportInfo reportInfo : excelInfo.reportInfoList) {
                                    pstmtInsert.setString(1, reportInfo.fileName);
                                    pstmtInsert.setInt(2, reportInfo.codeIndex);
                                    pstmtInsert.setString(3, reportInfo.reportDate.format(dateFormat));
                                    pstmtInsert.setInt(4, reportInfo.reportValue);
                                    pstmtInsert.addBatch();
                                }
                                break;
                            case "code_info":
                                for (CodeInfo codeInfo : excelInfo.codeInfoList) {
                                    pstmtInsert.setString(1, codeInfo.fileName);
                                    pstmtInsert.setInt(2, codeInfo.codeIndex);
                                    pstmtInsert.setString(3, codeInfo.codeName);
                                    pstmtInsert.setString(4, String.join(" ", codeInfo.codeWordList));
                                    pstmtInsert.addBatch();
                                }
                                break;
                            case "word_info":
                                for (CodeInfo codeInfo : excelInfo.codeInfoList) {
                                    for (int ind = 0; ind < codeInfo.codeWordList.size(); ++ind) {
                                        pstmtInsert.setString(1, codeInfo.fileName);
                                        pstmtInsert.setInt(2, codeInfo.codeIndex);
                                        pstmtInsert.setInt(3, ind + 1);
                                        pstmtInsert.setString(4, codeInfo.codeWordList.get(ind));
                                        pstmtInsert.addBatch();
                                    }
                                }
                                break;
                            default:
                                throw new RuntimeException();
                        }
                        pstmtInsert.executeBatch();
                        conn.commit();
                    }
                }
            }

            try (PreparedStatement pstmtFile = conn.prepareStatement(getQuery(rb.getString("file")));
                 PreparedStatement pstmtFineCode = conn.prepareStatement(getQuery(rb.getString("fine_code")));
                 PreparedStatement pstmtWord = conn.prepareStatement(getQuery(rb.getString("word")));
                 PreparedStatement pstmtWordInCode = conn.prepareStatement(getQuery(rb.getString("word_in_code")));
                 PreparedStatement pstmtCode = conn.prepareStatement(getQuery(rb.getString("code")));
                 PreparedStatement pstmtCodeInFile = conn.prepareStatement(getQuery(rb.getString("code_in_file")));
                 PreparedStatement pstmtStat = conn.prepareStatement(getQuery(rb.getString("stat")))) {
                pstmtFile.execute();
                pstmtFineCode.execute();
                pstmtWord.execute();
                pstmtWordInCode.execute();
                pstmtCode.execute();
                pstmtCodeInFile.execute();
                pstmtStat.execute();
            }

            --conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
*/
    }

    public static String getQuery(String filePath) {
        String queryString = null;

        try {
            queryString = Files.lines(Paths.get(rb.getString("sql_directory"), filePath)).collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return queryString;
    }
}
