package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.List;

/**
 * Created by marek on 13.06.16.
 */
public class StartPeakB extends AbstractPeak {

    public StartPeakB(long startTime) {
        super( startTime);
        fragmentType = FragmentType.Start_PeakB_FT;
    }

    @Override
    protected IFragment getNextFragment(int j, List<DiscretePoint> points) {
        return new SimpleX_PeakB(points.get(j).getTime(), this).parse(j, points);
    }

    @Override
    public FragmentType getFragmentType() {
        return FragmentType.Start_PeakB_FT;
    }

    @Override
    public byte getIndicatedValue() {
        return -1;
    }


}
