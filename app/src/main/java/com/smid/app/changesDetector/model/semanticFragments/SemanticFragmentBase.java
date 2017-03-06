package com.smid.app.changesDetector.model.semanticFragments;

import com.smid.app.changesDetector.model.simpleFragments.IFragment;

/**
 * Created by marek on 15.06.16.
 */
public abstract class SemanticFragmentBase {
    private IFragment nextSimpleFragment;

    private long startTime;

    private long endTime;

    private String name;

    public SemanticFragmentBase(long startTime, long endTime, String name) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }

    public IFragment getNextSimpleFragment() {
        return nextSimpleFragment;
    }

    public void setNextSimpleFragment(IFragment nextSimpleFragment) {
        this.nextSimpleFragment = nextSimpleFragment;
    }

    public long getDuration () {
        return endTime - startTime;
    }
}
