package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.List;

/**
 * Created by marek on 16.06.16.
 */
public class FailureFragment extends FragmentBase {
    public FailureFragment(long startTime) {
        super(startTime);
    }

    @Override
    protected IFragment getNextFragment(int j, List<DiscretePoint> points) {
        return null;
    }

    @Override
    public FragmentType getFragmentType() {
        return FragmentType.Failure_Fragment_FT;
    }

    @Override
    public byte getIndicatedValue() {
        return 0;
    }

    @Override
    public long getMaxNoiseLength() {
        return 0;
    }

    @Override
    public long getMaxTimeLength() {
        return 0;
    }
}
