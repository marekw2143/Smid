package com.smid.app.changesDetector;

import com.smid.app.changesDetector.model.semanticFragments.SemanticFragmentBase;
import com.smid.app.changesDetector.model.semanticFragments.SemanticPeak;
import com.smid.app.changesDetector.model.semanticFragments.SemanticSimple;
import com.smid.app.changesDetector.model.simpleFragments.FragmentType;
import com.smid.app.changesDetector.model.simpleFragments.IFragment;

import java.util.ArrayList;

/**
 * Created by marek on 15.06.16.
 */
public class SemanticFragmentGenerator {
    private IFragment firstFragment;
    private final String simpleName;
    private final String peakAname;
    private final String peakBname;

    public SemanticFragmentGenerator(IFragment firstFragment, String simpleName, String peakAname, String peakBname) {
        this.firstFragment = firstFragment;
        this.simpleName = simpleName;
        this.peakAname = peakAname;
        this.peakBname = peakBname;
    }

    public ArrayList<SemanticFragmentBase> parse() {
        ArrayList<SemanticFragmentBase> ret = new ArrayList<>();
        ArrayList<IFragment> fragments = convertToList();
        
        fragments = removeSinglePeakParts(fragments);
        fragments = removeSmallerThanNeighbours(fragments);

        
        IFragment fragment = firstFragment;
        fragment = fragments.get(0);
        
        for(int i=1; i<fragments.size(); i++) {
        	IFragment previousFragment = fragments.get(i - 1);
        	
        	previousFragment.setNextFragment(fragments.get(i));
        }

        while(true) {
            switch(fragment.getFragmentType()){
                case Simple_Fragment_FT:
                    SemanticSimple semanticSimple = parseSimpleX(fragment);

                    ret.add(semanticSimple);

                    fragment = semanticSimple.getNextSimpleFragment();
                    break;
                case Start_PeakA_FT:
                    SemanticPeak semanticPeakA = parsePeakAStart(fragment);

                    if(semanticPeakA == null){
                        return null;
                    }

                    ret.add(semanticPeakA);

                    fragment = semanticPeakA.getNextSimpleFragment();

                    break;
                case Start_PeakB_FT:
                    SemanticPeak semanticPeakB = parsePeakBStart(fragment);

                    if(semanticPeakB == null) {
                        return null;
                    }

                    ret.add(semanticPeakB);

                    fragment = semanticPeakB.getNextSimpleFragment();

                    break;
                case End_Of_Fragments_FT:
                    return ret;
                case Failure_Fragment_FT:
                    return null;
                default:
                    return null;
            }
        }
    }

    private ArrayList<IFragment> removeSmallerThanNeighbours(ArrayList<IFragment> fragments) {
    	ArrayList<IFragment> ret = new ArrayList<>();
    	
    	int i=0;
    	
    	while(i < fragments.size() ) {
    		IFragment fr1 = fragments.get(i);
    		
    		if(fr1.getFragmentType () == FragmentType.End_Of_Fragments_FT) {
    			ret.add(fr1);
    			return ret;
    		}
    		if(fr1.getFragmentType() == FragmentType.End_PeakA_FT || fr1.getFragmentType() == FragmentType.End_PeakB_FT) {
    			int simpleIdx = i + 1;
    			int probablyNextStartIdx = i+2;
    			
    			IFragment nextSimple = fragments.get(simpleIdx);
    			IFragment probablyNextStart = fragments.get(probablyNextStartIdx);
    			
    			if(probablyNextStart.getFragmentType() == FragmentType.Start_PeakA_FT || probablyNextStart.getFragmentType() == FragmentType.Start_PeakB_FT) {
    				// if time space between act and next peak is small, choose the bigger
    				// 200 ms difference
    				if(twoFragmentsOneIsNoise(fr1, probablyNextStart)) { 
    					if(fr1.getFragmentDuration() < probablyNextStart.getFragmentDuration()) { // remove fr1 part
    						ret.remove(ret.size() - 1); // remove simple
    						ret.remove(ret.size() - 1); // remove start, currently last is simple
    						ret.get(ret.size() - 1).setEndTime(probablyNextStart.getStartTime()); // set previous simple end time to act start
    						
    						ret.add(probablyNextStart);    	
    						i = probablyNextStartIdx + 1;
    					} else { // remove next part
    						ret.add(fr1);
    						ret.add(nextSimple);
    						
    						i++; // simple;
    						i++; // start
    						i++; // simpleX
    						i++; // end
    						i++;// simple
    						
    						IFragment nextNextSimple = fragments.get(i);
    						nextSimple.setEndTime(nextNextSimple.getEndTime());
    						
    						
    						i++; // next after next simple;
    						
    					}
    					
    					continue;
    				}
    				
    			}
    		}

    	    	
			ret.add(fr1);
			i++;				    								   
    	}
	
		return ret;
	}

	/**
     * If after SimplePeakA/B there's Simple, just remove SimplePeakA/B
     * @param fragments
     * @return
     */
    private ArrayList<IFragment> removeSinglePeakParts(ArrayList<IFragment> fragments) {
    	ArrayList<IFragment> ret = new ArrayList<>();
    	
    	int i=0;
    	
    	while(i < fragments.size() ) {
    		IFragment fr1 = fragments.get(i);

    		if(fr1.getFragmentType () == FragmentType.End_Of_Fragments_FT) {
    			// if last is not simple, remove all previous until simple and set last simple's end time to (fr1 -1) time.
    			IFragment previous = ret.get(ret.size() -1);
    			
    			int index = ret.size() -1;
    			
    			while(ret.get(index).getFragmentType() != FragmentType.Simple_Fragment_FT) {
    				ret.remove(index);
    				index--;
    			}
    			
    			ret.get(index).setEndTime(previous.getEndTime());;

    			ret.add(fr1);
    			return ret;
    		}
    		
			int j = i + 1;			
			IFragment fr2 = fragments.get(j);
    		
//    		if(fr1.getFragmentType() == FragmentType.Start_PeakA_FT || fr1.getFragmentType() == FragmentType.Start_PeakB_FT) {    		
//    			if(fr2.getFragmentType() == FragmentType.End_Of_Fragments_FT) {
//    				// don't add fr1, just set fr1 -1 end time as end time of fr1
//    				// and return result as fr2 is end fragment.
//    				
//    				ret.get(ret.size() -1).setEndTime(fr1.getEndTime());
//    				
//    				return ret;
//    			} 
//    		}
    		
    		if(fr1.getFragmentType() == FragmentType.SimpleX_PeakA_FT || fr1.getFragmentType() == FragmentType.SimpleX_PeakB_Ft) {  			
				// if next fragment is simple, just ommit fr1 and previous, and set proper values of simple    				
				if(fr2.getFragmentType() == FragmentType.Simple_Fragment_FT) {
					IFragment previous = ret.get(ret.size() - 1); // StartPeakA/B
					IFragment previousPrevious = ret.get(ret.size() - 2); // Simple
					
					previousPrevious.setEndTime(fr2.getEndTime());
					
					ret.remove(ret.size() -1);// remove StartPeakA/B
					
					i = j + 1; // go to next fragment after simple
					
					continue;
				} 			
    		}
    		
			ret.add(fr1);
			i++;
				    								    	
    	}
	
		return ret;
	}

	private SemanticPeak parsePeakAStart(IFragment fragment) {
        return parsePeak(fragment, FragmentType.SimpleX_PeakA_FT, FragmentType.End_PeakB_FT, peakAname);
    }

    private SemanticPeak parsePeakBStart(IFragment fragment) {
        return parsePeak(fragment, FragmentType.SimpleX_PeakB_Ft, FragmentType.End_PeakA_FT, peakBname);
    }

    private SemanticPeak parsePeak(IFragment fragment, FragmentType simpleXType, FragmentType endPeakType, String parsedPeakName) {
        IFragment nextFragment = null;

        long startTime = fragment.getStartTime();

        long endTime;

        IFragment probablySimpleX = fragment.getNextFragment();

        if (probablySimpleX.getFragmentType() == simpleXType) {
            IFragment probablyPeakBEnd = probablySimpleX.getNextFragment();

            nextFragment = probablySimpleX.getNextFragment();

            if (probablyPeakBEnd.getFragmentType() == endPeakType) {
                nextFragment = probablyPeakBEnd.getNextFragment();

                endTime = probablyPeakBEnd.getEndTime();
            } else {
                return null;
            }

        } else if (probablySimpleX.getFragmentType() == endPeakType) {
            endTime = probablySimpleX.getEndTime();
        } else {
            return null;
        }

        SemanticPeak semanticPeak = new SemanticPeak(startTime, endTime, parsedPeakName);

        semanticPeak.setNextSimpleFragment(nextFragment);

        return semanticPeak;
    }

    protected SemanticSimple parseSimpleX(IFragment fragment) {
        SemanticSimple ret =  new SemanticSimple(fragment.getStartTime(), fragment.getEndTime(), simpleName);

        ret.setNextSimpleFragment(fragment.getNextFragment());

        return ret;
    }

    private ArrayList<IFragment> convertToList(){
        ArrayList<IFragment> ret = new ArrayList<>();
        IFragment fragment = firstFragment;

        while(fragment.getFragmentType() != FragmentType.End_Of_Fragments_FT) {
            ret.add(fragment);

            fragment = fragment.getNextFragment();
        }
        
        ret.add(fragment);
        

        return ret;
    }
    
    boolean twoFragmentsOneIsNoise(IFragment first, IFragment second){
    	boolean timeDelayCondition =second.getStartTime() - first.getEndTime() <= 150; 
    	  	

    	double avgFst = first.getAverageFragmentModSize();
    	double avgSnd = second.getAverageFragmentModSize();
    	
    	Double ratio =avgFst / avgSnd;     	
    	if(avgSnd > avgFst) {
    		ratio = avgSnd / avgFst;
    	}
    	
    	boolean averageValueSizeCOndition = ratio >= 2.0; 
    	
    	if(averageValueSizeCOndition) {
    		int j = 412 * 0xFF3;
    		
    		timeDelayCondition = !!timeDelayCondition && j > 0;
    	}
    	
    	return timeDelayCondition && averageValueSizeCOndition;
    }
}
