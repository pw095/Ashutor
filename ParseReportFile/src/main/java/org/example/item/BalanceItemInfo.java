package org.example.item;

public class BalanceItemInfo extends ItemInfo implements Comparable<BalanceItemInfo> {

    public int parentItemIndex = -1;
    public int itemLevel = 0;

    public boolean itemHeaderFlag = false;
    public boolean itemSubtotalFlag = false;

    @Override
    public int compareTo(BalanceItemInfo o) {
        return o.itemIndex - this.itemIndex;
    }

    public BalanceItemInfo() {}

    public BalanceItemInfo(BalanceItemInfo balanceItemInfo) {

        super(balanceItemInfo);
        this.parentItemIndex = balanceItemInfo.parentItemIndex;
        this.itemLevel = balanceItemInfo.itemLevel;
        this.itemHeaderFlag = balanceItemInfo.itemHeaderFlag;
        this.itemSubtotalFlag = balanceItemInfo.itemSubtotalFlag;

    }
}
