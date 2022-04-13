package org.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryResult {

    String fineItemCode;
    String hierPureItemPath;
    int level;
    int index;
    int cnt;
    String groupCnct;
    List<GroupItem> groupItemList;

    public String getFineItemCode() {
        return fineItemCode;
    }

    public String getHierPureItemPath() {
        return hierPureItemPath;
    }

    public int getLevel() {
        return level;
    }

    public int getIndex() {
        return index;
    }

    public int getCnt() {
        return cnt;
    }

    public String getGroupCnct() {
        return groupCnct;
    }

    public List<GroupItem> getGroupItemList() {
        return groupItemList;
    }

    public void setFineItemCode(String fineItemCode) {
        this.fineItemCode = fineItemCode;
    }

    public void setHierPureItemPath(String hierPureItemPath) {
        this.hierPureItemPath = hierPureItemPath;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public void setGroupCnct(String groupCnct) {
        this.groupCnct = groupCnct;
    }

    public void setGroupItemList(List<GroupItem> groupItemList) {
        this.groupItemList = groupItemList;
    }

    QueryResult() {

    }

    public QueryResult(String fineItemCode, String hierPureItemPath, int index, int cnt, String groupCnct) {

        setFineItemCode(fineItemCode);
        setHierPureItemPath(hierPureItemPath);
        setIndex(index);
        setCnt(cnt);
        setGroupCnct(groupCnct);

        List<String> list = Arrays.asList(groupCnct.split(", "));
        List<GroupItem> groupItemList = new ArrayList<>();

        for (String elt : list) {
            groupItemList.add(new GroupItem(elt));
        }

        setGroupItemList(groupItemList);

    }

    public QueryResult(String fineItemCode, String hierPureItemPath, int level, int index, int cnt, String groupCnct) {

        setFineItemCode(fineItemCode);
        setHierPureItemPath(hierPureItemPath);
        setLevel(level);
        setIndex(index);
        setCnt(cnt);
        setGroupCnct(groupCnct);

        List<String> list = Arrays.asList(groupCnct.split(", "));
        List<GroupItem> groupItemList = new ArrayList<>();

        for (String elt : list) {
            groupItemList.add(new GroupItem(elt));
        }

        setGroupItemList(groupItemList);

    }
}
