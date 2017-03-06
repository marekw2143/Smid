package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.List;

/**
 * Created by marek on 13.06.16.
 */
public class StartPeakA extends AbstractPeak {
    public StartPeakA(long startTime) {
        super(startTime);
        fragmentType = FragmentType.Start_PeakA_FT;
    }

    @Override
    protected IFragment getNextFragment(int j, List<DiscretePoint> points) {
        return new SimpleX_PeakA(points.get(j).getTime()).parse(j, points);
    }

    @Override
    public FragmentType getFragmentType() {
        return FragmentType.Start_PeakA_FT;
    }

    @Override
    public byte getIndicatedValue() {
        return 1;
    }


}
