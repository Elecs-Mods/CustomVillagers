package elec332.customvillagers.json;

import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.minetweaker.MineTweakerHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.Serializable;

/**
 * Created by Elec332 on 30-6-2015.
 */
public class VillagerData implements Serializable {

    public int ID = -1;
    public float spawnChance = 0.1f;
    public int villagerToOverride = 0;
    public String textureName = "null";
    public Trade[] trades = new Trade[0];


    public class Trade implements Serializable {

        public String input1 = null;
        public int input1amount = 0;
        public String input2 = null;
        public int input2amount = 0;
        public String output = null;
        public int outputAmount = 0;

        public ItemStack getInput1(){
            return getStack(input1, input1amount);
        }

        public ItemStack getInput2(){
            return getStack(input2, input2amount);
        }

        public ItemStack getOutput(){
            return getStack(output, outputAmount);
        }

        public void setInput1(ItemStack stack){
            input1 = itemStackString(stack);
            input1amount = itemStackAmount(stack);
        }

        public void setInput2(ItemStack stack){
            input2 = itemStackString(stack);
            input2amount = itemStackAmount(stack);
        }

        public void setOutput(ItemStack stack){
            output = itemStackString(stack);
            outputAmount = itemStackAmount(stack);
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
        if (trade == null)
            throw new IllegalArgumentException();
        Trade[] copy = trades.clone();
        trades = new Trade[copy.length+1];
        for (int i = 0; i < copy.length; i++) {
            trades[i] = copy[i];
        }
        trades[copy.length] = trade;
    }

    private static ItemStack getStack(String s, int i){
        if (s == null || s.equals("") || s.equals("null"))
            return null;
        String[] s1 = s.replace(":", " ").split(" ");
        Item item = GameRegistry.findItem(s1[0], s1[1]);
        if (item != null)
            return new ItemStack(item, i, Integer.parseInt(s1[2]));
        throw new RuntimeException(s+" is not a valid item!");
    }

    private static String itemStackString(ItemStack stack){
        if (stack != null && stack.getItem()  != null)
            return MineTweakerHelper.getItemRegistryName(stack)+":"+stack.getItemDamage();
        return null;
    }

    private static int itemStackAmount(ItemStack stack){
        if (stack != null && stack.getItem()  != null)
            return stack.stackSize;
        return 0;
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
