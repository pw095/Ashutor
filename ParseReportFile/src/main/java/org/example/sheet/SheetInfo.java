package org.example.sheet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class SheetInfo {

    public List<LocalDate> reportDateList;

    public SheetInfo() {}
    public SheetInfo(SheetInfo sheetInfo) {
        if (sheetInfo != null && sheetInfo.reportDateList != null && !sheetInfo.reportDateList.isEmpty()) {
            this.reportDateList = new ArrayList<>();
            this.reportDateList.addAll(sheetInfo.reportDateList);
        }
    }

}
