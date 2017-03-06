package com.smid.app.gestureDetection;

import android.content.Context;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.nomotion.StayingChecker;

/**
 * Created by marek on 15.06.16.
 */
public class NomotionDetector extends AccelerometerDataListener {
    private INomotionManager iNomotionManager;
    StayingChecker stayingChecker;

    private Object nomotinCallbackSyncrhonizer = new Object();

    public NomotionDetector(Context ctx, INomotionManager iNomotionManager) {
        super(ctx);

        construct(ctx, iNomotionManager, (long)GD_CONSTS.NOMOTION_TIME, GD_CONSTS.NOMOTION_TIME_WINDOW_SIZE, GD_CONSTS.NOMOTION_X_THRESHOLD);
    }

    public NomotionDetector(Context ctx, INomotionManager iNomotionManager, long msTicks, long window_size, Double maxAvg) {
        super(ctx);
        construct(ctx, iNomotionManager, msTicks, window_size, maxAvg);
    }

    protected void construct(Context ctx, INomotionManager iNomotionManager, long msTicks, long window_size, Double maxAvg) {
        this.iNomotionManager = iNomotionManager;
        stayingChecker = new StayingChecker(msTicks, window_size, maxAvg);
    }



    @Override
    protected void onStartCollectingData() {

    }

    @Override
    protected void onStopCollectingData() {

    }

    public void clearNomotionDetectedListener() {
        synchronized (nomotinCallbackSyncrhonizer) {
            iNomotionManager = null;
        }
    }

    @Override
    protected void processAccelerometerData(float x, float y, float z, long tickTime) {
        stayingChecker.addMeasurement(tickTime, x, y);

        if(stayingChecker.isPhoneNomotion()){
            synchronized(nomotinCallbackSyncrhonizer) {
                if (iNomotionManager != null) {
                    iNomotionManager.nomotionStarted();
                }
            }
        }
    }
}
