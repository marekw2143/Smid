package com.smid.app.changesDetector;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.model.IAccelerometerData;
import com.smid.app.changesDetector.model.Point;
import com.smid.app.changesDetector.model.PointBasedAccelerometerData;

import java.util.ArrayList;
import java.util.List;

/**
 * From acceleromeetr data generates motion description.
 */
public class DataPreparer {
    public IAccelerometerData getAveragedAccelerometerData(IAccelerometerData accelerometerData) {
        ArrayList<Point> averageXs = averageCalculator(accelerometerData.getXsPoints(), GD_CONSTS.TIME_WINDOW_AVERAGING_XS);
        ArrayList<Point> averageYs = averageCalculator(accelerometerData.getYsPoints(), GD_CONSTS.TIME_WINDOW_AVERAGING_YS);

        PointBasedAccelerometerData pointBasedAccelerometerData = new PointBasedAccelerometerData(averageXs, averageYs);

        return pointBasedAccelerometerData;
    }


    protected ArrayList<Point> averageCalculator(List<Point> points, long windowPartHeight) {
        ArrayList<Point> ret = new ArrayList<>(points.size());

        for(int i=0;i<points.size();i++){
            Point a = points.get(i);

            Double sum = 0.0;

            int elements = 0;

            for(int down = i -1; down >= 0; down--){
                Point b = points.get(down);

                if(a.getTime() - b.getTime() <= windowPartHeight) {
                    sum += b.getValue();
                    elements += 1;
                } else {
                    break;
                }
            }

            for(int up = i + 1; up < points.size(); up++){
                Point b = points.get(up);

                if(b.getTime() - a.getTime() <= windowPartHeight) {
                    sum += b.getValue();
                    elements += 1;
                } else {
                    break;
                }
            }
//            for(int j=0;j<points.size();j++){
//                Point b = points.get(j);
//
//                if(Math.abs(a.getTime() - b.getTime()) <= windowPartHeight){
//                    sum += b.getValue();
//                    elements += 1;
//                }
//            }

            ret.add(new Point(a.getTime(), sum/elements));
        }

        return ret;
    }
}
