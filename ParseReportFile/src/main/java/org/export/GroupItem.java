package org.export;

public class GroupItem {

    private String reportDate;
    private int id;

    public String getReportDate() {
        return reportDate;
    }

    public int getId() {
        return id;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GroupItem(String str) {
        String[] arr = str.split(": ");
        setReportDate(arr[0]);
        setId(Integer.parseInt(arr[1]));
    }

    public GroupItem(String reportDate, int id) {
        setReportDate(reportDate);
        setId(id);
    }

}
