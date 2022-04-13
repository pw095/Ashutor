package org.example.sheet;

import org.example.item.DoubleDimensionItemInfo;
import org.example.item.ItemInfo;
import org.example.item.SingleDimensionItemInfo;
import org.example.report.PeriodReportInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DoubleDimensionRichSheetInfo implements SheetInfo {

    // Обработанная информация с листа
    private List<DoubleDimensionItemInfo> itemInfoList;
    private Map<Integer, Map<Integer, List<PeriodReportInfo>>> reportInfoMap;

    public List<DoubleDimensionItemInfo> getItemInfoList() {
        return new ArrayList<>(itemInfoList);
    }
    public void setItemInfoList(List<DoubleDimensionItemInfo> itemInfoList) {
        this.itemInfoList = new ArrayList<>(itemInfoList);
    }

    public Map<Integer, Map<Integer, List<PeriodReportInfo>>> getReportInfoMap() {
        return new HashMap<>(reportInfoMap);
    }
    public void setReportInfoMap(Map<Integer, Map<Integer, List<PeriodReportInfo>>> reportInfoMap) {
        this.reportInfoMap = new HashMap<>(reportInfoMap);
    }

    private void getRich(DoubleDimensionRawSheetInfo rawSheetInfo) {

        List<LocalDate> reportBeginDateList = rawSheetInfo.getReportBeginDateList();
        List<LocalDate> reportEndDateList = rawSheetInfo.getReportEndDateList();
        List<String> horizontalItemList = rawSheetInfo.getHorizontalItemList();
        List<String> verticalItemList = rawSheetInfo.getVerticalItemList();
        Map<Integer, Map<Integer, Map<Integer, Long>>> reportInfo = rawSheetInfo.getReportInfo();


        Map<Integer, Long> tmpMap1d = new HashMap<>();
        for (int ind : reportInfo.keySet()) {
            Map<Integer, Map<Integer, Long>> map2d = reportInfo.get(ind);
            if (ind==0) {
                tmpMap1d = map2d.getOrDefault(verticalItemList.indexOf("На конец периода"), new HashMap<>());
            } else {
                if (!map2d.containsKey(verticalItemList.indexOf("На начало периода")) && !tmpMap1d.isEmpty()) {
                    map2d.put(verticalItemList.indexOf("На начало периода"), tmpMap1d);
                }
            }
        }

        // Транспонируем reportInfo. Теперь дата наиболее вложенная размерность
        // <ind, <jnd, <knd, value>>>
        // <jnd, <knd, <ind, value>>>
        Map<Integer, Map<Integer, Map<Integer, Long>>> newMap3d = new HashMap<>();

        for (int ind : reportInfo.keySet()) { // Along date dimension

            Map<Integer, Map<Integer, Long>> map2d = reportInfo.get(ind);

            for (int jnd : map2d.keySet()) { // Along vertical dimension

                Map<Integer, Long> map1d = map2d.get(jnd);

                for (int knd : map1d.keySet()) { // Along horizontal dimension

                    Map<Integer, Map<Integer, Long>> newMap2d = newMap3d.getOrDefault(knd, new HashMap<>());
                    Map<Integer, Long> newMap1d = newMap2d.getOrDefault(jnd, new HashMap<>());
                    newMap1d.put(ind, map1d.get(knd));
                    newMap2d.put(jnd, newMap1d);
                    newMap3d.put(knd, newMap2d);
/*                    Map<Integer, Map<Integer, Long>> newMap2d = newMap3d.getOrDefault(knd, new HashMap<>());
                    Map<Integer, Long> newMap1d = newMap2d.getOrDefault(jnd, new HashMap<>());
                    newMap1d.put(ind, map1d.get(knd));
                    newMap2d.put(knd, newMap1d);
                    newMap3d.put(jnd, newMap2d);*/
                }
            }
        }

        Map<Integer, Map<Integer, List<PeriodReportInfo>>> reportInfoMap = new HashMap<>();

        for (int ind : newMap3d.keySet()) {

            Map<Integer, Map<Integer, Long>> newMap2d = newMap3d.get(ind);
            Map<Integer, List<PeriodReportInfo>> reportInfo1d = new HashMap<>();

            for (int jnd : newMap2d.keySet()) {

                Map<Integer, Long> newMap1d = newMap2d.get(jnd);
                List<PeriodReportInfo> reportInfoString = new ArrayList<>();

                for (int knd : newMap1d.keySet()) {
                    reportInfoString.add(new PeriodReportInfo(reportBeginDateList.get(knd), reportEndDateList.get(knd), newMap1d.get(knd)));
                }
                reportInfo1d.put(jnd, reportInfoString);
            }
            reportInfoMap.put(ind, reportInfo1d);
        }

        List<String> pureHorizontalItemList = horizontalItemList.stream().map(p -> p.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim()).collect(Collectors.toList());
        List<String> pureVerticalItemList = verticalItemList.stream().map(p -> p.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim()).collect(Collectors.toList());

        List<DoubleDimensionItemInfo> doubleDimensionItemInfoList = new ArrayList<>();

        for (int ind=0; ind < horizontalItemList.size(); ++ind) {
            ItemInfo horizontalItemInfo = new ItemInfo(ind, horizontalItemList.get(ind), pureHorizontalItemList.get(ind));
            for (int jnd=0; jnd < verticalItemList.size(); ++jnd) {
                if (reportInfoMap.containsKey(ind) && reportInfoMap.get(ind).containsKey(jnd)) {
                    ItemInfo verticalItemInto = new ItemInfo(jnd, verticalItemList.get(jnd), pureVerticalItemList.get(jnd));
                    DoubleDimensionItemInfo doubleDimensionItemInfo = new DoubleDimensionItemInfo(horizontalItemInfo, verticalItemInto);
                    doubleDimensionItemInfoList.add(doubleDimensionItemInfo);
                }
            }
        }

        setInfo(doubleDimensionItemInfoList, reportInfoMap);
    }

    public DoubleDimensionRichSheetInfo() {}

    private void setInfo(List<DoubleDimensionItemInfo> itemInfoList, Map<Integer, Map<Integer, List<PeriodReportInfo>>> reportInfoMap) {
        if (itemInfoList != null && !itemInfoList.isEmpty()) {
            setItemInfoList(itemInfoList);
        }
        if (reportInfoMap != null && !reportInfoMap.isEmpty()) {
            setReportInfoMap(reportInfoMap);
        }
    }

    public DoubleDimensionRichSheetInfo(List<DoubleDimensionItemInfo> itemInfoList, List<PeriodReportInfo> reportInfoList) {
        if (itemInfoList != null && !itemInfoList.isEmpty()) {
            setItemInfoList(itemInfoList);
        }
        if (reportInfoList != null && !reportInfoList.isEmpty()) {
            setReportInfoMap(reportInfoMap);
        }
    }

    public DoubleDimensionRichSheetInfo(SheetInfo sheetInfo) {
        if (sheetInfo != null) {
            if (sheetInfo instanceof DoubleDimensionRichSheetInfo) {

                DoubleDimensionRichSheetInfo doubleDimensionRichSheetInfo = (DoubleDimensionRichSheetInfo) sheetInfo;
                setInfo(doubleDimensionRichSheetInfo.getItemInfoList(), doubleDimensionRichSheetInfo.getReportInfoMap());

            } else if (sheetInfo instanceof DoubleDimensionRawSheetInfo) {

                DoubleDimensionRawSheetInfo doubleDimensionRawSheetInfo = (DoubleDimensionRawSheetInfo) sheetInfo;
                getRich(new DoubleDimensionRawSheetInfo(doubleDimensionRawSheetInfo));

            }
        }
    }
}
