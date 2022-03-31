package org.example.sheet;

import java.time.LocalDate;
import java.util.*;

public class BalanceRawSheetInfo implements SheetInfo {
    // Необработанная информация с листа

    // Необработанный список статей
    private List<LocalDate> reportDateList;
    private List<String> itemList;

    // Необработанные данные
    private Map<Integer, Map<Integer, Long>> reportInfo;

    public List<LocalDate> getReportDateList() {
        return new ArrayList<>(reportDateList);
    }
    public void setReportDateList(List<LocalDate> reportDateList) {
        this.reportDateList = new ArrayList<>(reportDateList);
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

    public BalanceRawSheetInfo() {}
    public BalanceRawSheetInfo(BalanceRawSheetInfo sheetInfo) {
        setItemList(sheetInfo.getItemList());
        setReportDateList(sheetInfo.getReportDateList());
        setReportInfo(sheetInfo.getReportInfo());
    }
}
