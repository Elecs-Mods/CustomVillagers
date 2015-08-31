package elec332.customvillagers.json;

import elec332.customvillagers.util.CorrectedTuple;

/**
 * Created by Elec332 on 15-7-2015.
 */
public class RandomisationData {

    public RandomisationData(CorrectedTuple<Integer, Integer> data, Algorithm algorithm){
        this.min = data.getFirst();
        this.max = data.getSecond();
        this.algorithm = algorithm;
    }

    public int min;
    public int max;
    public Algorithm algorithm;


    public CorrectedTuple<Integer, Integer> toTuple(){
        return CorrectedTuple.newTuple(min, max);
    }

}
