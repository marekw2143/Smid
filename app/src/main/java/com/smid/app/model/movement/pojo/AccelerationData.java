package com.smid.app.model.movement.pojo;

/**
 * Created by marek on 05.05.16.
 */
public class AccelerationData extends BaseMoveDataPOJO{

    private float accX;

    private float accY;

    private float accZ;

    public AccelerationData(double accX, double accY, float accZ, long eventTime) {
        this.accX = (float) accX;
        this.accY = (float) accY;
        this.accZ = accZ;
        this.eventTime = eventTime;
    }

    public float getAccX() {
        return accX;
    }

    public float getAccY() {
        return accY;
    }

    public float getAccZ() {
        return accZ;
    }
}
