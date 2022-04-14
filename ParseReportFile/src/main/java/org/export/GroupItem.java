package org.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GroupItem {

    private String reportDate;
    private List<Integer> idList;

    public String getReportDate() {
        return reportDate;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    public GroupItem(String str) {
        String[] arr = str.split(": ");
        setReportDate(arr[0]);
        setIdList(Arrays.stream(arr[1].split(";")).map(Integer::parseInt).collect(Collectors.toList()));
    }

    public GroupItem(String reportDate, int id) {
        setReportDate(reportDate);
        setIdList(new ArrayList<Integer>(id));
    }

    public GroupItem(String reportDate, List<Integer> idList) {
        setReportDate(reportDate);
        setIdList(idList);
    }

    public GroupItem(String reportDate, String idListString) {
        setReportDate(reportDate);
        setIdList(Arrays.stream(idListString.split(";")).map(Integer::parseInt).collect(Collectors.toList()));
    }
}
