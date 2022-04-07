package org.example.item;

public class ItemInfo extends AbstractItemInfo {
    private int itemIndex = -1;
    private String itemName;
    private String itemPureName;

    public int getItemIndex() {
        return itemIndex;
    }
    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPureName() {
        return itemPureName;
    }
    public void setItemPureName(String itemPureName) {
        this.itemPureName  = itemPureName;
    }

    public ItemInfo() {}

    public ItemInfo(int itemIndex, String itemName, String itemPureName) {
        setItemIndex(itemIndex);
        setItemName(itemName);
        setItemPureName(itemPureName);
    }

    public ItemInfo(ItemInfo itemInfo) {
        setItemIndex(itemInfo.getItemIndex());
        setItemName(itemInfo.getItemName());
        setItemPureName(itemInfo.getItemPureName());
    }

}
