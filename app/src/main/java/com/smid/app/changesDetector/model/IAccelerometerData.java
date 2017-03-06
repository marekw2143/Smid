package com.smid.app.changesDetector.model;

import java.util.ArrayList;

/**
 * Created by marek on 13.06.16.
 */
public interface IAccelerometerData {
    ArrayList<Point> getXsPoints();

    ArrayList<Point> getYsPoints();
}
