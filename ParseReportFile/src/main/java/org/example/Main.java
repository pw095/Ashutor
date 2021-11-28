package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
//        public String codeName;
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
/*
    static class TupleCode {
        public int index;
        public String stmtName;
        public int fileDate;
    }

    static class Tuple {
        public String currencyName;
        public int factor;
        public int index;
        public int reportYear;
        public String stmtName;
        public int stmtValue;
        public int fileYear;

        public void print() {
            System.out.println("year = " + this.reportYear + ", index = " + index + ", stmtName = " + stmtName + ", stmtValue = " + stmtValue);
        }
    }
*/

    public static ExcelInfo parseFile(Path path) {

        FileInfo fileInfo = new FileInfo();
        List<CodeInfo> codeInfoList = new ArrayList<>();
        List<ReportInfo> reportInfoList = new ArrayList<>();

        fileInfo.fileName = path.getFileName().toString();

        fileInfo.fileDate = LocalDate.of(Integer.parseInt(fileInfo.fileName.replaceAll("\\.xlsx", "")), Month.DECEMBER, 31);
        Map<Integer, LocalDate> yearsMap = new HashMap<>();

//        List<TupleCode> tupleCodes = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            for (Sheet workSheet : workbook) {

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
                                if (cell.getCellType() == CellType.NUMERIC) {//                                            stringValue = .format(dateFormat);
                                    yearsMap.put(cell.getColumnIndex(), LocalDate.of((int) cell.getNumericCellValue(), Month.DECEMBER, 31));
                                } else {
                                    throw new RuntimeException("Invalid cell type: " + cell.getCellType().toString());
                                }
                            }
                        }
                    } else {
                        Iterator<Cell> cellIterator = row.iterator();
                        CodeInfo codeInfo = new CodeInfo();
//                        String header = "";
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            ReportInfo reportInfo = new ReportInfo();

//                            TupleCode tupleCode = new TupleCode();
//                            tupleCode.index = cell.getRowIndex();
//                            tupleCode.fileYear = fileYear;
//                            Tuple tuple = new Tuple();
//                            tuple.fileYear = fileYear;
//                            tuple.factor = factor;
//                            tuple.currencyName = currencyName;
//                            tuple.index = cell.getRowIndex();
                            if (cell.getColumnIndex() == 0) {//                                    header = cell.getStringCellValue();
                                codeInfo.fileName = fileInfo.fileName;
                                codeInfo.codeIndex = cell.getRowIndex();
                                codeInfo.codeName = cell.getStringCellValue();
                                codeInfo.codeWordList = Arrays.stream(codeInfo.codeName.toLowerCase().split("[\\p{Punct}\\p{Blank}]")).filter(p -> !p.equalsIgnoreCase("")).collect(Collectors.toList());
//                                System.out.println(codeInfo.codeWordList.size());
                                codeInfoList.add(codeInfo);
//                                    tupleCode.stmtName = cell.getStringCellValue();
//                                    tupleCodes.add(tupleCode);
                                //                                        System.out.println("cell = " + cell.getStringCellValue());
//                                        System.out.println("fontIndex = " + cell.getCellStyle().getFontIndex());
//                                        Font font = workbook.getFontAt(cell.getCellStyle().getFontIndex());
//                                        System.out.println("BOLD: " +font.getBold());
//                                        System.out.println("fontName" + font.getFontName());
                            } else {
                                reportInfo.fileName = codeInfo.fileName;
                                reportInfo.codeIndex = codeInfo.codeIndex;
//                                    reportInfo.codeName = codeInfo.codeName;
                                reportInfo.reportDate = yearsMap.get(cell.getColumnIndex());
                                if (cell.getCellType() == CellType.NUMERIC) {
                                    reportInfo.reportValue = (int) cell.getNumericCellValue();
                                }
                                reportInfoList.add(reportInfo);
//                                    tuple.reportYear = years.get(cell.getColumnIndex() - 1);
//                                    tuple.stmtName = header;
//                                    if (cell.getCellType() == CellType.NUMERIC) {
//                                        tuple.stmtValue = (int) cell.getNumericCellValue();
//                                    }
//                                    tuples.add(tuple);
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

    public static void main(String[] args)  {



//        List<Integer> years = new ArrayList<>();

//        List<Tuple> tuples = new ArrayList<>();
        List<String> files = new ArrayList<>();

//        List<TupleCode> tupleCodes;
        files.add("2020.xlsx");
        files.add("2021.xlsx");
//        String sqlTruncString = "DELETE FROM tmp_raw_code";
//        String sqlInsertString = "INSERT INTO tmp_raw_code(file_name, file_year, code_number, code_name, fine_code_name, word_number, word_name) VALUES(:file_name, :file_year, :code_number, :code_name, :fine_code_name, :word_number, :word_name)";
        try (Connection conn = DriverManager.getConnection(rb.getString("url"))
//             PreparedStatement pstmtTrunc = conn.prepareStatement(sqlTruncString);
//             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertString)
        ) {

            conn.setAutoCommit(false);
/*
            pstmtTrunc.execute();
            conn.commit();*/
            String sqlInsertPath = Paths.get(rb.getString("tmp_directory"), rb.getString("insert_directory")).toString();
            String sqlDeletePath = Paths.get(rb.getString("tmp_directory"), rb.getString("delete_directory")).toString();

            String[] tableList = {"file_info", "report_info", "code_info", "word_info"};
            for (String tab : tableList) {
                try (PreparedStatement pstmtDelete = conn.prepareStatement(getQuery(Paths.get(sqlDeletePath, rb.getString(tab)).toString()))) {
                    pstmtDelete.execute();
                }
            }
            conn.commit();

            for (String fileName : files) {
                System.out.println(fileName);
                Path path = Paths.get(rb.getString("source_directory")).resolve(fileName);
                ExcelInfo excelInfo = parseFile(path);

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
//                                    System.out.println(codeInfo.codeWordList.size());
                                    for (int ind = 0; ind < codeInfo.codeWordList.size(); ++ind) {
//                                        System.out.println(codeInfo.codeWordList.get(ind));
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

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

/*

                for(TupleCode tupleCode : tupleCodes) {
                    Stream<String> stringStream = Arrays.stream(tupleCode.stmtName.toLowerCase().split("[\\p{Punct}\\p{Blank}]")).filter(p -> !p.equalsIgnoreCase(""));
                    List<String> strs = stringStream.collect(Collectors.toList());
                    String stmtFineName = strs.stream().collect(Collectors.joining(" "));
                    for (int ind=0; ind < strs.size(); ++ind) {
                        pstmtInsert.setString(1, fileNM);
                        pstmtInsert.setInt(2, Integer.valueOf(fileNM.replaceAll("\\.xlsx", "")));
                        pstmtInsert.setInt(3, tupleCode.index);
                        pstmtInsert.setString(4, tupleCode.stmtName);
                        pstmtInsert.setString(5, stmtFineName);
                        pstmtInsert.setInt(6, ind+1);
                        pstmtInsert.setString(7, strs.get(ind));

                        pstmtInsert.addBatch();
                    }
                }

                pstmtInsert.executeBatch();
                conn.commit();

                try (PreparedStatement pstmtFile = conn.prepareStatement(getQuery(rb.getString("file")));
                     PreparedStatement pstmtFineCode = conn.prepareStatement(getQuery(rb.getString("fine_code")));
                     PreparedStatement pstmtWord = conn.prepareStatement(getQuery(rb.getString("word")));
                     PreparedStatement pstmtWordInCode = conn.prepareStatement(getQuery(rb.getString("word_in_code")));
                     PreparedStatement pstmtCode = conn.prepareStatement(getQuery(rb.getString("code")));
                     PreparedStatement pstmtCodeInFile = conn.prepareStatement(getQuery(rb.getString("code_in_file")))) {
                    pstmtFile.execute();
                    pstmtFineCode.execute();
                    pstmtWord.execute();
                    pstmtWordInCode.execute();
                    pstmtCode.execute();
                    pstmtCodeInFile.execute();
                }
                conn.commit();
            }

*/

/*            System.out.println(tupleCodes.get(0).stmtName);
            System.out.println(tupleCodes.get(0).stmtName.replaceAll("\\p{Punct}", " "));

            System.out.println(tupleCodes.get(17).stmtName);
            System.out.println(tupleCodes.get(17).stmtName.replaceAll("\\p{Punct}", " "));
            String str = tupleCodes.get(17).stmtName.replaceAll("\\p{Punct}", " ")
                                                    .replaceAll("\\p{Blank}+", " ")
                                                    .replaceFirst("\\p{Blank}", "").toLowerCase();*/

//            String sqlString = "INSERT INTO tmp_raw_data(currency_name, factor, report_year, idx, stmt_name, stmt_value, file_year) VALUES(:currency_name, :factor, :report_year, :idx, :stmt_name, :stmt_value, :file_year)";
//            try (Connection conn = DriverManager.getConnection(rb.getString("url"));
//                 PreparedStatement pstmtTrunc = conn.prepareStatement(sqlTruncString);
//                 PreparedStatement pstmt = conn.prepareStatement(sqlString)) {
//
//                conn.setAutoCommit(false);
//                pstmtTrunc.execute();
//
//                conn.commit();
//
//                for (Tuple tuple : tuples) {
//                    pstmt.setString(1, tuple.currencyName);
//                    pstmt.setInt(2, tuple.factor);
//                    pstmt.setInt(3, tuple.reportYear);
//                    pstmt.setInt(4, tuple.index);
//                    pstmt.setString(5, tuple.stmtName);
//                    pstmt.setInt(6, tuple.stmtValue);
//                    pstmt.setInt(7, tuple.fileYear);
//                    pstmt.addBatch();
//                }
//                pstmt.executeBatch();
//                pstmt.clearBatch();
//                conn.commit();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }



//            String strm = stringStream.collect(Collectors.joining(" "));
//            List<String> strs = Arrays.stream(tupleCodes.get(17).stmtName.toLowerCase().split("[\\p{Punct}\\p{Blank}]"))
//                .filter(p -> !p.equalsIgnoreCase("")).collect(Collectors.toList());

//            strs.forEach(System.out::println);
//            System.out.println(strm);

//            System.out.println(tupleCodes.get(17).stmtName.replaceAll("\\p{Punct}", " ")
//                .replaceAll("\\p{Blank}+", " ")
//            .replaceFirst("\\p{Blank}", "").toLowerCase());
//            int fileYear = Integer.valueOf(fileNM.replaceAll("\\.xlsx", ""));


//            String sqlTruncString = "DELETE FROM tmp_raw_data";
//            String sqlString = "INSERT INTO tmp_raw_data(currency_name, factor, report_year, idx, stmt_name, stmt_value, file_year) VALUES(:currency_name, :factor, :report_year, :idx, :stmt_name, :stmt_value, :file_year)";
//            try (Connection conn = DriverManager.getConnection(rb.getString("url"));
//                 PreparedStatement pstmtTrunc = conn.prepareStatement(sqlTruncString);
//                 PreparedStatement pstmt = conn.prepareStatement(sqlString)) {
//
//                conn.setAutoCommit(false);
//                pstmtTrunc.execute();
//
//                conn.commit();
//
//                for (Tuple tuple : tuples) {
//                    pstmt.setString(1, tuple.currencyName);
//                    pstmt.setInt(2, tuple.factor);
//                    pstmt.setInt(3, tuple.reportYear);
//                    pstmt.setInt(4, tuple.index);
//                    pstmt.setString(5, tuple.stmtName);
//                    pstmt.setInt(6, tuple.stmtValue);
//                    pstmt.setInt(7, tuple.fileYear);
//                    pstmt.addBatch();
//                }
//                pstmt.executeBatch();
//                pstmt.clearBatch();
//                conn.commit();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
/*        } catch (SQLException e) {
            e.printStackTrace();
        }*/
//        tuples.forEach(Tuple::print);
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
