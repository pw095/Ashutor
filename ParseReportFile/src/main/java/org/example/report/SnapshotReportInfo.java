package org.example.report;

import java.time.LocalDate;

public class SnapshotReportInfo extends ReportInfo {

    private LocalDate reportDate;

    public LocalDate getReportDate() {
        return reportDate;
    }
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public SnapshotReportInfo() {}

    public SnapshotReportInfo(SnapshotReportInfo snapshotReportInfo) {
        super(snapshotReportInfo);
        setReportDate(snapshotReportInfo.getReportDate());
    }

    public SnapshotReportInfo
        (
            LocalDate reportDate,
            long reportValue
        )
    {
        super(reportValue);
        setReportDate(reportDate);
    }
}
