package org.example.report;

import java.time.LocalDate;

public class SingleDimensionReportInfoOld extends ReportInfo {
    public int reportItemIndex;
    public LocalDate reportDate;

    public SingleDimensionReportInfoOld() {}

    public SingleDimensionReportInfoOld(SingleDimensionReportInfoOld singleDimensionReportInfoOld) {
        super(singleDimensionReportInfoOld);
        this.reportItemIndex = singleDimensionReportInfoOld.reportItemIndex;
        this.reportDate = singleDimensionReportInfoOld.reportDate;
    }

    public SingleDimensionReportInfoOld
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
