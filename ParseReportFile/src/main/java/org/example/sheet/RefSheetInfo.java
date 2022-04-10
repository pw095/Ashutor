package org.example.sheet;

import java.time.LocalDate;

public class RefSheetInfo implements SheetInfo {

    private String reportPeriod;
    private LocalDate fileDate;
    private LocalDate publishDate;
    private String auditor;
    private String field;
    private long factor;
    private String currency;

    public String getReportPeriod() {
        return reportPeriod;
    }
    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public LocalDate getFileDate() {
        return fileDate;
    }
    public void setFileDate(LocalDate fileDate) {
        this.fileDate = fileDate;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }
    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuditor() {
        return auditor;
    }
    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }

    public long getFactor() {
        return factor;
    }
    public void setFactor(String factor) {
        switch (factor.toLowerCase()) {
            case "тыс":
                setFactor(1_000L);
                break;
            case "млн":
                setFactor(1_000_000L);
                break;
            default:
                setFactor(1L);
                break;
        }
    }
    public void setFactor(long factor) {
        this.factor = factor;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        if (currency.toLowerCase().contains("руб")) {
            this.currency = "RUB";
        } else if (currency.toLowerCase().contains("долл")) {
            this.currency = "USD";

        }
        this.currency = currency;
    }

    public RefSheetInfo() {}

    public RefSheetInfo
        (
            String reportPeriod,
            LocalDate fileDate,
            LocalDate publishDate,
            String auditor,
            String factor,
            String currency,
            String field
        ) {

        setField(field);
        setReportPeriod(reportPeriod);
        setFileDate(fileDate);
        setPublishDate(publishDate);
        setAuditor(auditor);
        setFactor(factor);
        setCurrency(currency);

    }

    public RefSheetInfo(RefSheetInfo refSheetInfo) {

        setReportPeriod(refSheetInfo.getReportPeriod());
        setFileDate(refSheetInfo.getFileDate());
        setPublishDate(refSheetInfo.getPublishDate());
        setAuditor(refSheetInfo.getAuditor());
        setFactor(refSheetInfo.getFactor());
        setCurrency(refSheetInfo.getCurrency());
        setField(refSheetInfo.getField());

    }
}
