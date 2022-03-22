package org.example.item;

public class ItemInfo extends AbstractItemInfo {
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
