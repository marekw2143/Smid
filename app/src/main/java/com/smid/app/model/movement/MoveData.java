package com.smid.app.model.movement;

import com.smid.app.changesDetector.model.ActivityAccelerometerData;
import com.smid.app.changesDetector.model.Point;
import com.smid.app.model.ModelBase;
import com.smid.app.model.movement.pojo.AccelerationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marek on 05.05.16.
 */
public class MoveData extends ModelBase {
    private long startTime = -1;

    private List<AccelerationData> accelerationData;

    public MoveData () {
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) throws Exception {
        if(this.startTime >= 0) {
            throw new Exception("Cannot set startTime twice");
        }

        this.startTime = startTime;
    }

    public void setAccelerationData(ActivityAccelerometerData ad) {
        accelerationData = new ArrayList<>(ad.getXsPoints().size());

        for(int i=0;i<ad.getXsPoints().size() ;i++){
            Point x = ad.getXsPoints().get(i);
            Point y = ad.getYsPoints().get(i);
            accelerationData.add(new AccelerationData(x.getValue(), y.getValue(), 0.0f, x.getTime()));
        }

        startTime = ad.getStarTime();
    }

    public List<AccelerationData> getAccelerationData() {
        return accelerationData;
    }
}
