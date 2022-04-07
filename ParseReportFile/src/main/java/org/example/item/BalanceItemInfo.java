package org.example.item;

public class BalanceItemInfo extends ItemInfo implements Comparable<BalanceItemInfo> {

    private int parentItemIndex = -1;
    private int itemLevel = 0;

    private boolean itemHeaderFlag = false;
    private boolean itemSubtotalFlag = false;

    public int getParentItemIndex() {
        return parentItemIndex;
    }

    public int getItemLevel() {
        return itemLevel;
    }

    public boolean getItemHeaderFlag() {
        return itemHeaderFlag;
    }

    public boolean getItemSubtotalFlag() {
        return itemSubtotalFlag;
    }

    public void setParentItemIndex(int parentItemIndex) {
        this.parentItemIndex = parentItemIndex;
    }

    public void setItemLevel(int itemLevel) {
        this.itemLevel = itemLevel;
    }

    public void setItemHeaderFlag(boolean itemHeaderFlag) {
        this.itemHeaderFlag = itemHeaderFlag;
    }

    public void setItemSubtotalFlag(boolean itemSubtotalFlag) {
        this.itemSubtotalFlag = itemSubtotalFlag;
    }

    @Override
    public int compareTo(BalanceItemInfo o) {
        return o.getItemIndex() - this.getItemIndex();
    }

    public BalanceItemInfo() {}

    public BalanceItemInfo(BalanceItemInfo balanceItemInfo) {

        super(balanceItemInfo);
        setParentItemIndex(balanceItemInfo.getParentItemIndex());
        setItemLevel(balanceItemInfo.getItemLevel());
        setItemHeaderFlag(balanceItemInfo.getItemHeaderFlag());
        setItemSubtotalFlag(balanceItemInfo.getItemSubtotalFlag());

    }

    public BalanceItemInfo(
        int itemIndex, String itemName, String itemPureName,
        int parentItemIndex, int itemLevel, boolean itemHeaderFlag, boolean itemSubtotalFlag) {
        super(itemIndex,itemName, itemPureName);
        setParentItemIndex(parentItemIndex);
        setItemLevel(itemLevel);
        setItemHeaderFlag(itemHeaderFlag);
        setItemSubtotalFlag(itemSubtotalFlag);
    }
}
