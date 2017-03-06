package com.smid.app.changesDetector;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.model.ActivityAccelerometerData;
import com.smid.app.changesDetector.model.IAccelerometerData;
import com.smid.app.changesDetector.model.Point;
import com.smid.app.changesDetector.model.semanticFragments.SemanticFragmentBase;
import com.smid.app.changesDetector.model.simpleFragments.IFragment;

import java.util.ArrayList;

/**
 * Created by marek on 15.06.16.
 */
public class MoveDescriber {
    private IAccelerometerData averagedAccelerometerData;

    public MoveDescriber(ActivityAccelerometerData accelerometerData) {
        DataPreparer dataPreparer = new DataPreparer();
        averagedAccelerometerData = dataPreparer.getAveragedAccelerometerData(accelerometerData);

        for(int i = 0; i< getAveragedAccelerometerData().getXsPoints().size(); i++) {
            Point p = getAveragedAccelerometerData().getXsPoints().get(i);

            p.setTime(p.getTime() - accelerometerData.getStarTime());
       }

        for(int i = 0; i< getAveragedAccelerometerData().getYsPoints().size(); i++) {
            Point p = getAveragedAccelerometerData().getYsPoints().get(i);

            p.setTime(p.getTime() - accelerometerData.getStarTime());
        }
    }

    public MoveDescription getDescription() {
        ArrayList<Point> xPoints = getAveragedAccelerometerData().getXsPoints();
        ArrayList<Point> yPoints = getAveragedAccelerometerData().getYsPoints();

        IFragment xStartFragment = new SimpleFragmentGenerator(xPoints).analyze();
        IFragment yStartFragment = new SimpleFragmentGenerator(yPoints).analyze();

        SemanticFragmentGenerator xSemanticFragmentGenerator = new SemanticFragmentGenerator(xStartFragment, GD_CONSTS.HORIZONTAL_SIMPLE, GD_CONSTS.HORIZONTAL_RIGHT, GD_CONSTS.HORIZONTAL_LEFT);
        ArrayList<SemanticFragmentBase> xSemFragments = xSemanticFragmentGenerator.parse();

        SemanticFragmentGenerator ySemanticFragmentGenerator = new SemanticFragmentGenerator(yStartFragment, GD_CONSTS.VERTICAL_SIMPLE, GD_CONSTS.VERTICAL_UP, GD_CONSTS.VERTICAL_DOWN);
        ArrayList<SemanticFragmentBase> ySemFragments = ySemanticFragmentGenerator.parse();

        AxisMerger axisMerger = new AxisMerger();
        String move = axisMerger.merge(xSemFragments, ySemFragments);

        MoveDescription description = new MoveDescription(move);

        description.semanticFragmentsX = xSemFragments;
        description.semanticFragmentsY = ySemFragments;

        return description;
    }

    /**
     * Important - this is for tests only!!!
     * @return
     */
    public IAccelerometerData getAveragedAccelerometerData() {
        return averagedAccelerometerData;
    }
}
