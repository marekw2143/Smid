package com.smid.app.gestureDetection;

import android.content.Context;

import com.smid.app.Helper;
import com.smid.app.changesDetector.model.ActivityAccelerometerData;

/**
 * Created by marek on 15.06.16.
 */
public class GestureMoveCollector extends AccelerometerDataListener {
    private long startTime = -1;

    ActivityAccelerometerData accelerometerData;

    public GestureMoveCollector(Context ctx) {
        super(ctx);
    }

    @Override
    protected void onStartCollectingData() {
        startTime = Helper.getTime();
        accelerometerData = new ActivityAccelerometerData();
        accelerometerData.setStarTime(startTime);
    }

    @Override
    protected void onStopCollectingData() {
    }

    @Override
    protected void processAccelerometerData(float x, float y, float z, long tickTime) {
        accelerometerData.addData(tickTime, ((double) x),((double) y));
    }

    public ActivityAccelerometerData getAccelerometerData(){
        return accelerometerData;
    }
}
