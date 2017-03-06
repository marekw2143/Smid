package com.smid.app.model.movement.pojo;

/**
 * Created by marek on 05.05.16.
 */
public class GyroData extends BaseMoveDataPOJO {
    private final float angle;

    protected float x;

    protected float y;

    protected float z;

    public GyroData(float x, float y, float z, float angle, long eventTime){
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
        this.eventTime = eventTime;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public long getEventTime() {
        return super.getEventTime();
    }

    public float getAngle() {
        return angle;
    }

    public String toString() {
        GyroData ad = this;

        return ad.getEventTime() + "," + ad.getX() + "," + ad.getY() + "," + ad.getZ() + "," + ad.getAngle() + "\n";
    }
}
