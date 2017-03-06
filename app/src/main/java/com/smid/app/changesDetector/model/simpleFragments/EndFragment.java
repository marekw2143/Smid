package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.List;

/**
 * Created by marek on 14.06.16.
 */
public class EndFragment implements IFragment {

    public EndFragment(){

    }
    @Override
    public long getEndTime() {
        return 0;
    }

    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public FragmentType getFragmentType() {
        return FragmentType.End_Of_Fragments_FT;
    }

    @Override
    public void setEndTime(long time) {

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

    @Override
    public IFragment getNextFragment() {
        return null;
    }

    @Override
    public FragmentBase parse(int startIndex, List<DiscretePoint> points) {
        return null;
    }
	@Override
	public void setStartTime(long time) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setNextFragment(IFragment fragment) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Double getAverageFragmentModSize() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getFragmentDuration() {
		// TODO Auto-generated method stub
		return 0;
	}
}
