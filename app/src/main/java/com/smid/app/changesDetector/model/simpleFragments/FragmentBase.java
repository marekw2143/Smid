package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marek on 13.06.16.
 */
public abstract class FragmentBase implements IFragment {
    private long startTime;
    private long endTime;
    private IFragment nextFragment = null;
    protected FragmentType fragmentType;
	protected FragmentBase previous;

    public FragmentBase(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public FragmentBase parse(int i, List<DiscretePoint> points) {
    	this.thisPoints = points;
        int startIdx = i;

        while(true) {
            int j = i + 1;

            if (j >= points.size()) {
//                if (this.getFragmentType() == FragmentType.Simple_Fragment_FT) { // hack, better code this!
                    this.setEndTime(points.get(points.size() - 1).getTime());
//                }

                this.nextFragment = new EndFragment();
                return this;
            }

            while (j < points.size() && points.get(j).getValue() == getIndicatedValue()) {
                if (points.get(j).getTime() - getStartTime() > getMaxTimeLength()) {
                	
                	if(getFragmentType() == FragmentType.SimpleX_PeakA_FT || getFragmentType() == FragmentType.SimpleX_PeakB_Ft) {
                		setEndTime(-1);
                        
                		this.nextFragment = new Simple(getStartTime(), this).parse(startIdx, points);
                        
                        return this;
                	}
                	
                	@SuppressWarnings("unused")
					double previousArea = previous.getArea(points);
                	
                    this.nextFragment = new FailureFragment(0);

//                    setEndTime(startTime);
//
//                    this.nextFragment = getNextFragment(startIdx, points);

                    return this;
                }

                j++;
            }

            // now j indicates first invalid value

            int newIdx = checkNoiseStartsAt(j, points);

            if (newIdx != -1) { // it's only noise - continue working
                i = newIdx;
                continue;
            } else { // set end time as last valid point.
                setEndTime(points.get(j - 1).getTime());


                if (j >= points.size()) {
                    this.nextFragment = new EndFragment();
                    return this;
                }
                this.nextFragment = getNextFragmentWithThis(j, points, this); // set next fragment!!
                return this;
            }
        }
    }

    protected abstract IFragment  getNextFragment(int j, List<DiscretePoint> points);

    protected IFragment getNextFragmentWithThis(int j, List<DiscretePoint> points, FragmentBase fb) {
    	this.previous = fb;
    	return getNextFragment(j, points);
    }
        
	// j - index of the point where noise starts.
    private int checkNoiseStartsAt(int j, List<DiscretePoint> points) {
        if (j >= points.size()) { // if checking at end of signal, indicate that it's no noise
            return points.size();
        }

        long startTime = points.get(j).getTime(); // start time of the noise.
        int startIdx = j;

        byte indicatedValue = getIndicatedValue();
        while (j < points.size() && points.get(j).getValue() != indicatedValue) {
            j++;
        }

        if(j >= points.size()) {
            j = points.size() - 1;
        }

        long diffTime = points.get(j).getTime() - startTime;


        Double avg_value = 0.0;

        int i = startIdx;
        for (i = startIdx; i < j; i++) {
            avg_value += points.get(i).getOrigValue();
        }

        avg_value /= (i - startIdx);

        Double integralDiff = Math.abs(avg_value * diffTime);


        if(j - startIdx == 1) {
            return j - 1;
        }

        if(getFragmentType() == FragmentType.Simple_Fragment_FT) {
                if(diffTime >= 250) {
                    if(integralDiff >= 200){
                        return -1; // new chunk !
                    }
            } else if (diffTime >= 200) {
                if(integralDiff >= 320) {
                    return -1;
                }
            }

            if (integralDiff< 300) {
                return j - 1; // it's noise
            } else {
                return -1;// -1 new chunk should start
            }
        } else if(getFragmentType() == FragmentType.SimpleX_PeakA_FT || getFragmentType() == FragmentType.SimpleX_PeakB_Ft) {
            if(integralDiff  < 150) {
                return j -1; // noise
            }
            else {
                return -1;// new chunk should start
            }
        } else {
            if (diffTime < getMaxNoiseLength()) {
                return j - 1;// index of last invalid value
            } else {
                return -1; // it's not noise
            }
        }
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public abstract FragmentType getFragmentType();

    @Override
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    @Override
    public void setNextFragment(IFragment fragment) {
    	this.nextFragment = fragment;    	
    }
    
    @Override    
    public void setStartTime(long time) {
    	this.startTime = time;    	
    }
    
    @Override
    public long getFragmentDuration() {
    	return getEndTime() - getStartTime();
    }

    @Override
    public abstract byte getIndicatedValue();

    @Override
    public IFragment getNextFragment() {
        return nextFragment;
    }
    
    public Double getArea(List<DiscretePoint> points) {
    	int indexFirstPoint = getPointIndex(points, getStartTime());
    	int indexLastPoint = getPointIndex(points, getEndTime());
    	double ret = 0.0;
    	
    	for(int i=indexFirstPoint; i<indexLastPoint ;i++) {
    		int j = i+1;
    		
    		double averageValue = (points.get(i).getOrigValue() + points.get(j).getOrigValue()) / 2;
    		double added = averageValue  * (points.get(j).getTime() - points.get(i).getTime());
    				
    		ret += added;
    	}
    	
    	return ret;
    }
    
    protected int getPointIndex(List<DiscretePoint> points, long time) {
    	for(int i=0;i<points.size();i++) {
    		if(points.get(i).getTime() >= time) {
    			return i;
    		}
    	}
    	
    	return -1;
    }
    
    @Override
    public Double getAverageFragmentModSize() {
    	int indexFirstPoint = getPointIndex(thisPoints, getStartTime());
    	int indexLastPoint = getPointIndex(thisPoints, getEndTime());
    	
    	double totalPointsSum = 0.0;
    	for(int i=indexFirstPoint; i<indexLastPoint ;i++) {
    		
    		totalPointsSum += thisPoints.get(i).getOrigValue();    				
    	}
    	
    	return Math.abs(totalPointsSum  / (indexLastPoint + 1 - indexFirstPoint));
    }
    
    List<DiscretePoint> thisPoints;
}
