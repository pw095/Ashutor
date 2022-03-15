package org.example.sheet;

import org.example.report.ReportInfo;
import org.example.item.BalanceItemInfo;

import java.util.ArrayList;
import java.util.List;

public class BalanceSheetInfo extends SheetInfo {

    // Обработанная информация с листа
    public List<BalanceItemInfo> balanceItemInfoList;
    public List<ReportInfo> reportInfoList;

    public BalanceSheetInfo() {}

    public BalanceSheetInfo(SheetInfo sheetInfo) {
        super(sheetInfo);
    }
    public BalanceSheetInfo(BalanceSheetInfo balanceSheetInfo) {
        super(balanceSheetInfo);
        if (balanceSheetInfo != null) {
            if (balanceSheetInfo.balanceItemInfoList != null && !balanceSheetInfo.balanceItemInfoList.isEmpty()) {
                balanceItemInfoList = new ArrayList<>();
                for (BalanceItemInfo balanceItemInfo : balanceSheetInfo.balanceItemInfoList) {
                    balanceItemInfoList.add(new BalanceItemInfo(balanceItemInfo));
                }
            }
            if (balanceSheetInfo.reportInfoList != null & !balanceSheetInfo.reportInfoList.isEmpty()) {
                reportInfoList = new ArrayList<>();
                for (ReportInfo reportInfo : balanceSheetInfo.reportInfoList) {
                    reportInfoList.add(new ReportInfo(reportInfo));
                }
            }
        }
    }
}
