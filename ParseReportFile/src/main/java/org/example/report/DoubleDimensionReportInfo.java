package org.example.report;

import java.time.LocalDate;

public class DoubleDimensionReportInfo extends ReportInfo {
    public int reportHorizontalItemIndex;
    public int reportVerticalItemIndex;
    public LocalDate reportDate;

    public DoubleDimensionReportInfo() {}

    public DoubleDimensionReportInfo(DoubleDimensionReportInfo doubleDimensionReportInfo) {
        super(doubleDimensionReportInfo);
        this.reportHorizontalItemIndex = doubleDimensionReportInfo.reportHorizontalItemIndex;
        this.reportVerticalItemIndex = doubleDimensionReportInfo.reportVerticalItemIndex;
        this.reportDate = doubleDimensionReportInfo.reportDate;
    }

    public DoubleDimensionReportInfo
        (
            int reportHorizontalItemIndex,
            int reportVerticalItemIndex,
            LocalDate reportDate,
            int reportValue
        )
    {
        super(reportValue);
        this.reportHorizontalItemIndex = reportHorizontalItemIndex;
        this.reportVerticalItemIndex = reportVerticalItemIndex;
        this.reportDate = reportDate;
    }
}
