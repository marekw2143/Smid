package com.smid.app.changesDetector;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.model.Point;
import com.smid.app.changesDetector.model.DiscretePoint;
import com.smid.app.changesDetector.model.simpleFragments.FragmentBase;
import com.smid.app.changesDetector.model.simpleFragments.Simple;

import java.util.ArrayList;

/**
 * Created by marek on 14.06.16.
 */
public class SimpleFragmentGenerator {

    private final ArrayList<DiscretePoint> accPoints;

    public SimpleFragmentGenerator(ArrayList<Point> accs) {
        this.accPoints = convertToSimpleForm(accs);
    }

    public FragmentBase analyze() {
        return new Simple(0, null).parse(0, accPoints);
    }

    private ArrayList<DiscretePoint> convertToSimpleForm(ArrayList<Point> accs) {
        ArrayList<DiscretePoint> ret = new ArrayList<>(accs.size());

        for (int i = 0; i < accs.size(); i++) {
            Point p = accs.get(i);

            Byte value = 0;

            if (p.getValue() >= GD_CONSTS.THRESHOLD) {
                value = 1;
            } else if (Math.abs(p.getValue()) < GD_CONSTS.THRESHOLD) {
                value = 0;
            } else {
                value = -1;
            }

            ret.add(new DiscretePoint(p.getTime(), value, p.getValue()));
        }

        return ret;
    }
}