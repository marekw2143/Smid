package com.smid.app.gestureDetection;

/**
 * Created by marek on 24.06.16.
 */

import android.content.Context;

import com.smid.app.GD_CONSTS;
import com.smid.app.Helper;
import com.smid.app.changesDetector.MoveDescriber;
import com.smid.app.changesDetector.MoveDescription;
import com.smid.app.changesDetector.model.ActivityAccelerometerData;


/**
 * This class can detect one cycle of motion - nomotion.
 * It stops fetching data after 30 seconds - TODO: implement!
 * Created by marek on 24.06.16.
 */
public class DetectionManager implements INomotionManager {

    GestureMoveCollector gestureMoveCollector;
    Context context;
    Object SyncNomotionStarted = new Object();
    Object syncStart = new Object();
    Object syncEnd = new Object();
    private final long timeCreated;

    private NomotionDetector nomotionDetector;
    int nomotionDetectionAmount = 0;

    MoveDescription moveDescription = null;

    IGestureDetectorReceiver iGestureDetectorReceiver;

    static int number = 0;

    public int dmNumber;

    public DetectionManager(Context ctx, IGestureDetectorReceiver iGestureDetectorReceiver, Counter counter) {
        dmNumber = ++DetectionManager.number;

        this.context = ctx;
        this.iGestureDetectorReceiver = iGestureDetectorReceiver;
        timeCreated = counter.getNextCounter();
    }

    public void startWaitingForChanges() {
        try{
            nomotionDetector = new NomotionDetector(context, this);
            nomotionDetector.startCollectingData();
        } catch (Exception ex) {
            clearDetectors();
            Helper.logException(context, ex);
        }
    }

    @Override
    public void nomotionStarted() {
        try {
            synchronized (SyncNomotionStarted) {
                nomotionDetectionAmount += 1;

                if (nomotionDetectionAmount > 2) {
                    nomotionDetectionAmount = 2;

                    return;
                }

                nomotionDetector.clearNomotionDetectedListener();

                if (nomotionDetectionAmount == 1) { // start measuring
                    onNomotionWhenStarted();
                } else {
                    onNomotionWhenFinished();
                }
            }
        } catch (Exception ex) {
            Helper.logException(context, ex);
        }
    }

    protected void onNomotionWhenStarted () {
        synchronized (syncStart) {
            try{
                iGestureDetectorReceiver.onNomotionStarted(this);
            } catch(Exception ex){
                Helper.logException(context, ex);
            }

            try {
                clearNomotionDetector();

                nomotionDetector = new NomotionDetector(context, this);
                nomotionDetector.startCollectingData();

                gestureMoveCollector =  new GestureMoveCollector(context);
                gestureMoveCollector.startCollectingData();
            } catch (Exception ex){
                clearDetectors();

                Helper.logException(context, ex);
            }
        }
    }

    /**
     * Called when nomotion is detected second time.
     */
    protected void onNomotionWhenFinished() {
        synchronized (syncEnd) {
            try{
                iGestureDetectorReceiver.onNomotionFinished(this);
            } catch(Exception ex){
                Helper.logException(context, ex);
            }

            try {
                boolean gestureSend = false;
                clearNomotionDetector();

                gestureMoveCollector.stopCollectingData();

                try {
                    // Fetch accelerometer data.
                    ActivityAccelerometerData accelerometerData = gestureMoveCollector.getAccelerometerData();

                    // get move description.
                    MoveDescriber describer = new MoveDescriber(accelerometerData);

                    moveDescription = describer.getDescription();
                } catch (Exception ex) {

                    // anything went wrnog - inform receiver anyway that gesture detection is finished.
                    gestureSend = true;
                    try {
                        MoveDescription md = new MoveDescription(GD_CONSTS.EMPTY_MOVE);
                        iGestureDetectorReceiver.onGestureDetected(md, this);
                    } catch(Exception exx) {
                        Helper.logException(context, ex);
                    }
                    Helper.logException(context, ex);
                }

                if(!gestureSend) {
                    try {
                        iGestureDetectorReceiver.onGestureDetected(moveDescription, this);
                    } catch (Exception ex) {
                        Helper.logException(context, ex);
                    }
                }

                clearDetectors();
            } catch (Exception ex) {
                clearDetectors();
                Helper.logException(context, ex);
            }
        }
    }

    protected void clearNomotionDetector () {
        try {
            if (nomotionDetector != null) {
                nomotionDetector.stopCollectingData();

                nomotionDetector.clearNomotionDetectedListener();

                nomotionDetector.unregisterListener();

                nomotionDetector = null;
            }
        } catch (Exception ex){
            Helper.logException(context, ex);
        }
    }

    protected void clearGestureMoveCollector() {
        try{
            if(gestureMoveCollector != null) {
                gestureMoveCollector.stopCollectingData();

                gestureMoveCollector.unregisterListener();
            }
        } catch (Exception ex) {
            Helper.logException(context, ex);
        }

        gestureMoveCollector = null;
    }

    protected void clearDetectors () {
        clearNomotionDetector();

        clearGestureMoveCollector();
    }

    public void stop() {
        clearDetectors();
    }

    @Override
    public String toString() {
        return "DetectionManager" + Integer.toString(dmNumber);
    }

    public long getTimeCreated() {
        return timeCreated;
    }
}
