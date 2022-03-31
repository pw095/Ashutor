package org.example.sheet;

import java.time.LocalDate;
import java.util.*;

public class DoubleDimensionSheetInfoClass extends SheetInfoClass {
    // Необработанная информация с листа

    // Необработанный список статей
    public List<String> horizontalItemList;
    public List<String> verticalItemList;

    // Необработанные данные
    public Map<LocalDate, Map<String, Map<String, Integer>>> reportInfo;

    public DoubleDimensionSheetInfoClass() {}

    public DoubleDimensionSheetInfoClass(DoubleDimensionSheetInfoClass sheetInfo) {
        super(sheetInfo);
        if (sheetInfo != null) {
            if (sheetInfo.horizontalItemList != null && !sheetInfo.horizontalItemList.isEmpty()) {
                this.horizontalItemList = new LinkedList<>();
                this.horizontalItemList.addAll(sheetInfo.horizontalItemList);
            }
            if (sheetInfo.verticalItemList != null && !sheetInfo.verticalItemList.isEmpty()) {
                this.verticalItemList = new LinkedList<>();
                this.verticalItemList.addAll(sheetInfo.verticalItemList);
            }

            this.reportInfo = null;

            if (sheetInfo.reportInfo != null && !sheetInfo.reportInfo.isEmpty()) {
                this.reportInfo = new HashMap<>();
                for (LocalDate reportDate : sheetInfo.reportInfo.keySet()) {
                    Map<String, Map<String, Integer>> reportElement = sheetInfo.reportInfo.get(reportDate);
                    Map<String, Map<String, Integer>> reportElementCopy = new HashMap<>();
                    if (reportElement != null && !reportElement.isEmpty()) {
                        for (String horizontalIndex : reportElement.keySet()) {
                            Map<String, Integer> subReportElement = reportElement.get(horizontalIndex);
                            if (subReportElement != null && ! subReportElement.isEmpty()) {
                                reportElementCopy.put(horizontalIndex, new HashMap<>(subReportElement));
                            }
                        }
                        this.reportInfo.put(reportDate, reportElementCopy);
                    }
                }
            }
        }
    }
}
