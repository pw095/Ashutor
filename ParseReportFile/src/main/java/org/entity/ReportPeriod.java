package org.entity;

public class ReportPeriod {

    private String reportPeriodCode;
    private String reportPeriodName;

    public String getReportPeriodCode() {
        return reportPeriodCode;
    }
    public void setReportPeriodCode(String reportPeriodCode) {
        this.reportPeriodCode = reportPeriodCode;
    }

    public String getReportPeriodName() {
        return reportPeriodName;
    }
    public void setReportPeriodName(String reportPeriodName) {
        this.reportPeriodName = reportPeriodName;
    }

    public ReportPeriod() {}

    public ReportPeriod(String reportPeriodCode, String reportPeriodName) {
        setReportPeriodCode(reportPeriodCode);
        setReportPeriodName(reportPeriodName);
    }

    public ReportPeriod(ReportPeriod reportPeriod) {
        this(reportPeriod.getReportPeriodCode(), reportPeriod.getReportPeriodName());
    }
}
