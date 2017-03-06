package com.smid.app.changesDetector.nomotion;


import com.smid.app.changesDetector.model.IAccelerometerData;
import com.smid.app.changesDetector.model.Point;

import java.util.ArrayList;

/**
 * Created by marek on 13.06.16.
 */
public class Chunk implements IAccelerometerData {
    private long timeValidFrom;
    private long timeValidTo;

    ArrayList<Point> xs = new ArrayList<>();
    ArrayList<Point> ys = new ArrayList<>();

    public void addMeasurement(long time, Double ax, Double ay){
        this.xs.add(new Point(time, ax));
        this.ys.add(new Point(time, ay));
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


