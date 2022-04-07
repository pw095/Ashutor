package org.entity;

import org.example.sheet.*;

import java.util.HashMap;
import java.util.Map;

public class RichReport {

//    private RefSheetInfo refSheetInfo;
    private BalanceRichSheetInfo balanceRichSheetInfo;
    private Map<String, SingleDimensionRichSheetInfo> singleDimensionRichSheetInfoMap;
    /*private Map<String, DoubleDimensionRawSheetInfo> doubleDimensionRawSheetInfoMap;*/

//    public void setRefSheetInfo(RefSheetInfo refSheetInfo) {
//        this.refSheetInfo = refSheetInfo;
//    }

//    public RefSheetInfo getRefSheetInfo() {
//        return new RefSheetInfo(refSheetInfo);
//    }

    public void setBalanceRichSheetInfo(SheetInfo balanceRichSheetInfo) {
        this.balanceRichSheetInfo = new BalanceRichSheetInfo(balanceRichSheetInfo);
    }
    public BalanceRichSheetInfo getBalanceRichSheetInfo() { return new BalanceRichSheetInfo(balanceRichSheetInfo); }

    public HashMap<String, SingleDimensionRichSheetInfo> SingleDimensionRichSheetInfo() {
        return new HashMap<>(singleDimensionRichSheetInfoMap);
    }
    public void SingleDimensionRichSheetInfo(Map<String, SingleDimensionRichSheetInfo> singleDimensionRichSheetInfoMap) {
        this.singleDimensionRichSheetInfoMap = new HashMap<>(singleDimensionRichSheetInfoMap);
    }
/*
    public HashMap<String, DoubleDimensionRawSheetInfo> getDoubleDimensionRawSheetInfoMap() {
        return new HashMap<>(doubleDimensionRawSheetInfoMap);
    }
    public void setDoubleDimensionRawSheetInfoMap(Map<String, DoubleDimensionRawSheetInfo> doubleDimensionRawSheetInfoMap) {
        this.doubleDimensionRawSheetInfoMap = new HashMap<>(doubleDimensionRawSheetInfoMap);
    }
*/

    public RichReport() {

//        this.refSheetInfo = new RefSheetInfo();
        this.balanceRichSheetInfo = new BalanceRichSheetInfo();
        this.singleDimensionRichSheetInfoMap = new HashMap<>();
        /*
        this.doubleDimensionRawSheetInfoMap = new HashMap<>();*/
    }

}
