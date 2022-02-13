package org.example;

import java.time.LocalDate;
import java.util.List;

public class RichBalanceSheetInfo extends SheetInfo {

    public static class ReportInfo {
        public int reportItemIndex;
        public LocalDate reportDate;
        public int reportValue;
    }

    // Обработанная информация с листа
    public List<ItemInfo> itemInfoList;
    public List<ReportInfo> reportInfoList;

}
