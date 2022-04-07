package org.example.item;

public class DoubleDimensionItemInfo extends AbstractItemInfo {
    public ItemInfo horizontalItemInfo;
    public ItemInfo verticalItemInfo;

    public ItemInfo getHorizontalItemInfo() {
        return new ItemInfo(horizontalItemInfo);
    }
    public void setHorizontalItemInfo(ItemInfo horizontalItemInfo) {
        this.horizontalItemInfo = new ItemInfo(horizontalItemInfo);
    }

    public ItemInfo getVerticalItemInfo() {
        return new ItemInfo(verticalItemInfo);
    }
    public void setVerticalItemInfo(ItemInfo verticalItemInfo) {
        this.verticalItemInfo = new ItemInfo(verticalItemInfo);
    }

    public DoubleDimensionItemInfo() {}

    public DoubleDimensionItemInfo(ItemInfo horizontalItemInfo, ItemInfo verticalItemInfo) {
        setHorizontalItemInfo(horizontalItemInfo);
        setVerticalItemInfo(verticalItemInfo);
    }

    public DoubleDimensionItemInfo(DoubleDimensionItemInfo doubleDimensionItemInfo) {
        this(doubleDimensionItemInfo.getHorizontalItemInfo(), doubleDimensionItemInfo.getVerticalItemInfo());
    }
}
