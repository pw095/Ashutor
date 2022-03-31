package org.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.source.ReadSource;

import java.time.*;
import java.util.Arrays;
import java.util.Date;

public interface ReadExcel extends ReadSource {

    default String getStringCellValue(Cell cell) {

        CellType cellType = cell.getCellType();
        String stringCellValue;

        if (cellType == CellType.STRING) {
            stringCellValue = cell.getStringCellValue();
        } else if (cellType == CellType.BLANK) {
            stringCellValue = "";
        } else {
            throw new RuntimeException("Invalid cell type! Cell type is " + cellType + ", column = " + cell.getColumnIndex() + ", row = " + cell.getRowIndex());
        }

        return stringCellValue;

    }

    default LocalDate getDateCellValue(Cell cell) {

        CellType cellType = cell.getCellType();
        Date date;

        if (cellType == CellType.NUMERIC) {
            date = cell.getDateCellValue();
        } else {
            throw new RuntimeException("Invalid cell type! Cell type is " + cellType + ", column = " + cell.getColumnIndex() + ", row = " + cell.getRowIndex());
        }

        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    default long getLongCellValue(Cell cell) {

        CellType cellType = cell.getCellType();
        long longCellValue;

        if (cellType == CellType.NUMERIC) {
            longCellValue = (long) cell.getNumericCellValue();
        } else if (cellType == CellType.BLANK) {
            longCellValue = 0L;
        } else if (cellType == CellType.STRING) {
            longCellValue = 0L;
        } else {
            throw new RuntimeException("Invalid cell type! Cell type is " + cellType + ", column = " + cell.getColumnIndex() + ", row = " + cell.getRowIndex() + " value = " + cell.getStringCellValue());
        }

        return longCellValue;

    }

    default double getDoubleCellValue(Cell cell) {

        CellType cellType = cell.getCellType();
        double doubleCellValue;

        if (cellType == CellType.NUMERIC) {
            doubleCellValue = cell.getNumericCellValue();
        } else {
            throw new RuntimeException("Invalid cell type! Cell type is " + cellType);
        }

        return doubleCellValue;

    }

    default void isCertainStringCellValue(Cell cell, String ... stringValues) {
        String stringCellValue = getStringCellValue(cell);
        if (!Arrays.asList(stringValues).contains(stringCellValue)) {
            throw new RuntimeException("Invalid column header! Column header is " + stringCellValue);
        }
    }
}
