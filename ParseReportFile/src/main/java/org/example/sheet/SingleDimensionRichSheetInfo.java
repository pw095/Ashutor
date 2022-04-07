package org.example.sheet;

import org.example.item.ItemInfo;
import org.example.item.SingleDimensionItemInfo;
import org.example.report.PeriodReportInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SingleDimensionRichSheetInfo implements SheetInfo {

    // Обработанная информация с листа
    private List<SingleDimensionItemInfo> itemInfoList;
    private Map<Integer, List<PeriodReportInfo>> reportInfoMap;

    public List<SingleDimensionItemInfo> getItemInfoList() {
        return new ArrayList<>(itemInfoList);
    }
    public void setItemInfoList(List<SingleDimensionItemInfo> itemInfoList) {
        this.itemInfoList = new ArrayList<>(itemInfoList);
    }

    public Map<Integer, List<PeriodReportInfo>> getReportInfoMap() {
        return new HashMap<>(reportInfoMap);
    }
    public void setReportInfoMap(Map<Integer, List<PeriodReportInfo>> reportInfoMap) {
        this.reportInfoMap = new HashMap<>(reportInfoMap);
    }

    private void getRich(SingleDimensionRawSheetInfo rawSheetInfo) {

        List<LocalDate> reportBeginDateList = rawSheetInfo.getReportBeginDateList();
        List<LocalDate> reportEndDateList = rawSheetInfo.getReportEndDateList();
        List<String> itemList = rawSheetInfo.getItemList();
        Map<Integer, Map<Integer, Long>> reportInfo = rawSheetInfo.getReportInfo();

        List<String> pureItemList = itemList.stream().map(p -> p.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim()).collect(Collectors.toList());

        List<SingleDimensionItemInfo> singleDimensionItemInfoList = new ArrayList<>();

        for (int ind=0; ind < itemList.size(); ++ind) {
            ItemInfo itemInfo = new ItemInfo(ind, itemList.get(ind), pureItemList.get(ind));
            SingleDimensionItemInfo singleDimensionItemInfo = new SingleDimensionItemInfo(itemInfo);
            singleDimensionItemInfoList.add(singleDimensionItemInfo);
        }


        Map<Integer, List<PeriodReportInfo>> reportInfoMap = new HashMap<>();

        for (int ind : reportInfo.keySet()) {
            Map<Integer, Long> reportString = reportInfo.get(ind);
            List<PeriodReportInfo> reportInfoString = new ArrayList<>();
            for (int jnd : reportString.keySet()) {
                reportInfoString.add(new PeriodReportInfo(reportBeginDateList.get(jnd), reportEndDateList.get(jnd), reportString.get(jnd)));
            }
            reportInfoMap.put(ind, reportInfoString);
        }

        setInfo(singleDimensionItemInfoList, reportInfoMap);
    }


    public SingleDimensionRichSheetInfo() {}

    private void setInfo(List<SingleDimensionItemInfo> itemInfoList, Map<Integer, List<PeriodReportInfo>> reportInfoMap) {
        if (itemInfoList != null && !itemInfoList.isEmpty()) {
            setItemInfoList(itemInfoList);
        }
        if (reportInfoMap != null && !reportInfoMap.isEmpty()) {
            setReportInfoMap(reportInfoMap);
        }
    }

    public SingleDimensionRichSheetInfo(List<SingleDimensionItemInfo> itemInfoList, Map<Integer, List<PeriodReportInfo>> reportInfoMap) {
        setInfo(itemInfoList, reportInfoMap);
    }

    public SingleDimensionRichSheetInfo(SheetInfo sheetInfo) {
        if (sheetInfo != null) {
            if (sheetInfo instanceof SingleDimensionRichSheetInfo) {

                SingleDimensionRichSheetInfo singleDimensionRichSheetInfo = (SingleDimensionRichSheetInfo) sheetInfo;
                setInfo(singleDimensionRichSheetInfo.getItemInfoList(), singleDimensionRichSheetInfo.getReportInfoMap());

            } else if (sheetInfo instanceof SingleDimensionRawSheetInfo) {

                SingleDimensionRawSheetInfo singleDimensionRawSheetInfo = (SingleDimensionRawSheetInfo) sheetInfo;
                getRich(new SingleDimensionRawSheetInfo(singleDimensionRawSheetInfo));

            }
        }
    }
}
