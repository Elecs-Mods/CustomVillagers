package elec332.customvillagers.minetweaker;

import cpw.mods.fml.common.registry.VillagerRegistry;
import elec332.customvillagers.Data;
import elec332.customvillagers.VillageTradeHandler;
import elec332.customvillagers.main.CustomVillager;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.village.MerchantRecipe;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;

/**
 * Created by Elec332 on 9-3-2015.
 */
@ZenClass("CustomVillagers")
public class CustomVillagers {

    @ZenMethod
    public static void registerVillager(int i){
        registerVillager(i, null);
    }

    @ZenMethod
    public static void registerVillager(int i, String s){
        tryToApply(new registerVillager(i, s), "You attempted to register an villager too late, try restarting your game, skipping trying to register villager for ID: "+i);
    }

    @ZenMethod
    public static void addTrade(int ID, IItemStack input1, IItemStack input2, IItemStack output){
        tryToApply(new addTrade(input1, input2, output, ID) , "You attempted to register the villager trades for villager ID "+ID+" too late, try restarting your game.");
    }

    private static class addTrade extends IrreversibleAction{

        public addTrade(IItemStack input1, IItemStack input2, IItemStack output, int ID){
            this.input1 = input1;
            this.input2 = input2;
            this.output = output;
            this.ID = ID;
        }

        int ID;
        IItemStack input1;
        IItemStack input2;
        IItemStack output;
        ArrayList<MerchantRecipe> arrayList = new ArrayList<MerchantRecipe>();
        @Override
        public void apply() {
            arrayList.add(new MerchantRecipe(MineTweakerMC.getItemStack(input1), MineTweakerMC.getItemStack(input2), MineTweakerMC.getItemStack(output)));
            VillagerRegistry.instance().registerVillageTradeHandler(ID, new VillageTradeHandler(ID, arrayList));
        }

        @Override
        public String describe() {
            return null;
        }
    }

    private static class registerVillager extends IrreversibleAction{

        public registerVillager(int ID, String s){
            this.ID = ID;
            this.texture = s;
        }

        int ID;
        String texture;

        @Override
        public void apply() {
            Data.registerTexture(texture, ID);
        }

        @Override
        public String describe() {
            return "Registering villager ID: "+ID;
        }
    }

    private static void tryToApply(IUndoableAction action, String s){
        if (canApply())
            MineTweakerAPI.apply(action);
        else
            MineTweakerAPI.logError(s);
    }

    private static boolean canApply (){
        return !CustomVillager.hasInit;
    }

    private abstract static class IrreversibleAction implements IUndoableAction{

        @Override
        public boolean canUndo() {
            return false;
        }

        @Override
        public void undo() {

        }

        @Override
        public String describeUndo() {
            return "Impossible";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
