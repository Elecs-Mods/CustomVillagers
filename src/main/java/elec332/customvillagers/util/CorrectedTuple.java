package elec332.customvillagers.util;

import net.minecraft.util.Tuple;

/**
 * Created by Elec332 on 14-7-2015.
 */
public class CorrectedTuple<F, S> {

    @SuppressWarnings("unchecked")
    public static <F, S> CorrectedTuple<F, S> fromTuple(Tuple tuple){
        return new CorrectedTuple<F, S>((F) tuple.getFirst(), (S) tuple.getSecond());
    }

    public static <F, S> CorrectedTuple<F, S> newTuple(F first, S second){
        return new CorrectedTuple<F, S>(first, second);
    }

    private F first;
    private S second;

    private CorrectedTuple(F first, S second){
        this.first = first;
        this.second = second;
    }

    public F getFirst(){
        return first;
    }

    public S getSecond(){
        return second;
    }

    public Tuple toTuple(){
        return new Tuple(first, second);
    }
}
