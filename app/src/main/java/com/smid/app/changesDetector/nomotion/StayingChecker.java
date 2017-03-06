package com.smid.app.changesDetector.nomotion;

import com.smid.app.GD_CONSTS;

import java.util.ArrayList;

/**
 * Created by marek on 13.06.16.
 */
public class StayingChecker {
    private final Double generalThreshold;

    private int minElementsSize;

    ArrayList<BorderedChunk> list;

    int pointer = -1;

    public StayingChecker(long ticks, long window_size, Double threshold) {
        generalThreshold = threshold;

        minElementsSize = (int) (ticks / window_size) - 1;

        list = new ArrayList<>(minElementsSize + 1);
    }

    public void addMeasurement(long time, double ax, double ay) {
        BorderedChunk chunkToAdd;

        if(pointer >= 0) {
            chunkToAdd = list.get(pointer);
        } else {
            chunkToAdd = new BorderedChunk(time, time + GD_CONSTS.NOMOTION_TIME_WINDOW_SIZE);
            list.add(chunkToAdd);
            pointer += 1;
        }

        if(!chunkToAdd.canAddMeasurement(time)) {
            chunkToAdd = new BorderedChunk(time, time + GD_CONSTS.NOMOTION_TIME_WINDOW_SIZE);

            if(list. size() == minElementsSize + 1) {
                pointer += 1;

                if(pointer >= list.size() ){
                    pointer = 0;
                }

                list.set(pointer, chunkToAdd);
            } else {
                list.add(chunkToAdd);
                pointer += 1;
            }
        }

        chunkToAdd.addMeasurement(time, ax, ay);
    }

    /**
     * Checks if phone is in nomotion.
     * @return
     */
    public boolean isPhoneNomotion() {
        if(list.size() < minElementsSize) {
            return false;
        }

        /* check average values of all buffers but not last */
        boolean ret = true;
        int idx = 0;

        while(idx < list.size()) {
            if(idx == pointer) {
                idx += 1;
                continue;
            }

            BorderedChunk chunk = list.get(idx);

            if(chunk.getAbsAvgX() > generalThreshold || chunk.getAbsAvgY() > generalThreshold) {
                ret = false;
                break;
            }

            idx += 1;
        }

        return ret;
    }
}
