package org.example.sheet;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.example.PossibleParent;
import org.example.item.BalanceItemInfo;
import org.example.report.SnapshotReportInfo;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BalanceRichSheetInfo implements SheetInfo {

    // Обработанная информация с листа
    private List<BalanceItemInfo> balanceItemInfoList;
    private Map<Integer, List<SnapshotReportInfo>> reportInfoMap;

    public List<BalanceItemInfo> getBalanceItemInfoList() {
        return new ArrayList<>(balanceItemInfoList);
    }
    public void setBalanceItemInfoList(List<BalanceItemInfo> balanceItemInfoList) {
        this.balanceItemInfoList = new ArrayList<>(balanceItemInfoList);
    }

    public Map<Integer, List<SnapshotReportInfo>> getReportInfoMap() {
        return new HashMap<>(reportInfoMap);
    }
    public void setReportInfoMap(Map<Integer, List<SnapshotReportInfo>> reportInfoMap) {
        this.reportInfoMap = new HashMap<>(reportInfoMap);
    }

    private void getRich(BalanceRawSheetInfo balanceRawSheetInfo) {

        List<LocalDate> reportDateList = balanceRawSheetInfo.getReportDateList();
        List<String> itemList = balanceRawSheetInfo.getItemList();
        Map<Integer, Map<Integer, Long>> reportInfo = balanceRawSheetInfo.getReportInfo();

        List<String> pureItemList = itemList.stream().map(p -> p.toLowerCase().replaceAll("[\\p{Punct}\\p{Blank}]+", " ").trim()).collect(Collectors.toList());
        if (pureItemList.contains("обязательства")) {
        } else if (pureItemList.contains("liabilities")) {
        } else if (pureItemList.contains("долгосрочные обязательства")) {

            int ii = pureItemList.indexOf("долгосрочные обязательства");

            itemList.add(ii, "Обязательства");
            pureItemList.add(ii, "обязательства");

            Map<Integer, Map<Integer, Long>> newReportInfo = new HashMap<>();

            for (int ind : reportInfo.keySet()) {
                if (ind < ii) {
                    newReportInfo.put(ind, reportInfo.get(ind));
                } else {
                    newReportInfo.put(ind+1, reportInfo.get(ind));
                }
            }

            reportInfo = newReportInfo;

        } else if (pureItemList.contains("non current liabilities")) {

            int ii = pureItemList.indexOf("non current liabilities");

            itemList.add(ii, "Liabilities");
            pureItemList.add(ii, "liabilities");

            Map<Integer, Map<Integer, Long>> newReportInfo = new HashMap<>();

            for (int ind : reportInfo.keySet()) {
                if (ind < ii) {
                    newReportInfo.put(ind, reportInfo.get(ind));
                } else {
                    newReportInfo.put(ind+1, reportInfo.get(ind));
                }
            }

            reportInfo = newReportInfo;

        }

        List<BalanceItemInfo> balanceItemInfoList = new ArrayList<>();
        Map<Integer, List<SnapshotReportInfo>> reportInfoMap = new HashMap<>();
        for (int ind=0; ind < itemList.size(); ++ind) {

            BalanceItemInfo balanceItemInfo = new BalanceItemInfo();

            balanceItemInfo.setItemIndex(ind);
            balanceItemInfo.setItemName(itemList.get(ind));
            balanceItemInfo.setItemPureName(pureItemList.get(ind));

            if (reportInfo.get(ind) == null || reportInfo.get(ind).isEmpty()) {
                balanceItemInfo.setItemHeaderFlag(true);
            }
            if (pureItemList.get(ind).startsWith("итого") || pureItemList.get(ind).startsWith("total")) {
                balanceItemInfo.setItemSubtotalFlag(true);
            }

            balanceItemInfoList.add(balanceItemInfo);
        }

        for (int ind : reportInfo.keySet()) {
            Map<Integer, Long> reportString = reportInfo.get(ind);
            List<SnapshotReportInfo> reportInfoString = new ArrayList<>();
            for (int jnd : reportString.keySet()) {
                reportInfoString.add(new SnapshotReportInfo(reportDateList.get(jnd), reportString.get(jnd)));
            }
            reportInfoMap.put(ind, reportInfoString);
        }

        for (BalanceItemInfo balanceItemInfo : balanceItemInfoList) {
            if (balanceItemInfo.getItemSubtotalFlag()) {
                int tt;
                tt = balanceItemInfoList.stream()
                    .filter(p -> p.getItemHeaderFlag())
                    .filter(p -> p.getItemIndex() < balanceItemInfo.getItemIndex())
                    .filter(p -> balanceItemInfo.getItemPureName().equals("итого " + p.getItemPureName()) || balanceItemInfo.getItemPureName().equals("total " + p.getItemPureName()))
                    .mapToInt(p -> p.getItemIndex()).findFirst().orElse(-2);
                balanceItemInfo.setParentItemIndex(tt);
            }
        }

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        for (BalanceItemInfo balanceItemInfo : balanceItemInfoList) {
            // Отбираем, те у которых Subtotal flag = 1
            if (balanceItemInfo.getParentItemIndex() == -2) {
                int tt;
                tt = balanceItemInfoList.stream()
                        // Шагаем по элементам, которые являются заголовками
                        .filter(p -> p.getItemHeaderFlag())
                        // Отбираем те заголовки, у которых порядковый номер меньше нашей записи
                        .filter(p -> p.getItemIndex() < balanceItemInfo.getItemIndex())
                        // Отбираем те заголовки, которые никому более присвоены не были
                        .filter(p -> balanceItemInfoList.stream().mapToInt(t -> t.getParentItemIndex()).noneMatch(u -> u == p.getItemIndex()))
                        // Для каждого такого заголовка рассчитываем его путь до нашего элемента
                        .map(p -> new PossibleParent(p.getItemIndex(), levenshteinDistance.apply(p.getItemPureName(), balanceItemInfo.getItemPureName())))
                        .sorted()
                        .mapToInt(p -> p.getParentIndex())
                        .findFirst()
                        .orElse(-3);
                balanceItemInfo.setParentItemIndex(tt);
            }
        }

        int kk = 0;
        int indStart = 0;
        for (int ii = 0; ii < balanceItemInfoList.size(); ++ii) {
            BalanceItemInfo balanceItemInfo = balanceItemInfoList.get(ii);
            if (balanceItemInfo.getItemHeaderFlag() || balanceItemInfo.getItemSubtotalFlag()) {
                if (balanceItemInfo.getItemHeaderFlag()) {
                    int tt;
                    tt = balanceItemInfoList
                        .subList(indStart, ii).stream()
                        .filter(p -> p.getItemHeaderFlag() || p.getItemSubtotalFlag())
                        .sorted()
                        .skip(kk)
                        .map(p -> p.getItemIndex()).findFirst().orElse(-2);
                    balanceItemInfo.setParentItemIndex(tt);
                }
                if (balanceItemInfo.getItemSubtotalFlag()) {
                    kk += 2;
                } else if (kk > 0 && balanceItemInfoList.get(ii - 1).getItemSubtotalFlag() && balanceItemInfoList.get(ii).getItemHeaderFlag()) {
                    kk -= 2;
                }
                if (balanceItemInfo.getParentItemIndex() == 0 && balanceItemInfo.getItemSubtotalFlag()) {
                    kk = 0;
                    indStart = ii + 1;
                }
            }
        }

        int headerIndex = 0;
        for (int ii = 0; ii < balanceItemInfoList.size(); ++ii) {
            BalanceItemInfo balanceItemInfo = balanceItemInfoList.get(ii);
            if (balanceItemInfo.getItemHeaderFlag() || balanceItemInfo.getItemSubtotalFlag()) {
                headerIndex = ii;
            }
            if (balanceItemInfo.getParentItemIndex() == -1) {
                balanceItemInfo.setParentItemIndex(balanceItemInfoList.get(headerIndex).getItemIndex());
            }
        }

        int lag_ind = 0;
        for (BalanceItemInfo balanceItemInfo : balanceItemInfoList) {

            balanceItemInfo.setItemLevel(lag_ind + (balanceItemInfo.getItemSubtotalFlag() ? -1 : 0));
            lag_ind += (balanceItemInfo.getItemSubtotalFlag() ? -1 : 0) + (balanceItemInfo.getItemHeaderFlag() ? 1 : 0);

        }

        setBalanceItemInfoList(balanceItemInfoList);
        setReportInfoMap(reportInfoMap);
    }

    public BalanceRichSheetInfo() {}

    public BalanceRichSheetInfo(SheetInfo balanceSheetInfo) {
        if (balanceSheetInfo != null) {
            if (balanceSheetInfo instanceof BalanceRichSheetInfo) {

                BalanceRichSheetInfo balanceRichSheetInfo = (BalanceRichSheetInfo) balanceSheetInfo;

                if (balanceRichSheetInfo.balanceItemInfoList != null && !balanceRichSheetInfo.balanceItemInfoList.isEmpty()) {
                    setBalanceItemInfoList(balanceRichSheetInfo.getBalanceItemInfoList());
                }

                if (balanceRichSheetInfo.reportInfoMap != null && !balanceRichSheetInfo.reportInfoMap.isEmpty()) {
                    setReportInfoMap(balanceRichSheetInfo.reportInfoMap);
                }

            } else if (balanceSheetInfo instanceof BalanceRawSheetInfo) {

                BalanceRawSheetInfo balanceRawSheetInfo = (BalanceRawSheetInfo) balanceSheetInfo;

                getRich(new BalanceRawSheetInfo(balanceRawSheetInfo));

            }
        }
    }
}
