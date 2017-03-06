package com.smid.app.model.movement;

import android.util.Pair;

import com.smid.app.model.ModelBase;

import java.util.List;

/**
 * Created by marek on 22.04.16.
 */
public class AccData extends ModelBase {

    private String fname;
    private List<Pair<Long, Float>> x;
    private List<Pair<Long, Float>> y;
    private List<Pair<Long, Float>> z;
    protected long startTime;

    public AccData(String fname, List<Pair<Long, Float>> x, List<Pair<Long, Float>> y, List<Pair<Long, Float>> z, long startTIme) {

        this.fname = fname;
        this.x = x;
        this.y = y;
        this.z = z;
        this.startTime = startTIme;
    }


    public List<Pair<Long, Float>> getX() {
        return x;
    }

    public List<Pair<Long, Float>> getY() {
        return y;
    }

    public List<Pair<Long, Float>> getZ() {
        return z;
    }

    public long getStartTime() {
        return startTime;
    }
}
