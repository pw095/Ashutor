package org.export;

public class FineItemInfo {

    private String fineItemCode;
    private String fineItemName;
    private String hierPureItemPath;

    public String getFineItemCode() {
        return fineItemCode;
    }

    public String getFineItemName() {
        return fineItemName;
    }

    public String getHierPureItemPath() {
        return hierPureItemPath;
    }

    public void setFineItemCode(String fineItemCode) {
        this.fineItemCode = fineItemCode;
    }

    public void setFineItemName(String fineItemName) {
        this.fineItemName = fineItemName;
    }

    public void setHierPureItemPath(String hierPureItemPath) {
        this.hierPureItemPath = hierPureItemPath;
    }

    FineItemInfo() {}

    public FineItemInfo(String fineItemCode, String fineItemName, String hierPureItemPath) {
        setFineItemCode(fineItemCode);
        setFineItemName(fineItemName);
        setHierPureItemPath(hierPureItemPath);
    }
}