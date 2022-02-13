package org.example;

public class PossibleParent implements Comparable<PossibleParent>{
    public int parentIndex;
    public int lDistance;

    PossibleParent(int parentIndex, int lDistance) {
        this.parentIndex = parentIndex;
        this.lDistance = lDistance;
    }

    @Override
    public int compareTo(PossibleParent o) {
        return this.lDistance - o.lDistance;
    }
}
