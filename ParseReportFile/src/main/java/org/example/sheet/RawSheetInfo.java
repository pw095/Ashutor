package org.example.sheet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class RawSheetInfo implements SheetInfo {

    public List<LocalDate> reportDateList;

    public RawSheetInfo() {}
    public RawSheetInfo(RawSheetInfo sheetInfo) {
        if (sheetInfo != null && sheetInfo.reportDateList != null && !sheetInfo.reportDateList.isEmpty()) {
            this.reportDateList = new ArrayList<>();
            this.reportDateList.addAll(sheetInfo.reportDateList);
        }
    }

}
