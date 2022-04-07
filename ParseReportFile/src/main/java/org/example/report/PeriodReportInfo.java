package org.example.report;

import java.time.LocalDate;

public class PeriodReportInfo extends ReportInfo {

    private LocalDate reportBeginDate;
    private LocalDate reportEndDate;

    public LocalDate getReportBeginDate() {
        return reportBeginDate;
    }
    public void setReportBeginDate(LocalDate reportBeginDate) {
        this.reportBeginDate = reportBeginDate;
    }

    public LocalDate getReportEndDate() {
        return reportEndDate;
    }
    public void setReportEndDate(LocalDate reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    public PeriodReportInfo() {}

    public PeriodReportInfo(PeriodReportInfo periodReportInfo) {
        super(periodReportInfo);
        setReportBeginDate(periodReportInfo.getReportBeginDate());
        setReportEndDate(periodReportInfo.getReportEndDate());
    }

    public PeriodReportInfo
        (
            LocalDate reportBeginDate,
            LocalDate reportEndDate,
            long reportValue
        )
    {
        super(reportValue);
        setReportBeginDate(reportBeginDate);
        setReportEndDate(reportEndDate);
    }
}
