package org.example.sheet;

import org.example.item.PLItemInfo;
import org.example.report.ReportInfo;

import java.util.ArrayList;
import java.util.List;

public class PLSheetInfoClass extends SheetInfoClass {

    // Обработанная информация с листа
    public List<PLItemInfo> plItemInfoList;
    public List<ReportInfo> reportInfoList;

    public PLSheetInfoClass() {}

    public PLSheetInfoClass(SheetInfoClass sheetInfo) {
        super(sheetInfo);
    }
    public PLSheetInfoClass(PLSheetInfoClass plSheetInfo) {
        super(plSheetInfo);
        if (plSheetInfo != null) {
            if (plSheetInfo.plItemInfoList != null && !plSheetInfo.plItemInfoList.isEmpty()) {
                plItemInfoList = new ArrayList<>();
                for (PLItemInfo plItemInfo : plSheetInfo.plItemInfoList) {
                    plItemInfoList.add(new PLItemInfo(plItemInfo));
                }
            }
            if (plSheetInfo.reportInfoList != null & !plSheetInfo.reportInfoList.isEmpty()) {
                reportInfoList = new ArrayList<>();
                for (ReportInfo reportInfo : plSheetInfo.reportInfoList) {
                    reportInfoList.add(new ReportInfo(reportInfo));
                }
            }
        }
    }
}
