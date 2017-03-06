package com.smid.app.changesDetector.model;

import java.util.ArrayList;

/**
 * Created by marek on 15.06.16.
 */
public class ActivityAccelerometerData implements IAccelerometerData {
    private long starTime = -1;

    ArrayList<Point> xs;
    ArrayList<Point> ys;
    public ActivityAccelerometerData() {
        xs = new ArrayList<Point>();
        ys = new ArrayList<Point>();
    }

    public void addData(long time, Double x, Double y){
        xs.add(new Point(time, x));
        ys.add(new Point(time, y));
    }

    @Override
    public ArrayList<Point> getXsPoints() {
        return xs;
    }

    @Override
    public ArrayList<Point> getYsPoints() {
        return ys;
    }

    public long getStarTime() {
        return starTime;
    }

    public void setStarTime(long starTime) {
        this.starTime = starTime;
    }
}

