package org.example.report;

public class ReportInfo {

    public long reportValue;

    public long getReportValue() {
        return reportValue;
    }

    public void setReportValue(long reportValue) {
        this.reportValue = reportValue;
    }
    public ReportInfo() {}

    public ReportInfo(ReportInfo reportInfo) {
        setReportValue(reportInfo.getReportValue());
    }
    public ReportInfo(long reportValue) {
        setReportValue(reportValue);
    }
}
