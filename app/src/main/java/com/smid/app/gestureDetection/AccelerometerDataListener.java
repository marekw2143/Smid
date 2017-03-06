package com.smid.app.gestureDetection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.smid.app.Helper;

/**
 * Created by marek on 15.06.16.
 */
public abstract class AccelerometerDataListener implements SensorEventListener {

    private int accelerometerType;
    protected SensorManager sensorManager;
    protected Sensor senAccelerometer;

    /* time related */
    private long lastMeasuredCollectionTime;
    long startTime = -1;
    private final Object syncToken = new Object();
    protected Context ctx;
    private boolean canCollect = false;
    private boolean stopListening = false;

    public AccelerometerDataListener(Context ctx) {
        try {
            this.ctx = ctx;
            accelerometerType = Sensor.TYPE_LINEAR_ACCELERATION;
            sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
            senAccelerometer = sensorManager.getDefaultSensor(accelerometerType);
            sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST); //TODO: after testing change to SENSOR_DELAY_FASTEST
        } catch (Exception ex) {
            Helper.logException(ctx, ex);
        }
    }

    public void startCollectingData() {
        synchronized(syncToken) {
            onStartCollectingData();

            canCollect = true;
        }
    }

    protected abstract void onStartCollectingData();
    protected abstract void onStopCollectingData();

    public void stopCollectingData() {
        synchronized (syncToken){
            onStopCollectingData();
            canCollect = false;
        }
    }

    protected abstract void processAccelerometerData(float x, float y, float z, long tickTime);

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (syncToken) {
            if (!canCollect) {
                return;
            }

            Sensor sensor = event.sensor;

            if (sensor.getType() == accelerometerType) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long tickTime = Helper.getTime();

                processAccelerometerData(x, y, z, tickTime);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void unregisterListener () {
        if(sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        sensorManager = null;
    }

    public void stopListening () {
        this.stopListening = true;
    }
}




