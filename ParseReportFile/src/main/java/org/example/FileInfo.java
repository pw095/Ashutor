package org.example;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileInfo {
    public String emitterName;
    public String fileName;
    public LocalDate fileDate;
    public String fileCurrency;
    public int fileFactor;

    // Элемент списка соответствует одной странице
    public List<SheetInfo> balanceSheetInfoList;
//    public List<RawBalanceSheetInfo> rawBalanceSheetInfoList;
//    public List<RichBalanceSheetInfo> richBalanceSheetInfoList;

    public FileInfo() {}

    public FileInfo(Path path) {
        parseFile(this, path);
    }

    private static void parseFile(FileInfo fileInfo, Path path) {

//        FileInfo fileInfo = new FileInfo();

        fileInfo.fileName = path.getFileName().toString();
        fileInfo.emitterName = path.getParent().getFileName().toString();

        System.out.println(fileInfo.emitterName);
        System.out.println(fileInfo.fileName);

        fileInfo.fileDate = LocalDate.of(Integer.parseInt(fileInfo.fileName.replaceAll("\\.xlsx", "")), Month.DECEMBER, 31);

        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            fileInfo.balanceSheetInfoList = new ArrayList<>();
//            fileInfo.rawBalanceSheetInfoList = new ArrayList<>();
//            fileInfo.richBalanceSheetInfoList = new ArrayList<>();
            for (Sheet workSheet : workbook) {

                if (workSheet.getSheetName().equals("BALANCE")) {

                    RawBalanceSheetInfo rawBalanceSheetInfo = new RawBalanceSheetInfo();
//                    fileInfo.balanceSheetInfoList.add(new RawBalanceSheetInfo());
//                    fileInfo.rawBalanceSheetInfoList.add(new RawBalanceSheetInfo());
//                    RawBalanceSheetInfo rawBalanceSheetInfo = (RawBalanceSheetInfo) fileInfo.balanceSheetInfoList.get(fileInfo.balanceSheetInfoList.size() - 1);
//                    RawBalanceSheetInfo rawBalanceSheetInfo = fileInfo.rawBalanceSheetInfoList.get(fileInfo.rawBalanceSheetInfoList.size()-1);

//                    fileInfo.richBalanceSheetInfoList.add(new RichBalanceSheetInfo());
//                    RichBalanceSheetInfo richBalanceSheetInfo = fileInfo.richBalanceSheetInfoList.get(fileInfo.richBalanceSheetInfoList.size()-1);
//                    richBalanceSheetInfo.itemInfoList = new ArrayList<>();
//                    richBalanceSheetInfo.reportInfoList = new ArrayList<>();
//                    richBalanceSheetInfo.reportDateList = new ArrayList<>();

                    rawBalanceSheetInfo.itemList = new ArrayList<>();
                    rawBalanceSheetInfo.reportDateList = new ArrayList<>();
                    rawBalanceSheetInfo.reportInfo = new ArrayList<>();

                    int maxColumnIndex = 0;

                    labelRow:
                    for (Row row : workSheet) { //  Первая строка содержит информацию о множителе (млн, тыс), валюте и отчётных годах

                        for (Cell cell : row) {
                            maxColumnIndex = maxColumnIndex > cell.getColumnIndex() ? maxColumnIndex : cell.getColumnIndex();
                        }
                        if (row.getRowNum() == 0) {
                            for (Cell cell : row) { // В первом столбце всегда множитель и валюта
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
                                } else { // Во всех прочих информация об отчётных годах
                                    if (cell.getCellType() == CellType.NUMERIC) {
                                        rawBalanceSheetInfo.reportDateList.add(LocalDate.of((int) cell.getNumericCellValue(), Month.DECEMBER, 31));
//                                        fileInfo.reportDateList.add();
                                    } else {
                                        throw new RuntimeException("Invalid cell type: " + cell.getCellType().toString());
                                    }
                                }
                            }
                        } else { // Статьи и показатели отчётности
                            List<Integer> reportLine = new ArrayList<>();

//                            for (Cell cell : row) {
                            for (int columnIndex = 0; columnIndex <= maxColumnIndex; ++columnIndex) {
//                                int columnIndex = cell.getColumnIndex();

                                Cell cell = row.getCell(columnIndex);
/*
                                if (cell == null) {
                                    System.out.println("columnIndex = " + columnIndex + ", rowNum = " + row.getRowNum());
                                }
*/
                                if (columnIndex == 0) { // В первом столбце всегда статьи
//                                    String cellValue = cell.getStringCellValue();
//                                    Отбрасываем всю строку, если первый столбец не заполнен. Он может быть не заполнен, если
//                                    1. Вся строка не заполнена
//                                    2. Данные в строке выполняю роль промежуточного подытога. Такие подытоги не обрабатываются и в БД не загружаются.
                                    if (cell != null) {
                                        if (cell.getCellType() == CellType.STRING) {

                                            String cellValue = cell.getStringCellValue();

                                            if ( !(cellValue == null || cell.getStringCellValue().isEmpty()) ) {
                                                rawBalanceSheetInfo.itemList.add(cellValue);
                                            } else {
                                                continue labelRow;
                                            }

                                        } else {
                                            continue labelRow;
                                        }
                                    } else {
                                        continue labelRow;
                                    }
                                } else { // Во всех прочих показатели отчётности
                                    if (cell != null ) {
                                        if (cell.getCellType() == CellType.NUMERIC) {
//                                            System.out.print("max_column_index = " + maxColumnIndex + " column_index = " + (columnIndex-1) + " " + (int) cell.getNumericCellValue());
                                            reportLine.add(columnIndex-1, (int) cell.getNumericCellValue());
                                        } else {
//                                            System.out.print("max_column_index = " + maxColumnIndex + " column_index = " + (columnIndex-1) + " 0000000");
                                            reportLine.add(columnIndex-1, 0);
                                        }
                                    } else {
                                        reportLine.add(columnIndex-1, 0);
                                    }
                                }
//                                System.out.println();
                            }
                            if (!reportLine.isEmpty()) {
                                rawBalanceSheetInfo.reportInfo.add(reportLine);
                            }
                        }
                    }
//                    System.out.println("## " + fileInfo.balanceSheetInfoList.size());
                    fileInfo.balanceSheetInfoList.add(rawBalanceSheetInfo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("parse size " + fileInfo.rawBalanceSheetInfoList.size());
//        return fileInfo;
    }

    public FileInfo(FileInfo fileInfo) {

        this.fileName = fileInfo.fileName;
        this.emitterName = fileInfo.emitterName;
        this.fileDate = fileInfo.fileDate;
        this.fileCurrency = fileInfo.fileCurrency;
        this.fileFactor = fileInfo.fileFactor;

        this.balanceSheetInfoList = new ArrayList<>(fileInfo.balanceSheetInfoList);
//        this.reportDateList = new ArrayList<>(fileInfo.reportDateList);

    }

    public static FileInfo getRich(FileInfo rawFileInfo) {

        FileInfo richFileInfo = new FileInfo(rawFileInfo);

//        rawFileInfo.balanceSheetInfoList.
//        richFileInfo.balanceSheetInfoList = new ArrayList<>();
//        FileInfo richFileInfo = new FileInfo();
//        richFileInfo.fileName = rawFileInfo.fileName;

        richFileInfo.balanceSheetInfoList = new ArrayList<>();
        RawBalanceSheetInfo rawBalanceSheetInfo = (RawBalanceSheetInfo) rawFileInfo.balanceSheetInfoList.get(0);
//        System.out.println("Size1 =" + rawBalanceSheetInfo.itemList.size());
//        System.out.println("Size1 =" + rawBalanceSheetInfo.reportInfo.size());
        for (int k = rawBalanceSheetInfo.itemList.size() - 1; k >= 0; --k) {
            if (rawBalanceSheetInfo.itemList.get(k).isEmpty()) {
                rawBalanceSheetInfo.reportInfo.remove(k);
            }
        }
        rawBalanceSheetInfo.itemList.removeIf(String::isEmpty);

        RichBalanceSheetInfo richBalanceSheetInfo = new RichBalanceSheetInfo(); //fileInfo.richBalanceSheetInfoList.get(0);

        List<String> pureList = rawBalanceSheetInfo.itemList.stream().map(p -> p.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim()).collect(Collectors.toList());
        if (pureList.contains("обязательства")) {
        } else if (pureList.contains("liabilities")) {
        } else if (pureList.contains("долгосрочные обязательства")) {
            int ii = pureList.indexOf("долгосрочные обязательства");
            rawBalanceSheetInfo.itemList.add(ii, "Обязательства");
            List<Integer> list = new ArrayList<>();
            for (int ind=0; ind < rawBalanceSheetInfo.reportInfo.get(ii).size(); ++ind) {
                list.add(0);
            }
            rawBalanceSheetInfo.reportInfo.add(ii, list);
        } else if (pureList.contains("non current liabilities")) {
            int ii = pureList.indexOf("non current liabilities");
            rawBalanceSheetInfo.itemList.add(ii, "Liabilities");
            List<Integer> list = new ArrayList<>();
            for (int ind=0; ind < rawBalanceSheetInfo.reportInfo.get(ii).size(); ++ind) {
                list.add(0);
            }
            rawBalanceSheetInfo.reportInfo.add(ii, list);
        }

        richBalanceSheetInfo.reportDateList = new ArrayList<>(rawBalanceSheetInfo.reportDateList);
        richBalanceSheetInfo.itemInfoList = new ArrayList<>();

        for (int ind = 0; ind < rawBalanceSheetInfo.reportInfo.size(); ++ind) {

            ItemInfo itemInfo = new ItemInfo();
            itemInfo.itemIndex = ind;
//            System.out.println("ind = " + ind + " " + rawBalanceSheetInfo.itemList.get(ind));
            try {
                itemInfo.itemName = rawBalanceSheetInfo.itemList.get(ind);
            } catch (RuntimeException e) {
                e.printStackTrace();
                System.out.println("ind = " + ind + ", rawBalanceSheetInfo = " + rawBalanceSheetInfo.itemList.get(1));
            }
            itemInfo.itemPureName = itemInfo.itemName.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim();
            if (rawBalanceSheetInfo.reportInfo.get(ind).stream().allMatch(p -> p == 0)) {
                itemInfo.itemHeaderFlag = true;
            }
            if (itemInfo.itemPureName.startsWith("итого") || itemInfo.itemPureName.startsWith("total")) {
                itemInfo.itemSubtotalFlag = true;
            }
            richBalanceSheetInfo.itemInfoList.add(itemInfo);

        }

        richBalanceSheetInfo.reportInfoList = new ArrayList<>();

//        System.out.println(richBalanceSheetInfo.itemInfoList.size());
        for (int ind=0; ind < richBalanceSheetInfo.itemInfoList.size(); ++ind) {
            for (int jnd=0; jnd < richBalanceSheetInfo.reportDateList.size(); ++jnd) {
//                rawBalanceSheetInfo.reportInfo.get(ind).get(jnd);
//                System.out.println("ind = " + ind + ", jnd = " + jnd + " ii = " + richBalanceSheetInfo.itemInfoList.get(ind).itemName + " " + rawBalanceSheetInfo.reportInfo.get(ind).get(jnd));
//                System.out.println(richBalanceSheetInfo.reportDateList.get(jnd));
                richBalanceSheetInfo.reportInfoList.add(new ReportInfo(ind, richBalanceSheetInfo.reportDateList.get(jnd), rawBalanceSheetInfo.reportInfo.get(ind).get(jnd)));
            }
        }

        for (ItemInfo itemInfo : richBalanceSheetInfo.itemInfoList) {
            if (itemInfo.itemSubtotalFlag) {
                itemInfo.parentItemIndex =
                    richBalanceSheetInfo.itemInfoList.stream()
                        .filter(p -> p.itemHeaderFlag)
                        .filter(p -> p.itemIndex < itemInfo.itemIndex)
                        .filter(p -> itemInfo.itemPureName.equals("итого " + p.itemPureName) || itemInfo.itemPureName.equals("total " + p.itemPureName))
                        .mapToInt(p -> p.itemIndex).findFirst().orElse(-2);
            }
        }

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        for (ItemInfo itemInfo : richBalanceSheetInfo.itemInfoList) {

            // Отбираем, те у которых Subtotal flag = 1
            if (itemInfo.parentItemIndex == -2) {
//                System.out.println(itemInfo.itemPureName);
                itemInfo.parentItemIndex =
                    richBalanceSheetInfo.itemInfoList.stream()
                        // Шагаем по элементам, которые являются заголовками
                        .filter(p -> p.itemHeaderFlag)
                        // Отбираем те заголовки, у которых порядковый номер меньше нашей записи
                        .filter(p -> p.itemIndex < itemInfo.itemIndex)
                        // Отбираем те заголовки, которые никому более присвоены не были
                        .filter(p -> richBalanceSheetInfo.itemInfoList.stream().mapToInt(t -> t.parentItemIndex).noneMatch(u -> u == p.itemIndex))
                        // Для каждого такого заголовка рассчитываем его путь до нашего элемента
                        .map(p -> new PossibleParent(p.itemIndex, levenshteinDistance.apply(p.itemPureName, itemInfo.itemPureName)))
                        .sorted()
                        .mapToInt(p -> p.parentIndex)
                        .findFirst()
                        .orElse(-3);
            }
        }

        int kk = 0;
        int indStart = 0;
        for (int ii = 0; ii < richBalanceSheetInfo.itemInfoList.size(); ++ii) {
            if (richBalanceSheetInfo.itemInfoList.get(ii).itemHeaderFlag || richBalanceSheetInfo.itemInfoList.get(ii).itemSubtotalFlag) {
                if (richBalanceSheetInfo.itemInfoList.get(ii).itemHeaderFlag) {
                    richBalanceSheetInfo.itemInfoList.get(ii).parentItemIndex = richBalanceSheetInfo.itemInfoList
                        .subList(indStart, ii).stream()
                        .filter(p -> p.itemHeaderFlag || p.itemSubtotalFlag)
                        .sorted()
                        .skip(kk)
                        .map(p -> p.itemIndex).findFirst().orElse(-2);
                }
                if (richBalanceSheetInfo.itemInfoList.get(ii).itemSubtotalFlag) {
                    kk += 2;
                } else if (kk > 0 && richBalanceSheetInfo.itemInfoList.get(ii - 1).itemSubtotalFlag && richBalanceSheetInfo.itemInfoList.get(ii).itemHeaderFlag) {
                    kk -= 2;
                }
                if (richBalanceSheetInfo.itemInfoList.get(ii).parentItemIndex == 0 && richBalanceSheetInfo.itemInfoList.get(ii).itemSubtotalFlag) {
                    kk = 0;
                    indStart = ii + 1;
                }
            }
        }

        int headerIndex = 0;
        for (int ii = 0; ii < richBalanceSheetInfo.itemInfoList.size(); ++ii) {
            if (richBalanceSheetInfo.itemInfoList.get(ii).itemHeaderFlag || richBalanceSheetInfo.itemInfoList.get(ii).itemSubtotalFlag) {
                headerIndex = ii;
            }
            if (richBalanceSheetInfo.itemInfoList.get(ii).parentItemIndex == -1) {
                richBalanceSheetInfo.itemInfoList.get(ii).parentItemIndex = richBalanceSheetInfo.itemInfoList.get(headerIndex).itemIndex;
            }
        }

        richFileInfo.balanceSheetInfoList.add(richBalanceSheetInfo);

        int ind = -1;
        int lag_ind = 0;
        for (ItemInfo itemInfo : richBalanceSheetInfo.itemInfoList) {

            itemInfo.itemLevel = lag_ind + (itemInfo.itemSubtotalFlag ? -1 : 0);
            lag_ind += (itemInfo.itemSubtotalFlag ? -1 : 0) + (itemInfo.itemHeaderFlag ? 1 : 0);

        }
//        System.out.println("richFileInfo = " + ((RichBalanceSheetInfo) richFileInfo.balanceSheetInfoList.get(0)).itemInfoList.size());
        return richFileInfo;
    }
}
