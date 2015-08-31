package elec332.customvillagers.json;

import com.google.common.collect.Lists;
import elec332.customvillagers.CustomVillagers;
import elec332.customvillagers.entity.EntityCustomVillager;
import elec332.customvillagers.network.PacketSyncTradeContents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Elec332 on 30-6-2015.
 */
public class VillagerData implements Serializable {

    public int ID = -1;
    public boolean shuffle = true;
    public float spawnChance = 0.1f;
    public int villagerToOverride = 0;
    public String textureName = "none";
    public List<Trade> trades = Lists.newArrayList();

    public List<MerchantRecipe> getTrades(EntityCustomVillager villager){
        List<MerchantRecipe> merchantRecipeList = Lists.newArrayList();
        for (Trade trade : trades){
            trade.addToList(villager, merchantRecipeList);
        }
        return merchantRecipeList;
    }

    public class Trade implements Serializable {

        public SpecialType specialType = null;
        public WrappedStack input1 = null;
        public WrappedStack input2 = null;
        public WrappedStack output = null;
        public boolean adjustProbability = false;
        public float probability = 1.0f;

        private ItemStack getTradeInput1() {
            if (input1 == null)
                return null;
            return input1.toStack(TradeSlot.INPUT);
        }

        private ItemStack getTradeInput2() {
            if (input2 == null)
                return null;
            return input2.toStack(TradeSlot.INPUT);
        }

        private ItemStack getTradeOutput() {
            if (output == null)
                return null;
            return output.toStack(TradeSlot.OUTPUT);
        }

        private void addToList(EntityCustomVillager villager, List<MerchantRecipe> merchantRecipeList){
            if (add(villager))
                merchantRecipeList.add(toMerchantRecipe(villager));
        }

        private MerchantRecipe toMerchantRecipe(EntityCustomVillager villager){
            if (specialType != null){
                switch (specialType){
                    case NORMAL:
                        break;
                    case RANDOM_ENCHANTED_BOOK:
                        ItemStack payment = input1.toStack(TradeSlot.INPUT);
                        Enchantment enchantment = Enchantment.enchantmentsBookList[CustomVillagers.random.nextInt(Enchantment.enchantmentsBookList.length)];
                        int i = MathHelper.getRandomIntegerInRange(CustomVillagers.random, enchantment.getMinLevel(), enchantment.getMaxLevel());
                        ItemStack itemstack = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, i));
                        int size = 2 + CustomVillagers.random.nextInt(5 + i * 10) + 3 * i;
                        payment.stackSize = size;
                        return new MerchantRecipe(new ItemStack(Items.book), payment, itemstack);
                    case ADD_RANDOM_ENCHANTMENT:
                        return new MerchantRecipe(input1.toStack(TradeSlot.INPUT), input2.toStack(TradeSlot.INPUT), EnchantmentHelper.addRandomEnchantment(CustomVillagers.random, input1.toStack(TradeSlot.INPUT), 5 + CustomVillagers.random.nextInt(15)));
                    case RANDOM_BLACKSMITH_WEAPONRY:
                        break;
                    default:
                        break;
                }
            }

            if (output == null) {
                throw new RuntimeException("Invalid output on trade for villager: " + VillagerData.this.ID);
            } else if (input1 == null && input2 == null) {
                throw new RuntimeException("Both trade inputs are null, this is impossible! Villager ID: " + VillagerData.this.ID);
            } else if (input1 != null && input2 != null) {
                return new MerchantRecipe(getTradeInput1(), getTradeInput2(), getTradeOutput());
            } else if (input1 != null) {
                return new MerchantRecipe(getTradeInput1(), getTradeOutput());
            } else {
                return new MerchantRecipe(getTradeInput2(), getTradeOutput());
            }
        }

        public void setInput1(WrappedStack stack) {
            this.input1 = stack;
        }

        public void setInput2(WrappedStack stack) {
            this.input2 = stack;
        }

        public void setOutput(WrappedStack stack) {
            this.output = stack;
        }

        public VillagerData.Trade setProbability(float f){
            this.probability = f;
            return this;
        }

        public VillagerData.Trade setAdjustProbability(float f){
            this.adjustProbability = true;
            return setProbability(f);
        }

        private boolean add(EntityCustomVillager villager){
            return CustomVillagers.random.nextFloat() < getProbability(villager);
        }

        private float getProbability(EntityCustomVillager villager){
            if (adjustProbability)
                return villager.adjustProbability(probability);
            return probability;
        }

    }

    public Trade newTrade(WrappedStack s1, WrappedStack s2, WrappedStack s3){
        Trade trade = new Trade();
        trade.specialType = SpecialType.NORMAL;
        trade.setInput1(s1);
        trade.setInput2(s2);
        trade.setOutput(s3);
        return trade;
    }

    public void addTrade(WrappedStack s1, WrappedStack s2, WrappedStack s3){
        addTrade(newTrade(s1, s2, s3));
    }

    public void addTrade(Trade trade){
        trades.add(trade);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VillagerData && obj.hashCode() == hashCode() || obj.getClass().isAssignableFrom(Integer.TYPE) && obj.equals(ID);
    }

    @Override
    public int hashCode() {
        return this.ID;
    }

    public enum SpecialType{
        NORMAL, RANDOM_ENCHANTED_BOOK, ADD_RANDOM_ENCHANTMENT, RANDOM_BLACKSMITH_WEAPONRY
    }

    public Trade newRandomEnchantedBookTrade(ItemStack payment){
        SpecialTypeContainer specialTypeContainer = new SpecialTypeContainer(SpecialType.RANDOM_ENCHANTED_BOOK);
        specialTypeContainer.input1 = wrappedStackWithoutAmount(payment);
        return specialTypeContainer.toTrade();
    }

    public Trade newAddRandomEnchantmentTrade(ItemStack tool, WrappedStack payment){
        SpecialTypeContainer specialTypeContainer = new SpecialTypeContainer(SpecialType.ADD_RANDOM_ENCHANTMENT);
        specialTypeContainer.input1 = wrappedStackWithoutAmount(tool);
        specialTypeContainer.input2 = payment;
        return specialTypeContainer.toTrade();
    }

    private class SpecialTypeContainer{

        private WrappedStack input1 = null;
        private WrappedStack input2 = null;
        private WrappedStack output = null;
        private SpecialType type;
        private SpecialTypeContainer(SpecialType type){
            this.type = type;
        }

        public Trade toTrade(){
            Trade trade = new Trade();
            trade.specialType = type;
            trade.input1 = input1;
            trade.input2 = input2;
            trade.output = output;
            return trade;
        }

    }

    private static WrappedStack wrappedStackWithoutAmount(ItemStack stack){
        WrappedStack wrappedStack = new WrappedStack(stack);
        wrappedStack.amount = null;
        return wrappedStack;
    }

}
