package org.entity;

import org.example.sheet.*;

import java.util.*;

public class AbstractReport {

    private RefSheetInfo refSheetInfo;
    private BalanceRawSheetInfo balanceRawSheetInfo;
    private Map<String, SingleDimensionRawSheetInfo> singleDimensionRawSheetInfoMap;
    private Map<String, DoubleDimensionRawSheetInfo> doubleDimensionRawSheetInfoMap;

    private BalanceRichSheetInfo balanceRichSheetInfo;
    private Map<String, SingleDimensionRichSheetInfo> singleDimensionRichSheetInfoMap;
    private Map<String, DoubleDimensionRichSheetInfo> doubleDimensionRichSheetInfoMap;

    public RefSheetInfo getRefSheetInfo() {
        return new RefSheetInfo(refSheetInfo);
    }
    public void setRefSheetInfo(RefSheetInfo refSheetInfo) {
        this.refSheetInfo = refSheetInfo;
    }

    public BalanceRawSheetInfo getBalanceRawSheetInfo() { return new BalanceRawSheetInfo(balanceRawSheetInfo); }
    public void setBalanceRawSheetInfo(BalanceRawSheetInfo balanceRawSheetInfo) {
        this.balanceRawSheetInfo = new BalanceRawSheetInfo(balanceRawSheetInfo);
    }

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

    public BalanceRichSheetInfo getBalanceRichSheetInfo() { return new BalanceRichSheetInfo(balanceRichSheetInfo); }
    public void setBalanceRichSheetInfo(BalanceRichSheetInfo balanceRichSheetInfo) {
        this.balanceRichSheetInfo = new BalanceRichSheetInfo(balanceRichSheetInfo);
    }

    public HashMap<String, SingleDimensionRichSheetInfo> getSingleDimensionRichSheetInfoMap() {
        return new HashMap<>(singleDimensionRichSheetInfoMap);
    }
    public void setSingleDimensionRichSheetInfoMap(Map<String, SingleDimensionRichSheetInfo> singleDimensionRichSheetInfoMap) {
        this.singleDimensionRichSheetInfoMap = new HashMap<>(singleDimensionRichSheetInfoMap);
    }

    public HashMap<String, DoubleDimensionRichSheetInfo> getDoubleDimensionRichSheetInfoMap() {
        return new HashMap<>(doubleDimensionRichSheetInfoMap);
    }
    public void setDoubleDimensionRichSheetInfoMap(Map<String, DoubleDimensionRichSheetInfo> doubleDimensionRichSheetInfoMap) {
        this.doubleDimensionRichSheetInfoMap = new HashMap<>(doubleDimensionRichSheetInfoMap);
    }

    public AbstractReport() {

        this.refSheetInfo = new RefSheetInfo();

        this.balanceRawSheetInfo = new BalanceRawSheetInfo();
        this.singleDimensionRawSheetInfoMap = new HashMap<>();
        this.doubleDimensionRawSheetInfoMap = new HashMap<>();

        this.balanceRichSheetInfo = new BalanceRichSheetInfo();
        this.singleDimensionRichSheetInfoMap = new HashMap<>();
        this.doubleDimensionRichSheetInfoMap = new HashMap<>();

    }

    public AbstractReport(AbstractReport abstractReport) {

        setRefSheetInfo(abstractReport.getRefSheetInfo());

        setBalanceRawSheetInfo(abstractReport.getBalanceRawSheetInfo());
        setSingleDimensionRawSheetInfoMap(abstractReport.getSingleDimensionRawSheetInfoMap());
        setDoubleDimensionRawSheetInfoMap((abstractReport.getDoubleDimensionRawSheetInfoMap()));

        setBalanceRichSheetInfo(abstractReport.getBalanceRichSheetInfo());
        setSingleDimensionRichSheetInfoMap(abstractReport.getSingleDimensionRichSheetInfoMap());
        setDoubleDimensionRichSheetInfoMap(abstractReport.getDoubleDimensionRichSheetInfoMap());

    }
}
