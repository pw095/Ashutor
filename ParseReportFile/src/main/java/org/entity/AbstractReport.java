package org.entity;

import org.example.sheet.BalanceRawSheetInfo;
import org.example.sheet.DoubleDimensionRawSheetInfo;
import org.example.sheet.RefSheetInfo;
import org.example.sheet.SingleDimensionRawSheetInfo;

import java.util.*;

public abstract class AbstractReport {

    private RefSheetInfo refSheetInfo;
    private BalanceRawSheetInfo balanceRawSheetInfo;
    private Map<String, SingleDimensionRawSheetInfo> singleDimensionRawSheetInfoMap;
    private Map<String, DoubleDimensionRawSheetInfo> doubleDimensionRawSheetInfoMap;

    public void setRefSheetInfo(RefSheetInfo refSheetInfo) {
        this.refSheetInfo = refSheetInfo;
    }
    public void setBalanceRawSheetInfo(BalanceRawSheetInfo balanceRawSheetInfo) {
        this.balanceRawSheetInfo = balanceRawSheetInfo;
    }

    public RefSheetInfo getRefSheetInfo() {
        return new RefSheetInfo(refSheetInfo);
    }
    public BalanceRawSheetInfo getBalanceRawSheetInfo() { return new BalanceRawSheetInfo(balanceRawSheetInfo); }

    public HashMap<String, SingleDimensionRawSheetInfo> getSingleDimensionRawSheetInfoMap() {
        return new HashMap<>(singleDimensionRawSheetInfoMap);
    }
    public void setSingleDimensionRawSheetInfoMap(Map<String, SingleDimensionRawSheetInfo> singleDimensionRawSheetInfoMap) {
        this.singleDimensionRawSheetInfoMap = new HashMap<>(singleDimensionRawSheetInfoMap);
    }

    public HashMap<String, DoubleDimensionRawSheetInfo> getDoubleDimensionRawSheetInfoMap() {
        return new HashMap<>(doubleDimensionRawSheetInfoMap);
    }
    public void setDoubleDimensionRawSheetInfoMap(Map<String, DoubleDimensionRawSheetInfo> doubleDimensionRawSheetInfoMap) {
        this.doubleDimensionRawSheetInfoMap = new HashMap<>(doubleDimensionRawSheetInfoMap);
    }

    public AbstractReport() {

        this.refSheetInfo = new RefSheetInfo();
        this.balanceRawSheetInfo = new BalanceRawSheetInfo();
        this.singleDimensionRawSheetInfoMap = new HashMap<>();
        this.doubleDimensionRawSheetInfoMap = new HashMap<>();
    }

}
