package org.example.sheet;

import java.time.LocalDate;
import java.util.*;

public class SingleDimensionRawSheetInfo implements SheetInfo {
    // Необработанная информация с листа

    // Необработанный список статей
    private List<LocalDate> reportBeginDateList;
    private List<LocalDate> reportEndDateList;
    private List<String> itemList;

    // Необработанные данные
    private Map<Integer, Map<Integer, Long>> reportInfo;

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

    public List<String> getItemList() {
        return new ArrayList<>(itemList);
    }
    public void setItemList(List<String> itemList) {
        this.itemList = new ArrayList<>(itemList);
    }

    public Map<Integer, Map<Integer, Long>> getReportInfo() {
        Map<Integer, Map<Integer, Long>> report = new HashMap<>();
        for (Integer reportInfoIndex : reportInfo.keySet()) {
            report.put(reportInfoIndex, new HashMap<>(reportInfo.get(reportInfoIndex)));
        }
        return report;
    }
    public void setReportInfo(Map<Integer, Map<Integer, Long>> reportInfo) {
        if (reportInfo != null && !reportInfo.isEmpty()) {
            this.reportInfo = new HashMap<>();
            for (Integer reportInfoIndex : reportInfo.keySet()) {
                this.reportInfo.put(reportInfoIndex, new HashMap<>(reportInfo.get(reportInfoIndex)));
            }
        }
    }

    public SingleDimensionRawSheetInfo() {}
    public SingleDimensionRawSheetInfo(SingleDimensionRawSheetInfo sheetInfo) {
        setItemList(sheetInfo.getItemList());
        setReportBeginDateList(sheetInfo.getReportBeginDateList());
        setReportEndDateList(sheetInfo.getReportEndDateList());
        setReportInfo(sheetInfo.getReportInfo());
    }
}
