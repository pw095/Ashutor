package org.example.sheet;

import org.example.item.PLItemInfo;
import org.example.report.ReportInfo;

import java.util.ArrayList;
import java.util.List;

public class PLSheetInfo extends SheetInfo {

    // Обработанная информация с листа
    public List<PLItemInfo> plItemInfoList;
    public List<ReportInfo> reportInfoList;

    public PLSheetInfo() {}

    public PLSheetInfo(SheetInfo sheetInfo) {
        super(sheetInfo);
    }
    public PLSheetInfo(PLSheetInfo plSheetInfo) {
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
