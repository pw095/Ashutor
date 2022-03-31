package org.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.entity.AbstractReference;
import org.entity.Emitter;
import org.entity.ReportPeriod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class ReadExcelReference extends AbstractReference implements ReadExcel {


    @Override
    protected void setFieldSet(Object object) {

        if (object instanceof Sheet) {

            Sheet sheet = (Sheet) object;
            Set<String> fieldSet = new HashSet<>();

            for (Row row : sheet) {
                for (Cell cell : row) {
                    int columnIndex = cell.getColumnIndex();
                    if (columnIndex == 0) {
                        if (cell.getRowIndex() == 0) {
                            isCertainStringCellValue(cell, "field_name");
                        } else {
                            fieldSet.add(getStringCellValue(cell));
                        }
                    } else {
                        throw new RuntimeException("Invalid column index! Column index is " + columnIndex);
                    }
                }
            }

            setFieldSet(fieldSet);

        } else {
            throw new RuntimeException("Invalid object type!");
        }

    }

    @Override
    protected void setEmitterSet(Object object) {

        if (object instanceof Sheet) {

            Sheet sheet = (Sheet) object;
            Set<Emitter> emitterSet = new HashSet<>();

            labelRow:
            for (Row row : sheet) {

                Emitter emitter = new Emitter();

                for (Cell cell : row) {
                    int rowIndex = cell.getRowIndex();
                    int columnIndex = cell.getColumnIndex();
                    if (rowIndex == 0) {
                        if (columnIndex == 0) {
                            isCertainStringCellValue(cell, "emitter_name");
                        } else if (columnIndex == 1) {
                            isCertainStringCellValue(cell, "field_name");
                            continue labelRow;
                        } else {
                            throw new RuntimeException("Invalid column index! Column index is " + columnIndex);
                        }
                    } else {
                        if (columnIndex == 0) {
                            emitter.setEmitterName(getStringCellValue(cell));
                        } else if (columnIndex == 1) {
                            emitter.setEmitterFieldName(getStringCellValue(cell));
                        } else {
                            throw new RuntimeException("Invalid column index! Column index is " + columnIndex);
                        }
                    }
                }

                emitterSet.add(emitter);

            }

            setEmitterSet(emitterSet);

        } else {
            throw new RuntimeException("Invalid object type!");
        }

    }

    @Override
    protected void setAuditorSet(Object object) {

        if (object instanceof Sheet) {

            Sheet sheet = (Sheet) object;
            Set<String> auditorSet = new HashSet<>();

            for (Row row : sheet) {
                for (Cell cell : row) {
                    int columnIndex = cell.getColumnIndex();
                    if (columnIndex == 0) {
                        if (cell.getRowIndex() == 0) {
                            isCertainStringCellValue(cell, "auditor_name");
                        } else {
                            auditorSet.add(getStringCellValue(cell));
                        }
                    } else {
                        throw new RuntimeException("Invalid column index! Column index is " + columnIndex);
                    }
                }
            }

            setAuditorSet(auditorSet);

        } else {
            throw new RuntimeException("Invalid object type!");
        }

    }

    @Override
    protected void setReportPeriodSet(Object object) {

        if (object instanceof Sheet) {

            Sheet sheet = (Sheet) object;
            Set<ReportPeriod> reportPeriodSet = new HashSet<>();

            labelRow:
            for (Row row : sheet) {

                ReportPeriod reportPeriod = new ReportPeriod();

                for (Cell cell : row) {
                    int rowIndex = cell.getRowIndex();
                    int columnIndex = cell.getColumnIndex();
                    if (rowIndex == 0) {
                        if (columnIndex == 0) {
                            isCertainStringCellValue(cell,"report_period_code");
                        } else if (columnIndex == 1) {
                            isCertainStringCellValue(cell,"report_period_name");
                            continue labelRow;
                        } else {
                            throw new RuntimeException("Invalid column index! Column index is " + columnIndex);
                        }
                    } else {
                        if (columnIndex == 0) {
                            reportPeriod.setReportPeriodCode(getStringCellValue(cell));
                        } else if (columnIndex == 1) {
                            reportPeriod.setReportPeriodName(getStringCellValue(cell));
                        } else {
                            throw new RuntimeException("Invalid column index! Column index is " + columnIndex);
                        }
                    }
                }

                reportPeriodSet.add(reportPeriod);

            }

            setReportPeriodSet(reportPeriodSet);

        } else {
            throw new RuntimeException("Invalid object type!");
        }

    }

    @Override
    public void readSource(String sourcePath) {
        try (FileInputStream inputStream = new FileInputStream(new File(sourcePath))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            for (Sheet workSheet : workbook) {
                String workSheetName = workSheet.getSheetName();
                switch (workSheetName) {
                    case "field":
                        this.setFieldSet(workSheet);
                        break;
                    case "emitter":
                        this.setEmitterSet(workSheet);
                        break;
                    case "auditor":
                        this.setAuditorSet(workSheet);
                        break;
                    case "report_period":
                        this.setReportPeriodSet(workSheet);
                        break;
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ReadExcelReference(String sourcePath) {
        readSource(sourcePath);
    }
}
