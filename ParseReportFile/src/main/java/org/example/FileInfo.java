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
//        System.out.println(fileInfo.fileName);

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
//                    fileInfo.reportDateList = new ArrayList<>();

                    for (Row row : workSheet) { //  Первая строка содержит информацию о множителе (млн, тыс), валюте и отчётных годах
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
                            for (Cell cell : row) {
                                int columnIndex = cell.getColumnIndex();
                                if (columnIndex == 0) { // В первом столбце всегда статьи
                                    rawBalanceSheetInfo.itemList.add(cell.getStringCellValue());
                                } else { // Во всех прочих показатели отчётности
                                    if (cell.getCellType() == CellType.NUMERIC) {
                                        reportLine.add((int) cell.getNumericCellValue());
                                    } else {
                                        reportLine.add(0);
                                    }
                                }
                            }
                            rawBalanceSheetInfo.reportInfo.add(reportLine);
                        }
                    }
                    System.out.println("## " + fileInfo.balanceSheetInfoList.size());
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
            rawBalanceSheetInfo.reportInfo.add(ii, Arrays.asList(0, 0));
        } else if (pureList.contains("non current liabilities")) {
            int ii = pureList.indexOf("non current liabilities");
            rawBalanceSheetInfo.itemList.add(ii, "Liabilities");
            rawBalanceSheetInfo.reportInfo.add(ii, Arrays.asList(0, 0));
        }

        richBalanceSheetInfo.reportDateList = new ArrayList<>(rawBalanceSheetInfo.reportDateList);
        richBalanceSheetInfo.itemInfoList = new ArrayList<>();
//        richBalanceSheetInfo.reportDateList.addAll(rawBalanceSheetInfo.reportDateList);

        for (int ind = 0; ind < rawBalanceSheetInfo.reportInfo.size(); ++ind) {

            ItemInfo itemInfo = new ItemInfo();
            itemInfo.itemIndex = ind;
            itemInfo.itemName = rawBalanceSheetInfo.itemList.get(ind);
            itemInfo.itemPureName = itemInfo.itemName.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim();
            if (rawBalanceSheetInfo.reportInfo.get(ind).stream().allMatch(p -> p == 0)) {
                itemInfo.itemHeaderFlag = true;
            }
            if (itemInfo.itemPureName.startsWith("итого") || itemInfo.itemPureName.startsWith("total")) {
                itemInfo.itemSubtotalFlag = true;
            }
            richBalanceSheetInfo.itemInfoList.add(itemInfo);

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
                System.out.println(itemInfo.itemPureName);
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
        return richFileInfo;
    }
}
