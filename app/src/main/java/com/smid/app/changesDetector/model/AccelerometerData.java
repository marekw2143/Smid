package com.smid.app.changesDetector.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marek on 11.06.16.
 */
public class AccelerometerData implements IAccelerometerData {
    private List<Long> ts = new ArrayList<>(1000);
    private List<Double> xs = new ArrayList<>(1000);
    private List<Double> ys = new ArrayList<>(1000);

    private ArrayList<Point> xsPoints = null;
    private ArrayList<Point> ysPoints = null;

    public ArrayList<Point> createPoints(List<Double> axis) {
        ArrayList<Point> ret = new ArrayList<>(ts.size());

        for(int i=0;i<ts.size();i++){
            Point p = new Point(ts.get(i), axis.get(i));
            ret.add(p);
        }

        return ret;
    }

    public List<Long> getTs() {
        return ts;
    }

    public List<Double> getXs() {
        return xs;
    }

    public List<Double> getYs() {
        return ys;
    }

    public void buildXsYsPoints() {
        xsPoints = createPoints(xs);
        ysPoints = createPoints(ys);
    }


    @Override
    public ArrayList<Point> getXsPoints() {
        if(xsPoints == null) {
            buildXsYsPoints();
        }

        return xsPoints;
    }

    @Override
    public ArrayList<Point> getYsPoints() {
        if(ysPoints == null) {
            buildXsYsPoints();
        }

        return ysPoints;
    }
}
