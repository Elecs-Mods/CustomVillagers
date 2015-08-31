package elec332.customvillagers.json;

import elec332.core.minetweaker.MineTweakerHelper;
import elec332.customvillagers.util.CorrectedTuple;
import elec332.customvillagers.util.Util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.Serializable;

/**
 * Created by Elec332 on 15-7-2015.
 */
public final class WrappedStack implements Serializable{

    public WrappedStack(ItemStack stack){
        this(stack.getItem());
        this.damage = stack.getItemDamage();
        this.tag = stack.hasTagCompound()?(NBTTagCompound)stack.getTagCompound().copy():null;
        this.amount = new RandomisationData(CorrectedTuple.newTuple(stack.stackSize, stack.stackSize), Algorithm.NORMAl);
    }

    public WrappedStack(Item item){
        this.item = MineTweakerHelper.getItemRegistryName(new ItemStack(item));
        this.damage = 0;
        this.tag = null;
    }

    public WrappedStack setAmount(RandomisationData data){
        this.amount = data;
        return this;
    }

    public String item;
    public RandomisationData amount;
    public int damage;
    public NBTTagCompound tag;

    private int getStackSize(TradeSlot type){
        return amount == null? 1 : amount.algorithm.calculate(amount.toTuple(), type);
    }

    public ItemStack toStack(TradeSlot type){
        ItemStack ret = new ItemStack(Util.fromString(item), getStackSize(type), damage);
        ret.setTagCompound(tag);
        return ret;
    }

    public ItemStack toPacketStack(){
        ItemStack ret = new ItemStack(Util.fromString(item), 1, damage);
        ret.setTagCompound(tag);
        return ret;
    }


    public Item toItem(){
        return Util.fromString(item);
    }

}
