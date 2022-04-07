package org.example.report;

import java.time.LocalDate;

public class DoubleDimensionReportInfoOld extends ReportInfo {
    public int reportHorizontalItemIndex;
    public int reportVerticalItemIndex;
    public LocalDate reportDate;

    public DoubleDimensionReportInfoOld() {}

    public DoubleDimensionReportInfoOld(DoubleDimensionReportInfoOld doubleDimensionReportInfoOld) {
        super(doubleDimensionReportInfoOld);
        this.reportHorizontalItemIndex = doubleDimensionReportInfoOld.reportHorizontalItemIndex;
        this.reportVerticalItemIndex = doubleDimensionReportInfoOld.reportVerticalItemIndex;
        this.reportDate = doubleDimensionReportInfoOld.reportDate;
    }

    public DoubleDimensionReportInfoOld
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
