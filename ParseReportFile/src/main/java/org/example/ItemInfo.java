package org.example;

public class ItemInfo implements Comparable<ItemInfo>{
    public int itemIndex = -1;
    public int parentItemIndex = -1;
    public int itemLevel = 0;
    //        public Map<Integer, Integer> possibleParentItems;
    public String itemName;
    public String itemPureName;
    public boolean itemHeaderFlag = false;
    public boolean itemSubtotalFlag = false;

    @Override
    public int compareTo(ItemInfo o) {
        return o.itemIndex - this.itemIndex;
    }

}
