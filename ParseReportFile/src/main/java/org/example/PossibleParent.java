package org.example;

public class PossibleParent implements Comparable<PossibleParent>{

    private int parentIndex;
    private int lDistance;

    public int getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public int getlDistance() {
        return lDistance;
    }

    public void setlDistance(int lDistance) {
        this.lDistance = lDistance;
    }

    public PossibleParent(int parentIndex, int lDistance) {
        this.parentIndex = parentIndex;
        this.lDistance = lDistance;
    }

    @Override
    public int compareTo(PossibleParent o) {
        return this.lDistance - o.lDistance;
    }
}
