package com.smid.app.changesDetector;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.model.semanticFragments.SemanticFragmentBase;

import java.util.List;

/**
 * Created by marek on 25.06.16.
 */
public class MoveDescription {
    public String move;

    public MoveDescription(String moveDescription) {
        move = moveDescription;
    }

    public List<SemanticFragmentBase> semanticFragmentsX;
    public List<SemanticFragmentBase> semanticFragmentsY;

    public String getMove() {
        return move;
    }

    public boolean isValid() {
        if(getMove() == null){
            return false;
        }

        if(getMove().equals("")){
            return false;
        }
        if(getMove().equals(GD_CONSTS.EMPTY_MOVE)) {
            return false;
        }

        if(!validFragments(semanticFragmentsX)){
            return false;
        }

        if(!validFragments(semanticFragmentsY)){
            return false;
        }

        return true;
    }

    protected boolean validFragments(List<SemanticFragmentBase> fragments) {
        if(fragments != null){
            for(int i=0;i<fragments.size(); i++){
                SemanticFragmentBase fragment = fragments.get(i);

                String name = fragment.getName();
                long duration = fragment.getDuration();

                switch (name) {
                    case GD_CONSTS.HORIZONTAL_SIMPLE:
                    case GD_CONSTS.VERTICAL_SIMPLE:
                        if(duration < GD_CONSTS.MIN_SIMPLE_DURATION) {
                            return false;
                        }
                        break;
                    case GD_CONSTS.HORIZONTAL_LEFT:
                    case GD_CONSTS.HORIZONTAL_RIGHT:
                    case GD_CONSTS.VERTICAL_DOWN:
                    case GD_CONSTS.VERTICAL_UP:
                        if(duration < GD_CONSTS.MIN_NON_SIMPLE_DURATION){
                            return false;
                        }
                    default:
                        break;

                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return getMove();
    }
}
