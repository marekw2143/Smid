package com.smid.app.gestureDetection;

import com.smid.app.changesDetector.MoveDescription;

/**
 * Created by marek on 24.06.16.
 */
public interface IGestureDetectorReceiver {
    void onNomotionStarted(DetectionManager detectionManager);
    void onNomotionFinished(DetectionManager detectionManager);
    void onGestureDetected(MoveDescription moveDescription, DetectionManager detectionManager);
    void detectoTimeoutReached(DetectionManager detectionManager);
}
