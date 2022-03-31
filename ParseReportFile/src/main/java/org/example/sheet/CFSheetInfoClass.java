package org.example.sheet;

import org.example.item.CFItemInfo;
import org.example.report.ReportInfo;

import java.util.ArrayList;
import java.util.List;

public class CFSheetInfoClass extends SheetInfoClass {

    // Обработанная информация с листа
    public List<CFItemInfo> cfItemInfoList;
    public List<ReportInfo> reportInfoList;

    public CFSheetInfoClass() {}

    public CFSheetInfoClass(SheetInfoClass sheetInfo) {
        super(sheetInfo);
    }
    public CFSheetInfoClass(CFSheetInfoClass cfSheetInfo) {
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
