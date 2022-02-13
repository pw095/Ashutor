package org.example;

import java.time.LocalDate;

public class ReportInfo {
    public int reportItemIndex;
    public LocalDate reportDate;
    public int reportValue;

    public ReportInfo
        (
            int reportItemIndex,
            LocalDate reportDate,
            int reportValue
        )
    {
        this.reportItemIndex = reportItemIndex;
        this.reportDate = reportDate;
        this.reportValue = reportValue;
    }
}
