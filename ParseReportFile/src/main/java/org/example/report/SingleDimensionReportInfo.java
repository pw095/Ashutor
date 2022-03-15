package org.example.report;

import org.example.sheet.SingleDimensionSheetInfo;

import java.time.LocalDate;

public class SingleDimensionReportInfo extends ReportInfo {
    public int reportItemIndex;
    public LocalDate reportDate;

    public SingleDimensionReportInfo() {}

    public SingleDimensionReportInfo(SingleDimensionReportInfo singleDimensionReportInfo) {
        super(singleDimensionReportInfo);
        this.reportItemIndex = singleDimensionReportInfo.reportItemIndex;
        this.reportDate = singleDimensionReportInfo.reportDate;
    }

    public SingleDimensionReportInfo
        (
            int reportItemIndex,
            LocalDate reportDate,
            int reportValue
        )
    {
        super(reportValue);
        this.reportItemIndex = reportItemIndex;
        this.reportDate = reportDate;
    }
}
