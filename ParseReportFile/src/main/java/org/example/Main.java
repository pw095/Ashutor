package org.example;

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


    static class FileInfo {
        public String fileName;
        public LocalDate fileDate;
        public String fileCurrency;
        public int fileFactor;
    }

    static class CodeInfo {
        public String fileName;
        public int codeIndex;
        public String codeName;
        public List<String> codeWordList;
    }

    static class ReportInfo {
        public String fileName;
        public int codeIndex;
        public LocalDate reportDate;
        public int reportValue;
    }

    static class ExcelInfo {

        FileInfo fileInfo;
        List<CodeInfo> codeInfoList;
        List<ReportInfo> reportInfoList;

        public ExcelInfo(FileInfo fileInfo, List<CodeInfo> codeInfoList, List<ReportInfo> reportInfoList) {

            this.fileInfo = fileInfo;
            this.codeInfoList = codeInfoList;
            this.reportInfoList = reportInfoList;

        }

    }

    public static ExcelInfo parseFile(Path path) {

        FileInfo fileInfo = new FileInfo();
        List<CodeInfo> codeInfoList = new ArrayList<>();
        List<ReportInfo> reportInfoList = new ArrayList<>();

        fileInfo.fileName = path.getFileName().toString();

        fileInfo.fileDate = LocalDate.of(Integer.parseInt(fileInfo.fileName.replaceAll("\\.xlsx", "")), Month.DECEMBER, 31);
        Map<Integer, LocalDate> yearsMap = new HashMap<>();

        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("BALANCE")) {
                    for (Row row : workSheet) {
                        if (row.getRowNum() == 0) {
                            for (Cell cell : row) {
                                if (cell.getColumnIndex() == 0) {
                                    String stringValue;
                                    if (cell.getCellType() == CellType.STRING) {
                                        stringValue = cell.getStringCellValue();
                                    } else {
                                        throw new RuntimeException("Invalid cell type: " + cell.getCellType().toString());
                                    }

                                    if (stringValue.contains("млн")) {
                                        fileInfo.fileFactor = 6;
                                    } else if (stringValue.contains("тыс")) {
                                        fileInfo.fileFactor = 3;
                                    }

                                    if (stringValue.contains("руб")) {
                                        fileInfo.fileCurrency = "RUB";
                                    }
                                } else {
                                    if (cell.getCellType() == CellType.NUMERIC) {
                                        yearsMap.put(cell.getColumnIndex(), LocalDate.of((int) cell.getNumericCellValue(), Month.DECEMBER, 31));
                                    } else {
                                        throw new RuntimeException("Invalid cell type: " + cell.getCellType().toString());
                                    }
                                }
                            }
                        } else {

                            Iterator<Cell> cellIterator = row.iterator();
                            CodeInfo codeInfo = new CodeInfo();

                            while (cellIterator.hasNext()) {
                                Cell cell = cellIterator.next();
                                ReportInfo reportInfo = new ReportInfo();

                                if (cell.getColumnIndex() == 0) {

                                    codeInfo.fileName = fileInfo.fileName;
                                    codeInfo.codeIndex = cell.getRowIndex();
                                    codeInfo.codeName = cell.getStringCellValue();
                                    codeInfo.codeWordList = Arrays.stream(codeInfo.codeName.toLowerCase().split("[\\p{Punct}\\p{Blank}]")).filter(p -> !p.equalsIgnoreCase("")).collect(Collectors.toList());

                                    codeInfoList.add(codeInfo);

                                } else {

                                    reportInfo.fileName = codeInfo.fileName;
                                    reportInfo.codeIndex = codeInfo.codeIndex;
                                    reportInfo.reportDate = yearsMap.get(cell.getColumnIndex());
                                    if (cell.getCellType() == CellType.NUMERIC) {
                                        reportInfo.reportValue = (int) cell.getNumericCellValue();
                                    }

                                    reportInfoList.add(reportInfo);

                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ExcelInfo(fileInfo, codeInfoList, reportInfoList);
    }

    public static void main(String[] args) throws IOException{

        List<ExcelInfo> excelInfoList = new ArrayList<>();

        System.out.println(rb..getString("source_directory"));
        System.out.println("Д".getBytes("ISO-8859-1")[0]);
        System.out.println(new String(rb.getString("source_directory").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
        System.out.println(new String(rb.getString("source_directory").getBytes("UTF-8"), Charset.forName("ISO-8859-1")));
//        StandardCharsets.US_ASCII
        /*for (byte bt : rb.getString("source_directory").getBytes("IBM866")) {

            System.out.println(bt);
            break;
        }*/
/*

        System.out.println("Д".getBytes("IBM866")[0]);
        for (byte bt : rb.getString("source_directory").getBytes("ISO-8859-1")) {
            System.out.println(bt);
            break;
        }
*/

        if (2 > 1 ) {
            return;
        }
        Files.walk(Paths.get(rb.getString("source_directory")))
            .filter(p -> p.toString().endsWith(".xlsx"))
            .map(Main::parseFile)
            .forEach(excelInfoList::add);

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
/*
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
*/
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

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
