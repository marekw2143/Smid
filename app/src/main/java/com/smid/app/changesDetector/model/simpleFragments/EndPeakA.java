package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.List;

/**
 * Created by marek on 14.06.16.
 */
public class EndPeakA extends AbstractPeak {
    public EndPeakA(long startTime) {
        super(startTime);
        fragmentType = FragmentType.End_PeakA_FT;
    }

    @Override
    protected IFragment getNextFragment(int j, List<DiscretePoint> points) {
        return new Simple(points.get(j).getTime(), this).parse(j, points);
    }

    @Override
    public FragmentType getFragmentType() {
        return FragmentType.End_PeakA_FT;
    }

    @Override
    public byte getIndicatedValue() {
        return 1;
    }
}
