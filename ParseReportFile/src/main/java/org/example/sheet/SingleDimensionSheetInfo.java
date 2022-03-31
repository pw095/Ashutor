package org.example.sheet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleDimensionSheetInfo extends SheetInfoClass {
    // Необработанная информация с листа

    // Отчёты на листе <Код, Наименование>
    Map<String, String> reportMap = new HashMap<>();

    // Отчётные даты: дата начала, дата окончания
    List<LocalDate> reportBeginDateList = new ArrayList<>();
    List<LocalDate> reportEndDateList = new ArrayList<>();

    // Необработанный список статей
    Map<String, Map<String, Integer>> reportItemMap = new HashMap<>();


    // Необработанные данные
    Map<String, Map<String, List<Long>>> reportItemValueMap = new HashMap<>();

    public SingleDimensionSheetInfo() {}
    public SingleDimensionSheetInfo(SingleDimensionSheetInfo sheetInfo) {
        super(sheetInfo);
/*        if (sheetInfo != null) {
            if (sheetInfo.itemList != null && !sheetInfo.itemList.isEmpty()) {
                this.itemList = new ArrayList<>();
                this.itemList.addAll(sheetInfo.itemList);
            }
            if (sheetInfo.reportInfo != null && !sheetInfo.reportInfo.isEmpty()) {
                this.reportInfo = new ArrayList<>();
                this.reportInfo.addAll(sheetInfo.reportInfo);
            }
        }*/
    }
}
