package com.smid.app.changesDetector.model.simpleFragments;

import com.smid.app.changesDetector.model.DiscretePoint;

import java.util.List;

/**
 * Created by marek on 13.06.16.
 */
public interface IFragment {
    /**
     * Gets end time of the state.
     * @return
     */
    long getEndTime();

    /**
     * Gets start time of the state.
     * @return
     */
    long getStartTime();
    
    void setStartTime(long time);

    /**
     * Returns fragment type.
     * @return
     */
    FragmentType getFragmentType();

    /**
     * Sets state's end time.
     * @param time
     */
    void setEndTime(long time);

    /**
     * Returns value on the signal which indicates such state.
     * @return
     */
    byte getIndicatedValue();

    /**
     * maximal length of noise in such state.
     * @return
     */
    long getMaxNoiseLength();

    /**
     * Maximal time the state can last.
     * @return
     */
    long getMaxTimeLength();

    void setNextFragment(IFragment fragment);
    
    IFragment getNextFragment();
    
    Double getAverageFragmentModSize();
    
    long getFragmentDuration();

    FragmentBase parse(int startIndex, List<DiscretePoint> points);
}
