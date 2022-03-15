package org.example.sheet;

import org.example.item.CFItemInfo;
import org.example.report.ReportInfo;

import java.util.ArrayList;
import java.util.List;

public class CFSheetInfo extends SheetInfo {

    // Обработанная информация с листа
    public List<CFItemInfo> cfItemInfoList;
    public List<ReportInfo> reportInfoList;

    public CFSheetInfo() {}

    public CFSheetInfo(SheetInfo sheetInfo) {
        super(sheetInfo);
    }
    public CFSheetInfo(CFSheetInfo cfSheetInfo) {
        super(cfSheetInfo);
        if (cfSheetInfo != null) {
            if (cfSheetInfo.cfItemInfoList != null && !cfSheetInfo.cfItemInfoList.isEmpty()) {
                cfItemInfoList = new ArrayList<>();
                for (CFItemInfo cfItemInfo : cfSheetInfo.cfItemInfoList) {
                    cfItemInfoList.add(new CFItemInfo(cfItemInfo));
                }
            }
            if (cfSheetInfo.reportInfoList != null & !cfSheetInfo.reportInfoList.isEmpty()) {
                reportInfoList = new ArrayList<>();
                for (ReportInfo reportInfo : cfSheetInfo.reportInfoList) {
                    reportInfoList.add(new ReportInfo(reportInfo));
                }
            }
        }
    }
}
