package elec332.customvillagers;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Elec332 on 30-6-2015.
 */
public class VillagerData implements Serializable {

    public int ID = -1;
    public float spawnChance = 0.1f;
    public int villagerToOverride = 0;
    public String textureName = "null";
    public List<Trade> trades = Lists.newArrayList();


    public class Trade implements Serializable {

        public ItemStack input1 = null;
        public ItemStack input2 = null;
        public ItemStack output = null;

        public ItemStack getInput1() {
            return input1;
        }

        public ItemStack getInput2() {
            return input2;
        }

        public ItemStack getOutput() {
            return output;
        }

        public void setInput1(ItemStack input1) {
            this.input1 = input1;
        }

        public void setInput2(ItemStack input2) {
            this.input2 = input2;
        }

        public void setOutput(ItemStack output) {
            this.output = output;
        }

    }

    public void addTrade(ItemStack s1, ItemStack s2, ItemStack s3){
        Trade trade = new Trade();
        trade.setInput1(s1);
        trade.setInput2(s2);
        trade.setOutput(s3);
        addTrade(trade);
    }

    public void addTrade(Trade trade){
        trades.add(trade);
        /*if (trade == null)
            throw new IllegalArgumentException();
        Trade[] copy = trades.clone();
        trades = new Trade[copy.length+1];
        for (int i = 0; i < copy.length; i++) {
            trades[i] = copy[i];
        }
        trades[copy.length] = trade;*/
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VillagerData && obj.hashCode() == hashCode() || obj.getClass().isAssignableFrom(Integer.TYPE) && obj.equals(ID);
    }

    @Override
    public int hashCode() {
        return this.ID;
    }

}
