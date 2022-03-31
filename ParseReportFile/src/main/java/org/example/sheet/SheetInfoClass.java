package org.example.sheet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class SheetInfoClass implements SheetInfo {

    public List<LocalDate> reportDateList;

    public SheetInfoClass() {}
    public SheetInfoClass(SheetInfoClass sheetInfo) {
        if (sheetInfo != null && sheetInfo.reportDateList != null && !sheetInfo.reportDateList.isEmpty()) {
            this.reportDateList = new ArrayList<>();
            this.reportDateList.addAll(sheetInfo.reportDateList);
        }
    }

}
