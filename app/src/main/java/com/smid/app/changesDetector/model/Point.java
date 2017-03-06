package com.smid.app.changesDetector.model;

/**
 * Created by marek on 10.06.16.
 */
public class Point {
    private long time;
    private final double value;

    public Point(long time, double value) {
        this.time = time;
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public void setTime(long tt) {
        time = tt;
    }
}


