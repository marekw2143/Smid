package com.smid.app.changesDetector;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.model.semanticFragments.SemanticFragmentBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates move description on the basis of fragments from two axis.
 */
public class AxisMerger {
    static final String moveOnlyAxisX1 = GD_CONSTS.HORIZONTAL_LEFT + GD_CONSTS.VERTICAL_SIMPLE;
    static final String moveOnlyAxisX2 = GD_CONSTS.HORIZONTAL_RIGHT+ GD_CONSTS.VERTICAL_SIMPLE;
    static final String moveOnlyAxisY1 = GD_CONSTS.HORIZONTAL_SIMPLE + GD_CONSTS.VERTICAL_UP;
    static final String moveOnlyAxisY2 = GD_CONSTS.HORIZONTAL_SIMPLE + GD_CONSTS.VERTICAL_DOWN;

    static final String moveBothAxes1 = GD_CONSTS.HORIZONTAL_LEFT + GD_CONSTS.VERTICAL_DOWN;
    static final String moveBothAxes2 = GD_CONSTS.HORIZONTAL_LEFT + GD_CONSTS.VERTICAL_UP;
    static final String moveBothAxes3 = GD_CONSTS.HORIZONTAL_RIGHT + GD_CONSTS.VERTICAL_DOWN;
    static final String moveBothAxes4 = GD_CONSTS.HORIZONTAL_RIGHT + GD_CONSTS.VERTICAL_UP;


    boolean areTimeCorelated(long timeA, long timeB){
        return Math.abs(timeA - timeB) < GD_CONSTS.TIME_CORELATED_MAX_DIFF;
    }

    public String merge(ArrayList<SemanticFragmentBase> fragmentsX, ArrayList<SemanticFragmentBase> fragmentsY) {
        int idxX = 0;
        int idxY = 0;

        SemanticFragmentBase fragmentY;
        SemanticFragmentBase fragmentX;

        List<String> ret = new ArrayList<String>(fragmentsX.size() * 3);

        while(true)
        {
            fragmentX = fragmentsX.get(idxX);
            fragmentY = fragmentsY.get(idxY);

            ret.add(getDescription(fragmentX, fragmentY));

            if(idxX == fragmentsX.size() - 1 && idxY == fragmentsY.size() -1 ) { // stop if processed both lists
                break;
            }

            if (idxX < fragmentsX.size() - 1 && idxY == fragmentsY.size() - 1) {// last Y and have some on X - increase x
                idxX += 1;
            } else if(idxX == fragmentsX.size() - 1 && idxY < fragmentsY.size() - 1 ){// X processed, have some on y
                idxY += 1;
            } else { // further are both times:

                if(ret.size() > 0){
                    String lastEntry = ret.get(ret.size() -1);

                    if(moveInOnlyXAsxis(lastEntry)) {
                        idxX += 1;
                        continue;
                    } else if (moveInOnlyYAxis(lastEntry)){
                        idxY += 1;
                        continue;
                    } else if (moveInBothAxes(lastEntry)){
                        idxX += 1;
                        idxY += 1;
                        continue;
                    }
                }

                // decide which will be better.
                long tX = fragmentsX.get(idxX + 1).getStartTime();
                long tY = fragmentsY.get(idxY + 1).getStartTime();

                if(areTimeCorelated(tX, tY)) {
                    idxX += 1;
                    idxY += 1;
                } else if (tX < tY){
                    idxX += 1;
                } else {
                    idxY += 1;
                }
            }
        }

        StringBuilder sb = new StringBuilder(ret.size() * 2);
        for(int i=0;i<ret.size();i++){
            sb.append(ret.get(i));
        }

        return sb.toString();
    }

    private String getDescription(SemanticFragmentBase fragmentX, SemanticFragmentBase fragmentY) {
        return fragmentX.getName() + fragmentY.getName();
    }

    protected boolean moveInOnlyXAsxis(String movePartDescription) {
        return movePartDescription.equals(moveOnlyAxisX1)
                || movePartDescription.equals(moveOnlyAxisX2);
    }

    protected boolean moveInOnlyYAxis(String movePartDescription) {
        return movePartDescription.equals(moveOnlyAxisY1)
                || movePartDescription.equals(moveOnlyAxisY2);
    }

    protected boolean moveInBothAxes(String movePartDescription) {
        return movePartDescription.equals(moveBothAxes1)
                || movePartDescription.equals(moveBothAxes2)
                || movePartDescription.equals(moveBothAxes3)
                || movePartDescription.equals(moveBothAxes4);
    }
}

