package com.smid.app.changesDetector.model;

/**
 * Created by marek on 13.06.16.
 */

public class DiscretePoint {
    private final long time;
    private final byte value;
    private Double origValue;

    public DiscretePoint(long time, byte value, Double origValue) {
        this.time = time;
        this.value = value;
        this.origValue = origValue;
    }

    public long getTime() {
        return time;
    }

    public byte getValue() {
        return value;
    }

    public Double getOrigValue(){
        return origValue;
    }
}
