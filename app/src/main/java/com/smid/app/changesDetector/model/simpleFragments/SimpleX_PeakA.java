package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.List;

/**
 * Created by marek on 14.06.16.
 */
public class SimpleX_PeakA extends FragmentBase {
    public SimpleX_PeakA(long startTime) {
        super(startTime);
        fragmentType = FragmentType.SimpleX_PeakA_FT;
    }

    @Override
    protected IFragment getNextFragment(int j, List<DiscretePoint> points) {
        return new EndPeakB(points.get(j).getTime()).parse(j, points);
    }

    @Override
    public FragmentType getFragmentType() {
        return FragmentType.SimpleX_PeakA_FT;
    }

    @Override
    public byte getIndicatedValue() {
        return 0;
    }

    @Override
    public long getMaxNoiseLength() {
        return GD_CONSTS.MAX_NOISE_SIMPLE_X;
    }

    @Override
    public long getMaxTimeLength() {
        return GD_CONSTS.MAX_LENGTH_SIMPLE_X;
    }
}
