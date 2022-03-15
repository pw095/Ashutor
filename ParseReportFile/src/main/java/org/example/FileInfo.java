package org.example;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.item.BalanceItemInfo;
import org.example.item.CFItemInfo;
import org.example.item.PLItemInfo;
import org.example.report.SingleDimensionReportInfo;
import org.example.sheet.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileInfo {
    public String emitterName;
    public String fileName;
    public LocalDate fileDate;
    public String fileCurrency;
    public int fileFactor;

    // Элемент списка соответствует одной странице
    public Map<String, SheetInfo> sheetInfoMap;

    public FileInfo() {}

    public FileInfo(Path path) {
        parseFile(path);
    }


    private void getExtraFileInfo(Sheet workSheet) {

        // Первая ячейка содержит информацию о множителе (млн, тыс) и валюте
        Cell firstCell = workSheet.getRow(0).getCell(0);
        String stringValue;

        if (firstCell.getCellType() == CellType.STRING) {
            stringValue = firstCell.getStringCellValue();
        } else {
            throw new RuntimeException("Invalid cell type: " + firstCell.getCellType().toString());
        }

        if (stringValue.contains("млн")) {
            this.fileFactor = 6;
        } else if (stringValue.contains("тыс")) {
            this.fileFactor = 3;
        }

        if (stringValue.contains("руб")) {
            this.fileCurrency = "RUB";
        } else if (stringValue.contains("долл")) {
            this.fileCurrency = "USD";
        }

    }

    private static SingleDimensionSheetInfo parseSingleDimensionSheetInfo(Sheet workSheet) {
        SingleDimensionSheetInfo sheetInfo = new SingleDimensionSheetInfo();

        sheetInfo.itemList = new ArrayList<>();
        sheetInfo.reportDateList = new ArrayList<>();
        sheetInfo.reportInfo = new ArrayList<>();

        int maxColumnIndex = 0;

        labelRow:
        for (Row row : workSheet) { //  Первая строка содержит информацию о множителе (млн, тыс), валюте и отчётных годах

            for (Cell cell : row) {
                maxColumnIndex = Math.max(maxColumnIndex, cell.getColumnIndex());
            }

            if (row.getRowNum() == 0) {

                for (Cell cell : row) {
                    if (cell.getColumnIndex() > 0) { // Во всех столбцах начиная со второго информация об отчётных годах
                        if (cell.getCellType() == CellType.NUMERIC) {
                            sheetInfo.reportDateList.add(LocalDate.of((int) cell.getNumericCellValue(), Month.DECEMBER, 31));
                        } else {
                            throw new RuntimeException("Invalid cell type: " + cell.getCellType().toString());
                        }
                    }
                }

            } else { // Статьи и показатели отчётности

                List<Integer> reportLine = new ArrayList<>();

                for (int columnIndex=0; columnIndex <= maxColumnIndex; ++columnIndex) {

                    Cell cell = row.getCell(columnIndex);

                    if (columnIndex == 0) { // В первом столбце всегда статьи
//                                    Отбрасываем всю строку, если первый столбец не заполнен. Он может быть не заполнен, если
//                                    1. Вся строка не заполнена
//                                    2. Данные в строке выполняют роль промежуточного подытога. Такие подытоги не обрабатываются и в БД не загружаются.
                        if (cell != null) {
                            if (cell.getCellType() == CellType.STRING) {

                                String cellValue = cell.getStringCellValue();

                                if ( !(cellValue == null || cell.getStringCellValue().isEmpty()) ) {
                                    sheetInfo.itemList.add(cellValue);
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
                }
                if (!reportLine.isEmpty()) {
                    sheetInfo.reportInfo.add(reportLine);
                }
            }
        }

        return sheetInfo;

    }

    private void parseFile(Path path) {

        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {

                switch (workSheet.getSheetName()) {
                    case "BALANCE":
                        // Данные о файле
                        getExtraFileInfo(workSheet);

                        fileName = path.getFileName().toString();
                        emitterName = path.getParent().getFileName().toString();
                        fileDate = LocalDate.of(Integer.parseInt(fileName.replaceAll("\\.xlsx", "")), Month.DECEMBER, 31);
                        sheetInfoMap = new HashMap<>();

                        sheetInfoMap.put("BALANCE", parseSingleDimensionSheetInfo(workSheet));

                        break;
                    case "PL":
                        sheetInfoMap.put("PL", parseSingleDimensionSheetInfo(workSheet));
                        break;
                    case "CASH_FLOW":
                        sheetInfoMap.put("CF", parseSingleDimensionSheetInfo(workSheet));
                        break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public FileInfo(FileInfo fileInfo) {

        this.fileName = fileInfo.fileName;
        this.emitterName = fileInfo.emitterName;
        this.fileDate = fileInfo.fileDate;
        this.fileCurrency = fileInfo.fileCurrency;
        this.fileFactor = fileInfo.fileFactor;

    }

    private static BalanceSheetInfo getBalanceSheetInfo(SingleDimensionSheetInfo singleDimensionSheetInfo) {

        SingleDimensionSheetInfo rawBalanceSheetInfo = new SingleDimensionSheetInfo(singleDimensionSheetInfo);
        BalanceSheetInfo richBalanceSheetInfo = new BalanceSheetInfo(singleDimensionSheetInfo);

        for (int k = rawBalanceSheetInfo.itemList.size() - 1; k >= 0; --k) {
            if (rawBalanceSheetInfo.itemList.get(k).isEmpty()) {
                rawBalanceSheetInfo.reportInfo.remove(k);
            }
        }
        rawBalanceSheetInfo.itemList.removeIf(String::isEmpty);

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

        richBalanceSheetInfo.balanceItemInfoList = new ArrayList<>();

        for (int ind = 0; ind < rawBalanceSheetInfo.reportInfo.size(); ++ind) {

            BalanceItemInfo balanceItemInfo = new BalanceItemInfo();
            balanceItemInfo.itemIndex = ind;
            try {
                balanceItemInfo.itemName = rawBalanceSheetInfo.itemList.get(ind);
            } catch (RuntimeException e) {
                e.printStackTrace();
                System.out.println("ind = " + ind + ", rawBalanceSheetInfo = " + rawBalanceSheetInfo.itemList.get(1));
            }
            balanceItemInfo.itemPureName = balanceItemInfo.itemName.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim();
            if (rawBalanceSheetInfo.reportInfo.get(ind).stream().allMatch(p -> p == 0)) {
                balanceItemInfo.itemHeaderFlag = true;
            }
            if (balanceItemInfo.itemPureName.startsWith("итого") || balanceItemInfo.itemPureName.startsWith("total")) {
                balanceItemInfo.itemSubtotalFlag = true;
            }
            richBalanceSheetInfo.balanceItemInfoList.add(balanceItemInfo);

        }

        richBalanceSheetInfo.reportInfoList = new ArrayList<>();

        for (int ind = 0; ind < richBalanceSheetInfo.balanceItemInfoList.size(); ++ind) {
            for (int jnd = 0; jnd < richBalanceSheetInfo.reportDateList.size(); ++jnd) {
                richBalanceSheetInfo.reportInfoList.add(new SingleDimensionReportInfo(ind, richBalanceSheetInfo.reportDateList.get(jnd), rawBalanceSheetInfo.reportInfo.get(ind).get(jnd)));
            }
        }

        for (BalanceItemInfo balanceItemInfo : richBalanceSheetInfo.balanceItemInfoList) {
            if (balanceItemInfo.itemSubtotalFlag) {
                balanceItemInfo.parentItemIndex =
                    richBalanceSheetInfo.balanceItemInfoList.stream()
                        .filter(p -> p.itemHeaderFlag)
                        .filter(p -> p.itemIndex < balanceItemInfo.itemIndex)
                        .filter(p -> balanceItemInfo.itemPureName.equals("итого " + p.itemPureName) || balanceItemInfo.itemPureName.equals("total " + p.itemPureName))
                        .mapToInt(p -> p.itemIndex).findFirst().orElse(-2);
            }
        }

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        for (BalanceItemInfo balanceItemInfo : richBalanceSheetInfo.balanceItemInfoList) {

            // Отбираем, те у которых Subtotal flag = 1
            if (balanceItemInfo.parentItemIndex == -2) {
//                System.out.println(itemInfo.itemPureName);
                balanceItemInfo.parentItemIndex =
                    richBalanceSheetInfo.balanceItemInfoList.stream()
                        // Шагаем по элементам, которые являются заголовками
                        .filter(p -> p.itemHeaderFlag)
                        // Отбираем те заголовки, у которых порядковый номер меньше нашей записи
                        .filter(p -> p.itemIndex < balanceItemInfo.itemIndex)
                        // Отбираем те заголовки, которые никому более присвоены не были
                        .filter(p -> richBalanceSheetInfo.balanceItemInfoList.stream().mapToInt(t -> t.parentItemIndex).noneMatch(u -> u == p.itemIndex))
                        // Для каждого такого заголовка рассчитываем его путь до нашего элемента
                        .map(p -> new PossibleParent(p.itemIndex, levenshteinDistance.apply(p.itemPureName, balanceItemInfo.itemPureName)))
                        .sorted()
                        .mapToInt(p -> p.parentIndex)
                        .findFirst()
                        .orElse(-3);
            }
        }

        int kk = 0;
        int indStart = 0;
        for (int ii = 0; ii < richBalanceSheetInfo.balanceItemInfoList.size(); ++ii) {
            if (richBalanceSheetInfo.balanceItemInfoList.get(ii).itemHeaderFlag || richBalanceSheetInfo.balanceItemInfoList.get(ii).itemSubtotalFlag) {
                if (richBalanceSheetInfo.balanceItemInfoList.get(ii).itemHeaderFlag) {
                    richBalanceSheetInfo.balanceItemInfoList.get(ii).parentItemIndex = richBalanceSheetInfo.balanceItemInfoList
                        .subList(indStart, ii).stream()
                        .filter(p -> p.itemHeaderFlag || p.itemSubtotalFlag)
                        .sorted()
                        .skip(kk)
                        .map(p -> p.itemIndex).findFirst().orElse(-2);
                }
                if (richBalanceSheetInfo.balanceItemInfoList.get(ii).itemSubtotalFlag) {
                    kk += 2;
                } else if (kk > 0 && richBalanceSheetInfo.balanceItemInfoList.get(ii - 1).itemSubtotalFlag && richBalanceSheetInfo.balanceItemInfoList.get(ii).itemHeaderFlag) {
                    kk -= 2;
                }
                if (richBalanceSheetInfo.balanceItemInfoList.get(ii).parentItemIndex == 0 && richBalanceSheetInfo.balanceItemInfoList.get(ii).itemSubtotalFlag) {
                    kk = 0;
                    indStart = ii + 1;
                }
            }
        }

        int headerIndex = 0;
        for (int ii = 0; ii < richBalanceSheetInfo.balanceItemInfoList.size(); ++ii) {
            if (richBalanceSheetInfo.balanceItemInfoList.get(ii).itemHeaderFlag || richBalanceSheetInfo.balanceItemInfoList.get(ii).itemSubtotalFlag) {
                headerIndex = ii;
            }
            if (richBalanceSheetInfo.balanceItemInfoList.get(ii).parentItemIndex == -1) {
                richBalanceSheetInfo.balanceItemInfoList.get(ii).parentItemIndex = richBalanceSheetInfo.balanceItemInfoList.get(headerIndex).itemIndex;
            }
        }

        int lag_ind = 0;
        for (BalanceItemInfo balanceItemInfo : richBalanceSheetInfo.balanceItemInfoList) {

            balanceItemInfo.itemLevel = lag_ind + (balanceItemInfo.itemSubtotalFlag ? -1 : 0);
            lag_ind += (balanceItemInfo.itemSubtotalFlag ? -1 : 0) + (balanceItemInfo.itemHeaderFlag ? 1 : 0);

        }

        return richBalanceSheetInfo;

    }

    private static PLSheetInfo getPLSheetInfo(SingleDimensionSheetInfo singleDimensionSheetInfo) {

        SingleDimensionSheetInfo rawPLSheetInfo = new SingleDimensionSheetInfo(singleDimensionSheetInfo);
        PLSheetInfo richPLSheetInfo = new PLSheetInfo(rawPLSheetInfo);

        richPLSheetInfo.plItemInfoList = new ArrayList<>();

        for (int ind=0; ind < rawPLSheetInfo.reportInfo.size(); ++ind) {
            PLItemInfo plItemInfo = new PLItemInfo();
            plItemInfo.itemIndex = ind;
            plItemInfo.itemName = singleDimensionSheetInfo.itemList.get(ind);
            plItemInfo.itemPureName = plItemInfo.itemName.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim();
            richPLSheetInfo.plItemInfoList.add(plItemInfo);
        }
        System.out.println(richPLSheetInfo.plItemInfoList.size());
        richPLSheetInfo.reportInfoList = new ArrayList<>();

        for (int ind = 0; ind < richPLSheetInfo.plItemInfoList.size(); ++ind) {
            for (int jnd=0; jnd < richPLSheetInfo.reportDateList.size(); ++jnd) {
                richPLSheetInfo.reportInfoList.add(new SingleDimensionReportInfo(ind, richPLSheetInfo.reportDateList.get(jnd), rawPLSheetInfo.reportInfo.get(ind).get(jnd)));
            }
        }

        return richPLSheetInfo;

    }

    private static CFSheetInfo getCFSheetInfo(SingleDimensionSheetInfo singleDimensionSheetInfo) {

        SingleDimensionSheetInfo rawCFSheetInfo = new SingleDimensionSheetInfo(singleDimensionSheetInfo);
        CFSheetInfo richCFSheetInfo = new CFSheetInfo(rawCFSheetInfo);

        richCFSheetInfo.cfItemInfoList = new ArrayList<>();

        for (int ind=0; ind < rawCFSheetInfo.reportInfo.size(); ++ind) {
            CFItemInfo cfItemInfo = new CFItemInfo();
            cfItemInfo.itemIndex = ind;
            cfItemInfo.itemName = singleDimensionSheetInfo.itemList.get(ind);
            cfItemInfo.itemPureName = cfItemInfo.itemName.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim();
            richCFSheetInfo.cfItemInfoList.add(cfItemInfo);
        }
        System.out.println(richCFSheetInfo.cfItemInfoList.size());
        richCFSheetInfo.reportInfoList = new ArrayList<>();

        for (int ind = 0; ind < richCFSheetInfo.cfItemInfoList.size(); ++ind) {
            for (int jnd=0; jnd < richCFSheetInfo.reportDateList.size(); ++jnd) {
                richCFSheetInfo.reportInfoList.add(new SingleDimensionReportInfo(ind, richCFSheetInfo.reportDateList.get(jnd), rawCFSheetInfo.reportInfo.get(ind).get(jnd)));
            }
        }

        return richCFSheetInfo;

    }

    public void getRich() {

        sheetInfoMap.put("RICH_BALANCE", getBalanceSheetInfo((SingleDimensionSheetInfo) sheetInfoMap.get("BALANCE")));
        sheetInfoMap.put("RICH_PL", getPLSheetInfo((SingleDimensionSheetInfo) sheetInfoMap.get("PL")));
        sheetInfoMap.put("RICH_CF", getCFSheetInfo((SingleDimensionSheetInfo) sheetInfoMap.get("CF")));

    }
}
