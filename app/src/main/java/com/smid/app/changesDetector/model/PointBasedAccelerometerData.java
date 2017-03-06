package com.smid.app.changesDetector.model;

import java.util.ArrayList;

/**
 * Created by marek on 15.06.16.
 */
public class PointBasedAccelerometerData implements  IAccelerometerData {
    private ArrayList<Point> xs;
    private ArrayList<Point> ys;

    public PointBasedAccelerometerData(ArrayList<Point> xs, ArrayList<Point> ys) {
        this.xs = xs;

        this.ys = ys;
    }
    @Override
    public ArrayList<Point> getXsPoints() {
        return xs;
    }

    @Override
    public ArrayList<Point> getYsPoints() {
        return ys;
    }
}
