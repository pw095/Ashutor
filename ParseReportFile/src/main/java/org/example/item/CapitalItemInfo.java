package org.example.item;

public class CapitalItemInfo extends AbstractItemInfo {

    public ItemInfo horizontalItemInfo;
    public ItemInfo verticalItemInfo;

    public CapitalItemInfo() {}

    public CapitalItemInfo(CapitalItemInfo capitalItemInfo) {
        this.horizontalItemInfo = new ItemInfo(capitalItemInfo.horizontalItemInfo);
        this.verticalItemInfo = new ItemInfo(capitalItemInfo.verticalItemInfo);
    }

    public CapitalItemInfo(ItemInfo horizontalItemInfo, ItemInfo verticalItemInfo) {
        this.horizontalItemInfo = new ItemInfo(horizontalItemInfo);
        this.verticalItemInfo = new ItemInfo(verticalItemInfo);
    }

}
