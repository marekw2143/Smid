package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.List;

/**
 * Created by marek on 13.06.16.
 */
public class Simple extends FragmentBase {

    public Simple(long startTime, FragmentBase previous) {
        super(startTime);
        this.previous = previous;
        fragmentType = FragmentType.Simple_Fragment_FT;
    }

    @Override
    protected FragmentBase getNextFragment(int j, List<DiscretePoint> points) {
        DiscretePoint point = points.get(j);

        switch(point.getValue()) {
            case 1:
                return (new StartPeakA(point.getTime())).parse(j, points);
            case -1:
                return (new StartPeakB(point.getTime())).parse(j, points);
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public FragmentType getFragmentType() {
        return FragmentType.Simple_Fragment_FT;
    }

    @Override
    public byte getIndicatedValue() {
        return 0;
    }

    @Override
    public long getMaxNoiseLength() {
        return GD_CONSTS.MAX_NOISE_SIMPLE;
    }

    @Override
    public long getMaxTimeLength() {
        return GD_CONSTS.MAX_LENGTH_SIMPLE;
    } // may be infiniite
}
