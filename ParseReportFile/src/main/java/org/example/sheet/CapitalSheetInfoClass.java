package org.example.sheet;

import org.example.item.CapitalItemInfo;
import org.example.report.ReportInfo;

import java.util.ArrayList;
import java.util.List;

public class CapitalSheetInfoClass extends SheetInfoClass {

    // Обработанная информация с листа
    public List<CapitalItemInfo> capitalItemInfoList;
    public List<ReportInfo> reportInfoList;

    public CapitalSheetInfoClass() {}

    public CapitalSheetInfoClass(SheetInfoClass sheetInfo) {
        super(sheetInfo);
    }
    public CapitalSheetInfoClass(CapitalSheetInfoClass capitalSheetInfo) {
        super(capitalSheetInfo);
        if (capitalSheetInfo != null) {
            if (capitalSheetInfo.capitalItemInfoList != null && !capitalSheetInfo.capitalItemInfoList.isEmpty()) {
                capitalItemInfoList = new ArrayList<>();
                for (CapitalItemInfo capitalItemInfo : capitalSheetInfo.capitalItemInfoList) {
                    capitalItemInfoList.add(new CapitalItemInfo(capitalItemInfo));
                }
            }
            if (capitalSheetInfo.reportInfoList != null & !capitalSheetInfo.reportInfoList.isEmpty()) {
                reportInfoList = new ArrayList<>();
                for (ReportInfo reportInfo : capitalSheetInfo.reportInfoList) {
                    reportInfoList.add(new ReportInfo(reportInfo));
                }
            }
        }
    }
}
