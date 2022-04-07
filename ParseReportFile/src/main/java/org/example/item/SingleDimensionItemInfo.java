package org.example.item;

public class SingleDimensionItemInfo extends AbstractItemInfo {
    private ItemInfo itemInfo;

    public ItemInfo getItemInfo() {
        return new ItemInfo(itemInfo);
    }
    public void setItemInfo(ItemInfo itemInfo) {
        this.itemInfo = new ItemInfo(itemInfo);
    }

    public SingleDimensionItemInfo() {}

    public SingleDimensionItemInfo(ItemInfo itemInfo) {
        setItemInfo(itemInfo);
    }

    public SingleDimensionItemInfo(SingleDimensionItemInfo singleDimensionItemInfo) {
        this(singleDimensionItemInfo.getItemInfo());
    }
}
