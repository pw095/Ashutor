package org.example.sheet;

import java.util.ArrayList;
import java.util.List;

public class SingleDimensionSheetInfo extends SheetInfo {
    // Необработанная информация с листа

    // Необработанный список статей
    public List<String> itemList;

    // Необработанные данные
    public List<List<Integer>> reportInfo;

    public SingleDimensionSheetInfo() {}
    public SingleDimensionSheetInfo(SingleDimensionSheetInfo sheetInfo) {
        super(sheetInfo);
        if (sheetInfo != null) {
            if (sheetInfo.itemList != null && !sheetInfo.itemList.isEmpty()) {
                this.itemList = new ArrayList<>();
                this.itemList.addAll(sheetInfo.itemList);
            }
            if (sheetInfo.reportInfo != null && !sheetInfo.reportInfo.isEmpty()) {
                this.reportInfo = new ArrayList<>();
                this.reportInfo.addAll(sheetInfo.reportInfo);
            }
        }
    }
}
