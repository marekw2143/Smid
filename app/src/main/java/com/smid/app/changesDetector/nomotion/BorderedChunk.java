package com.smid.app.changesDetector.nomotion;

/**
 * Created by marek on 13.06.16.
 */
public class BorderedChunk extends Chunk {
    private long timeValidFromInclusive;
    private long timeValidToExclusive;

    double absXsSum = 0.0;
    double absYsSum = 0.0;

    public BorderedChunk(long timeValidFrom, long timeValidTo) {

        this.timeValidFromInclusive = timeValidFrom;
        this.timeValidToExclusive = timeValidTo;
    }

    public long measurementsAmount() {
        return xs.size();
    }

    @Override
    public void addMeasurement(long time, Double ax, Double ay) {
        super.addMeasurement(time, ax, ay);

        absXsSum += Math.abs(ax);
        absYsSum += Math.abs(ay);
    }

    public Double getAbsAvgX() {
        Double ret =  absXsSum / measurementsAmount();

        return ret;
    }

    public Double getAbsAvgY() {
        return absYsSum / measurementsAmount();
    }

    public boolean canAddMeasurement(long time) {
        return time >= timeValidFromInclusive && time < timeValidToExclusive;
    }
}
