package org.example.report;

import java.time.LocalDate;

public class ReportInfo {

    public int reportValue;

    public ReportInfo() {}

    public ReportInfo(ReportInfo reportInfo) {
        this.reportValue = reportInfo.reportValue;
    }
    public ReportInfo(int reportValue) {
        this.reportValue = reportValue;
    }
}
