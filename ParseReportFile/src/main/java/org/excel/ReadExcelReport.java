package org.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.entity.AbstractReport;
import org.example.sheet.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReadExcelReport extends AbstractReport implements ReadExcel {

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected void setRef(Object object) {

        if (object instanceof Sheet) {
            Sheet sheet = (Sheet) object;
            RefSheetInfo refSheetInfo = new RefSheetInfo();

            for (Row row : sheet) {
                String stringCellValue = "";
                for (Cell cell :row) {
                    int columnIndex = cell.getColumnIndex();
                    if (columnIndex == 0) {
                        stringCellValue = getStringCellValue(cell);
                    } else if (columnIndex == 1) {
                        switch (stringCellValue) {
                            case "report_period":
                                refSheetInfo.setReportPeriod(getStringCellValue(cell));
                                break;
                            case "file_date":
                                refSheetInfo.setFileDate(getDateCellValue(cell));
                                break;
                            case "publish_date":
                                refSheetInfo.setPublishDate(getDateCellValue(cell));
                                break;
                            case "auditor":
                                refSheetInfo.setAuditor(getStringCellValue(cell));
                                break;
                            case "factor":
                                refSheetInfo.setFactor(getStringCellValue(cell));
                                break;
                            case "currency":
                                refSheetInfo.setCurrency(getStringCellValue(cell));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            setRefSheetInfo(refSheetInfo);
        }
    }

    protected void setBalance(Object object) {

        if (object instanceof Sheet) {
            Sheet sheet = (Sheet) object;
            BalanceRawSheetInfo sheetInfo = new BalanceRawSheetInfo();
            List<LocalDate> reportDateList = new ArrayList<>();
            List<String> itemList = new ArrayList<>();
            Map<Integer, Map<Integer, Long>> reportInfo = new HashMap<>();
            int reportItemIndex = -1;

            labelRow:
            for (Row row : sheet) {
                Map<Integer, Long> reportLine = new HashMap<>();
                int rowIndex = 0;
                int columnIndex = 0;
                int reportDateIndex = -2;
                for (Cell cell : row) {
                    rowIndex = cell.getRowIndex();
                    columnIndex = cell.getColumnIndex();
                    ++reportDateIndex;
                    if (rowIndex == 0) {
                        if (columnIndex > 0) { // На первой строке во всех столбцах начиная со второго информация об отчётных годах
                            reportDateList.add(getDateCellValue(cell));
                        }
                    } else {
                        if (columnIndex == 0) { // В первом столбце начиная со второй строки идут статьи
                            String stringCellValue = getStringCellValue(cell);
                            if (stringCellValue.isEmpty()) {
                                continue labelRow;
                            }
                            itemList.add(stringCellValue);
                            ++reportItemIndex;
                        } else if (columnIndex > 0) { // Во всех прочих столбцах начиная со второй строки идут показатели отчётности
                            long longCellValue = getLongCellValue(cell);
                            if (longCellValue == 0L) {
                                continue;
                            }
                            reportLine.put(reportDateIndex, longCellValue);
                        }
                    }
                }
                if (!reportLine.isEmpty()) {
                    reportInfo.put(reportItemIndex, reportLine);
                }
            }
            sheetInfo.setReportInfo(reportInfo);
            sheetInfo.setItemList(itemList);
            sheetInfo.setReportDateList(reportDateList);
            setBalanceRawSheetInfo(sheetInfo);
        }

    }

    protected void setSingleDimension(Object object) {

        if (object instanceof Sheet) {
            Sheet sheet = (Sheet) object;
            SingleDimensionRawSheetInfo sheetInfo = new SingleDimensionRawSheetInfo();
            List<LocalDate> reportBeginDateList = new ArrayList<>();
            List<LocalDate> reportEndDateList = new ArrayList<>();
            List<String> itemList = new ArrayList<>();
            Map<Integer, Map<Integer, Long>> reportInfo = new HashMap<>();
            int reportItemIndex = -1;

            labelRow:
            for (Row row : sheet) {
                Map<Integer, Long> reportLine = new HashMap<>();
                int rowIndex = 0;
                int columnIndex = 0;
                int reportDateIndex = -2;
                for (Cell cell : row) {
                    rowIndex = cell.getRowIndex();
                    columnIndex = cell.getColumnIndex();
                    ++reportDateIndex;
                    if (rowIndex == 0) { // На первой строке во всех столбцах начиная со второго информация о начале отчётного периода
                        try {
                            reportBeginDateList.add(getDateCellValue(cell));
                        } catch (RuntimeException e) {
                            continue;
                        }
                    } else if (rowIndex == 1) { // На второй строке во всех столбцах начиная со второго информация об окончании отчётного периода
                        try {
                            reportEndDateList.add(getDateCellValue(cell));
                        } catch (RuntimeException e) {
                            continue;
                        }
                    } else {
                        if (columnIndex == 0) { // В первом столбце начиная с третьей строки идут статьи
                            String stringCellValue = getStringCellValue(cell);
                            if (stringCellValue.isEmpty()) {
                                continue labelRow;
                            }
                            itemList.add(stringCellValue);
                            ++reportItemIndex;
                        } else if (columnIndex > 0) { // Во всех прочих столбцах начиная со второй строки идут показатели отчётности
                            long longCellValue = getLongCellValue(cell);
                            if (longCellValue == 0) {
                                continue;
                            }
                            reportLine.put(reportDateIndex, longCellValue);
                        }
                    }
                }
                if (!reportLine.isEmpty()) {
                    reportInfo.put(reportItemIndex, reportLine);
                }
            }
            sheetInfo.setReportBeginDateList(reportBeginDateList);
            sheetInfo.setReportEndDateList(reportEndDateList);
            sheetInfo.setReportInfo(reportInfo);
            sheetInfo.setItemList(itemList);

            Map<String, SingleDimensionRawSheetInfo> sd = getSingleDimensionRawSheetInfoMap();
            sd.put(sheet.getSheetName(), sheetInfo);
            setSingleDimensionRawSheetInfoMap(sd);
        }

    }


    protected void setSingleDimensionList(Object object) {

        if (object instanceof Sheet) {
            Sheet sheet = (Sheet) object;
            SingleDimensionRawSheetInfo sheetInfo = new SingleDimensionRawSheetInfo();
            Map<String, SingleDimensionRawSheetInfo> sd = null;
            List<LocalDate> reportBeginDateList = new ArrayList<>();
            List<LocalDate> reportEndDateList = new ArrayList<>();
            List<String> itemList = new ArrayList<>();
            Map<Integer, Map<Integer, Long>> reportInfo = new HashMap<>();

            Map<String, String> reportMap = new HashMap<>();
            int reportItemIndex = -1;
            String reportCode = "";
            String reportName = "";

            labelRow:
            for (Row row : sheet) {
                Map<Integer, Long> reportLine = new HashMap<>();
                int rowIndex = 0;
                int columnIndex = 0;
                int reportDateIndex = -4;
                for (Cell cell : row) {
                    ++reportDateIndex;
                    rowIndex = cell.getRowIndex();
                    columnIndex = cell.getColumnIndex();
                    if (rowIndex == 0) { // На первой строке во всех столбцах начиная со второго информация о начале отчётного периода
                        try {
                            reportBeginDateList.add(getDateCellValue(cell));
                        } catch (RuntimeException e) {
                            continue;
                        }
                    } else if (rowIndex == 1) { // На второй строке во всех столбцах начиная со второго информация об окончании отчётного периода
                        try {
                            reportEndDateList.add(getDateCellValue(cell));
                        } catch (RuntimeException e) {
                            continue;
                        }
                    } else {
                        if (columnIndex == 0) { // В первом столбце начиная с третьей строки идёт код отчёта
                            String stringCellValue = getStringCellValue(cell);
                            if (stringCellValue.isEmpty() || (!reportCode.isEmpty() && !stringCellValue.equals(reportCode))) {

                                sheetInfo.setReportBeginDateList(reportBeginDateList);
                                sheetInfo.setReportEndDateList(reportEndDateList);
                                sheetInfo.setReportInfo(reportInfo);
                                sheetInfo.setItemList(itemList);

                                sd = getSingleDimensionRawSheetInfoMap();
                                sd.put(sheet.getSheetName() + "_" + reportCode, sheetInfo);
                                setSingleDimensionRawSheetInfoMap(sd);

                                sheetInfo = new SingleDimensionRawSheetInfo();
                                reportInfo = new HashMap<>();
                                itemList = new ArrayList<>();
                                reportCode = "";
                                reportName = "";
                                reportItemIndex = -1;

                            }
                            if (stringCellValue.isEmpty()) {
                                continue labelRow;
                            } else {
                                reportCode = stringCellValue;
                            }
                        } else if (columnIndex == 1) { // Во втором столбце наименование отчёта
                            reportName = getStringCellValue(cell);
                            if (reportName.isEmpty()) {
                                continue labelRow;
                            }
                        } else if (columnIndex == 2) { // В третьем столбце начиная с третьей строки идут статьи
                            String stringCellValue = getStringCellValue(cell);
                            if (stringCellValue.isEmpty()) {
                                continue labelRow;
                            }
                            itemList.add(stringCellValue);
                            ++reportItemIndex;
                        } else if (columnIndex > 0) { // Во всех прочих столбцах начиная со второй строки идут показатели отчётности
                            long longCellValue = getLongCellValue(cell);
                            if (longCellValue == 0) {
                                continue;
                            }
                            reportLine.put(reportDateIndex, longCellValue);
                        }
                    }
                }
                if (!reportLine.isEmpty()) {
                    reportInfo.put(reportItemIndex, reportLine);
                }
            }
            sheetInfo.setReportBeginDateList(reportBeginDateList);
            sheetInfo.setReportEndDateList(reportEndDateList);
            sheetInfo.setReportInfo(reportInfo);
            sheetInfo.setItemList(itemList);

            sd = getSingleDimensionRawSheetInfoMap();
            sd.put(sheet.getSheetName() + "_" + reportCode, sheetInfo);
            setSingleDimensionRawSheetInfoMap(sd);
        }

    }

    protected void setDoubleDimension(Object object) {

        if (object instanceof Sheet) {
            Sheet sheet = (Sheet) object;
            DoubleDimensionRawSheetInfo sheetInfo = new DoubleDimensionRawSheetInfo();
            List<LocalDate> reportBeginDateList = new ArrayList<>();
            List<LocalDate> reportEndDateList = new ArrayList<>();
            List<String> verticalItemList = new ArrayList<>();
            List<String> horizontalItemList = new ArrayList<>();
            Map<Integer, Map<Integer, Map<Integer, Long>>> reportInfo = new HashMap<>(); // <Дата, <Вертикальная статья, <Горизонтальная статья, Показатель>>>
            Map<Integer, Map<Integer, Long>> reportItemLine = new HashMap<>();
            String verticalItem = "";
            Row firstRow = null;

            labelRow:
            for (Row row : sheet) {
                Map<Integer, Long> reportLine = new HashMap<>(); // <Горизонтальая статья, Показатель>
                int rowIndex = 0;
                int columnIndex = 0;
                for (Cell cell : row) {
                    rowIndex = cell.getRowIndex();
                    columnIndex = cell.getColumnIndex();
                    if (rowIndex == 0) {
                        firstRow = row;
                        if (columnIndex >= 3) { // На первой строке во всех столбцах начиная с четвёртого информация о горизонтальных статьях
                            horizontalItemList.add(getStringCellValue(cell));
                        }
                    } else { // Начиная со второй строки
                        LocalDate dateCellValue;
                        switch (columnIndex) {
                            case 0: // В первом столбце информация о начале отчётного периода
                                try {
                                    dateCellValue = getDateCellValue(cell);
                                } catch (RuntimeException e) {
                                    reportInfo.put(reportBeginDateList.size() - 1, reportItemLine);
                                    reportItemLine = new HashMap<>();
                                    continue labelRow;
                                }
                                if (!reportBeginDateList.isEmpty() && !reportBeginDateList.contains(dateCellValue)) {
                                    reportInfo.put(reportBeginDateList.size() - 1, reportItemLine);
                                    reportItemLine = new HashMap<>();
                                }
                                if (!reportBeginDateList.contains(dateCellValue)) {
                                    reportBeginDateList.add(dateCellValue);
                                }
                                break;
                            case 1: // Во втором столбце информация об окончании отчётного периода
                                dateCellValue = getDateCellValue(cell);
                                if (!reportEndDateList.contains(dateCellValue)) {
                                    reportEndDateList.add(dateCellValue);
                                }
                                break;
                            case 2: // В третьем столбце вертикальные статьи
                                verticalItem = getStringCellValue(cell);
                                if (verticalItem.isEmpty()) {
                                    continue labelRow;
                                }
                                if (!verticalItemList.contains(verticalItem)) {
                                    verticalItemList.add(verticalItem);
                                }
                                break;
                            default: // Показатели отчётности
                                long longCellValue = getLongCellValue(cell);
                                if (longCellValue != 0) {
                                    String horizontalItemHeader = getStringCellValue(firstRow.getCell(columnIndex));
                                    reportLine.put(horizontalItemList.indexOf(horizontalItemHeader), longCellValue);
                                }
                                break;
                        }
                    }
                }
                if (!reportLine.isEmpty()) {
                    reportItemLine.put(verticalItemList.indexOf(verticalItem), reportLine);
                }
            }
            reportInfo.put(reportBeginDateList.size() - 1, reportItemLine);

            sheetInfo.setReportBeginDateList(reportBeginDateList);
            sheetInfo.setReportEndDateList(reportEndDateList);
            sheetInfo.setReportInfo(reportInfo);
            sheetInfo.setVerticalItemList(verticalItemList);
            sheetInfo.setHorizontalItemList(horizontalItemList);

            Map<String, DoubleDimensionRawSheetInfo> sd = getDoubleDimensionRawSheetInfoMap();
            sd.put(sheet.getSheetName(), sheetInfo);
            setDoubleDimensionRawSheetInfoMap(sd);
        }

    }



    protected void setDoubleDimensionList(Object object) {

        if (object instanceof Sheet) {
            Sheet sheet = (Sheet) object;
            Map<String, DoubleDimensionRawSheetInfo> sd = null;
            DoubleDimensionRawSheetInfo sheetInfo = new DoubleDimensionRawSheetInfo();
            Map<Integer, Map<Integer, Map<Integer, Long>>> reportInfo = new HashMap<>(); // <Отчётные период, <Вертикальная статья, <Горизонтальная статья, Показатель>>>
            Map<Integer, Map<Integer, Long>> reportItemLine = new HashMap<>(); // <Вертикальная статья, <Горизонтальная статья, Показатель>>
            List<LocalDate> reportBeginDateList = new ArrayList<>();
            List<LocalDate> reportEndDateList = new ArrayList<>();
            List<String> verticalItemList = new ArrayList<>();
            List<String> horizontalItemList = new ArrayList<>();
            String verticalItem = "";
            Row firstRow = null;
            String reportCode = "";
            String reportName = "";
            LocalDate reportBeginDateCellValue = null;
            LocalDate reportEndDateCellValue = null;
            LocalDate previousDate = null;
            labelRow:
            for (Row row : sheet) {
                Map<Integer, Long> reportLine = new HashMap<>(); // <Горизонтальая статья, Показатель>
                int rowIndex = 0;
                int columnIndex = 0;
                for (Cell cell : row) {

                    rowIndex = cell.getRowIndex();
                    columnIndex = cell.getColumnIndex();

                    if (rowIndex == 0) {
                        firstRow = row;
                        if (columnIndex >= 5) { // На первой строке во всех столбцах начиная с шестого информация о горизонтальных статьях
                            horizontalItemList.add(getStringCellValue(cell));
                        }
                    } else { // Начиная со второй строки
                        previousDate = reportBeginDateCellValue;
                        switch (columnIndex) {
                            case 0: // В первом столбце код отчёта
                                String stringCellValue = getStringCellValue(cell);
                                if (stringCellValue.isEmpty()) {
                                    continue labelRow;
                                } else {
                                    if (!reportCode.isEmpty() && !stringCellValue.equals(reportCode)) { // Новое значение отчёта

                                        // Сохраняем имеющиеся записи

                                        reportInfo.put(reportBeginDateList.indexOf(previousDate), reportItemLine);
                                        sheetInfo.setReportInfo(reportInfo);

                                        sheetInfo.setHorizontalItemList(horizontalItemList);
                                        sheetInfo.setVerticalItemList(verticalItemList);
                                        sheetInfo.setReportBeginDateList(reportBeginDateList);
                                        sheetInfo.setReportEndDateList(reportEndDateList);

                                        sd = getDoubleDimensionRawSheetInfoMap();
                                        sd.put(sheet.getSheetName() + "_" + reportCode, sheetInfo);
                                        setDoubleDimensionRawSheetInfoMap(sd);

                                        // Очищаем
                                        sheetInfo = new DoubleDimensionRawSheetInfo();
                                        reportInfo = new HashMap<>();
                                        reportItemLine = new HashMap<>();
                                        reportBeginDateList = new ArrayList<>();
                                        reportEndDateList = new ArrayList<>();

                                    }
                                }
                                reportCode = stringCellValue;
                                break;
                            case 1: // Во втором столбце наименование отчёта
                                reportName = getStringCellValue(cell);
                                if (reportName.isEmpty()) {
                                    continue labelRow;
                                }
                                break;
                            case 2: // В третьем столбце информация о начале отчётного периода
                                reportBeginDateCellValue = getDateCellValue(cell);
                                if (!reportBeginDateList.contains(reportBeginDateCellValue)) { // Новое значение начала отчётного периода
                                    if (!reportBeginDateList.isEmpty()) { // Если список непуст, то
                                        reportInfo.put(reportBeginDateList.indexOf(previousDate), reportItemLine); // Сохраняем имеющиеся записи
                                        reportItemLine = new HashMap<>();  // Очищаем reportItemLine
                                    }
                                    reportBeginDateList.add(reportBeginDateCellValue); // Добавляем записи в любом случае
                                }
                                break;
                            case 3: // В четвёртом столбце информация об окончании отчётного периода
                                reportEndDateCellValue = getDateCellValue(cell);
                                if (!reportEndDateList.contains(reportEndDateCellValue)) {
                                    reportEndDateList.add(reportEndDateCellValue);
                                }
                                break;
                            case 4: // В пятом столбце вертикальные статьи
                                verticalItem = getStringCellValue(cell);
                                if (verticalItem.isEmpty()) {
                                    continue labelRow;
                                }
                                if (!verticalItemList.contains(verticalItem)) {
                                    verticalItemList.add(verticalItem);
                                }
                                break;
                            default: // Показатели отчётности
                                long longCellValue = getLongCellValue(cell);
                                if (longCellValue != 0) {
                                    String horizontalItemHeader = getStringCellValue(firstRow.getCell(columnIndex));
                                    reportLine.put(horizontalItemList.indexOf(horizontalItemHeader), longCellValue);
                                }
                                break;
                        }
                    }
                }
                if (!reportLine.isEmpty()) {
                    reportItemLine.put(verticalItemList.indexOf(verticalItem), reportLine);
                }
            }

            reportInfo.put(reportBeginDateList.indexOf(previousDate), reportItemLine);
            sheetInfo.setReportInfo(reportInfo);

            sheetInfo.setHorizontalItemList(horizontalItemList);
            sheetInfo.setVerticalItemList(verticalItemList);
            sheetInfo.setReportBeginDateList(reportBeginDateList);
            sheetInfo.setReportEndDateList(reportEndDateList);

            sd = getDoubleDimensionRawSheetInfoMap();
            sd.put(sheet.getSheetName() + "_" + reportCode, sheetInfo);
            setDoubleDimensionRawSheetInfoMap(sd);
        }

    }



    @Override
    public void readSource(String sourcePath) {
        try (FileInputStream inputStream = new FileInputStream(new File(sourcePath))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {
                String workSheetName = workSheet.getSheetName();
                switch (workSheetName) {
                    case "REF":
                        setRef(workSheet);
                        break;
                    case "BALANCE":
                        setBalance(workSheet);
                        break;
                    case "PL":
                    case "CASH_FLOW":
                        setSingleDimension(workSheet);
                        break;
                    case "TAX":
                    case "LEASE":
                        setSingleDimensionList(workSheet);
                        break;
                    case "CAPITAL":
                    case "TAX_DEFERRED":
                        setDoubleDimension(workSheet);
                        break;
                    case "PROPERTY":
                        setDoubleDimensionList(workSheet);
                        break;
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ReadExcelReport(String sourcePath) {
        readSource(sourcePath);
    }
}
