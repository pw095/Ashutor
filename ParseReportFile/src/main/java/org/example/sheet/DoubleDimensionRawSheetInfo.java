package org.example.sheet;

import org.omg.CORBA.INTERNAL;

import java.time.LocalDate;
import java.util.*;

public class DoubleDimensionRawSheetInfo implements SheetInfo {
    // Необработанная информация с листа

    // Необработанный список статей
    private List<LocalDate> reportBeginDateList;
    private List<LocalDate> reportEndDateList;
    private List<String> horizontalItemList;
    private List<String> verticalItemList;

    // Необработанные данные
    private Map<Integer, Map<Integer, Map<Integer, Long>>> reportInfo;

    public List<LocalDate> getReportBeginDateList() {
        return new ArrayList<>(reportBeginDateList);
    }
    public void setReportBeginDateList(List<LocalDate> reportBeginDateList) {
        this.reportBeginDateList = new ArrayList<>(reportBeginDateList);
    }

    public List<LocalDate> getReportEndDateList() {
        return new ArrayList<>(reportEndDateList);
    }
    public void setReportEndDateList(List<LocalDate> reportEndDateList) {
        this.reportEndDateList = new ArrayList<>(reportEndDateList);
    }

    public List<String> getHorizontalItemList() {
        return new ArrayList<>(horizontalItemList);
    }
    public void setHorizontalItemList(List<String> itemList) {
        this.horizontalItemList = new ArrayList<>(itemList);
    }

    public List<String> getVerticalItemList() {
        return new ArrayList<>(verticalItemList);
    }
    public void setVerticalItemList(List<String> itemList) {
        this.verticalItemList = new ArrayList<>(itemList);
    }

    public Map<Integer, Map<Integer, Map<Integer, Long>>> getReportInfo() {
        Map<Integer, Map<Integer, Map<Integer, Long>>> report = new HashMap<>();
        for (Integer reportDateIndex : reportInfo.keySet()) {
            Map<Integer, Map<Integer, Long>> reportDate = new HashMap<>();
            Map<Integer, Map<Integer, Long>> reportSourceDate = reportInfo.get(reportDateIndex);
            for (Integer reportVerticaltemIndex : reportSourceDate.keySet()) {
                reportDate.put(reportVerticaltemIndex, new HashMap<>(reportSourceDate.get(reportVerticaltemIndex)));
            }
            report.put(reportDateIndex, reportDate);
        }
        return report;
    }
    public void setReportInfo(Map<Integer, Map<Integer, Map<Integer, Long>>> reportInfo) {
        if (reportInfo != null && !reportInfo.isEmpty()) {
            this.reportInfo = new HashMap<>();
            for (Integer reportDateIndex : reportInfo.keySet()) {
                Map<Integer, Map<Integer, Long>> reportDate = reportInfo.get(reportDateIndex);
                Map<Integer, Map<Integer, Long>> reportDestDate = new HashMap<>();
                for (Integer reportVerticalItemIndex : reportDate.keySet()) {
                    reportDestDate.put(reportVerticalItemIndex, new HashMap<>(reportDate.get(reportVerticalItemIndex)));
                }
                this.reportInfo.put(reportDateIndex, reportDestDate);
            }
        }
    }

    public DoubleDimensionRawSheetInfo() {}
    public DoubleDimensionRawSheetInfo(DoubleDimensionRawSheetInfo sheetInfo) {
        setVerticalItemList(sheetInfo.getVerticalItemList());
        setHorizontalItemList(sheetInfo.getHorizontalItemList());
        setReportBeginDateList(sheetInfo.getReportBeginDateList());
        setReportEndDateList(sheetInfo.getReportEndDateList());
        setReportInfo(sheetInfo.getReportInfo());
    }
}
