package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.GD_CONSTS;

/**
 * Created by marek on 14.06.16.
 */
public abstract  class AbstractPeak extends FragmentBase {
    public AbstractPeak(long startTime) {
        super(startTime);
    }

    @Override
    public long getMaxNoiseLength() {
        return GD_CONSTS.MAX_NOISE_PEAK;
    }

    @Override
    public long getMaxTimeLength() {
        return GD_CONSTS.MAX_LENGTH_PEAK;

    }
}
