package org.example.item;

public abstract class ItemInfo {
    public int itemIndex = -1;
    public String itemName;
    public String itemPureName;

    public ItemInfo() {}

    public ItemInfo(ItemInfo itemInfo) {
        this.itemIndex = itemInfo.itemIndex;
        this.itemName = itemInfo.itemName;
        this.itemPureName = itemInfo.itemPureName;
    }

}
