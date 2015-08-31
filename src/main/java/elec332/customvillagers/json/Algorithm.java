package elec332.customvillagers.json;

import elec332.customvillagers.CustomVillagers;
import elec332.customvillagers.util.CorrectedTuple;

import java.util.Random;

/**
 * Created by Elec332 on 15-7-2015.
 */
public enum Algorithm {
    NORMAl, BLACKSMITH;

    public int calculate(CorrectedTuple<Integer, Integer> tuple, TradeSlot type){
        int i = calculateFromTuple(tuple);
        switch (this){
            case NORMAl:
                return i;
            case BLACKSMITH:
                switch (type){
                    case OUTPUT:
                        if (i < 0){
                            return -i;
                        } else {
                            return 1;
                        }
                    case INPUT:
                        if (i < 0){
                            return 1;
                        } else {
                            return i;
                        }
                }
        }
        return 0;
    }

    public static int calculateFromTuple(CorrectedTuple<Integer, Integer> toCalc){
        return calculateFromTuple(toCalc, CustomVillagers.random);
    }

    public static int calculateFromTuple(CorrectedTuple<Integer, Integer> toCalc, Random random){
        CorrectedTuple<Integer, Integer> tuple = getChance(toCalc);
        if (tuple.getSecond() == 0)
            return tuple.getFirst();
        return tuple.getFirst() + random.nextInt(tuple.getSecond());
    }

    private static CorrectedTuple<Integer, Integer> getChance(CorrectedTuple<Integer, Integer> tuple){
        return tuple == null ? CorrectedTuple.newTuple(1, 0) : (tuple.getFirst() >= tuple.getSecond() ? CorrectedTuple.newTuple(tuple.getFirst(), 0) : CorrectedTuple.newTuple(tuple.getFirst() , tuple.getSecond() - tuple.getFirst()));
    }

}
