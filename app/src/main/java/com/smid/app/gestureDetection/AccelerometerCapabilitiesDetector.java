package com.smid.app.gestureDetection;

import android.content.Context;

import com.smid.app.GD_CONSTS;
import com.smid.app.Helper;

/**
 * Created by marek on 30.06.16.
 */
public class AccelerometerCapabilitiesDetector extends AccelerometerDataListener {
    private long measurementStartTime;
    private long measurementEndTime;
    private int measurementsAmount = 0;
    private IAccelerometerCapabilitiesListener listener;

    public AccelerometerCapabilitiesDetector(Context ctx, IAccelerometerCapabilitiesListener listener) {
        super(ctx);

        this.listener = listener;

        if(this.senAccelerometer == null) {
            listener.capabilitiesMeasured(MeasurementStatus.NOT_SUFFICIENT);
        }
    }

    @Override
    protected void onStartCollectingData() {
        this.measurementStartTime = Helper.getTime();
    }

    @Override
    protected void onStopCollectingData() {

    }

    @Override
    protected void processAccelerometerData(float x, float y, float z, long tickTime) {
        try {
            long actTime = Helper.getTime();

            if (actTime - measurementStartTime > GD_CONSTS.TEST_ACCELEROMETER_TIME_MS) {
                stopCollectingData();
                measurementEndTime = actTime;

                checkCapabilities();
            }

            measurementsAmount++;
        } catch (Exception ex) {
            Helper.logException(ctx, ex);
        }
    }

    private void checkCapabilities() {
        try {
            long diffTime = measurementEndTime - measurementStartTime;

            int minExpectedMeasurementsInDiffTime = (int) (GD_CONSTS.MIN_MEASUREMENTS_PER_SECOND * (diffTime / 1000));

            if (measurementsAmount >= minExpectedMeasurementsInDiffTime) {
                listener.capabilitiesMeasured(MeasurementStatus.SUFFICIENT);
            } else {
                listener.capabilitiesMeasured(MeasurementStatus.NOT_SUFFICIENT);
            }
        } catch (Exception ex) {
            Helper.logException(ctx, ex);
        }
    }
}
