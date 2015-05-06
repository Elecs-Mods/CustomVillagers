package elec332.customvillagers.minetweaker;

import cpw.mods.fml.common.registry.VillagerRegistry;
import elec332.customvillagers.Data;
import elec332.customvillagers.VillageTradeHandler;
import elec332.customvillagers.VillagerTradeCleaner;
import elec332.customvillagers.VillagerTransformer;
import elec332.customvillagers.main.CustomVillager;
import elec332.customvillagers.main.CustomVillagerModContainer;
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
@ZenClass("elec332.CustomVillagers")
public class CustomVillagers {

    @ZenMethod
    public static void registerVillager(int i){
        registerVillager(i, "default");
    }

    @ZenMethod
    public static void registerVillager(int i, String s){
        tryToApply(new registerVillager(i, s), "You attempted to register an villager too late, try restarting your game, skipping trying to register villager for ID: "+i);
    }

    @ZenMethod
    public static void addTrade(int ID, IItemStack input1, IItemStack input2, IItemStack output){
        tryToApply(new addTrade(input1, input2, output, ID) , "You attempted to register the villager trades for villager ID "+ID+" too late, try restarting your game.");
    }

    @ZenMethod
    public static void addTrade(int ID, IItemStack input1, IItemStack output){
        tryToApply(new addTrade(null, input1, output, ID) , "You attempted to register the villager trades for villager ID "+ID+" too late, try restarting your game.");
    }

    @ZenMethod
    public static void clearTrades(int ID){
        tryToApply(new clearVillagerTrades(ID), "");
    }

    @ZenMethod
    public static void addSpawnData(int toreplace, int chance, int ID){
        Float f = (chance/100F);
        tryToApply(new addSpawnData(toreplace, f, ID), "ERROR adding spawndata for villager ID "+ID);
        if (canApply())
            CustomVillagerModContainer.instance.info("Im gonna have a "+f+" chance to replace villager ID "+toreplace+" with a villager with ID "+ID);
    }

    private static class addSpawnData extends IrreversibleAction{
        public addSpawnData(int i, Float f, int i2){
            this.i = i;
            this.f = f;
            this.i2 = i2;
        }

        int i;
        Float f;
        int i2;


        @Override
        public void apply() {
            Data.spawnData.add(new VillagerTransformer(i, i2, f));
        }

        @Override
        public String describe() {
            return "Adding spawn data for villager ID: "+i;
        }
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
            if (input1 != null)
                arrayList.add(new MerchantRecipe(MineTweakerMC.getItemStack(input1), MineTweakerMC.getItemStack(input2), MineTweakerMC.getItemStack(output)));
            else
                arrayList.add(new MerchantRecipe(MineTweakerMC.getItemStack(input2), MineTweakerMC.getItemStack(output)));
            VillagerRegistry.instance().registerVillageTradeHandler(ID, new VillageTradeHandler(ID, arrayList));
        }

        @Override
        public String describe() {
            return null;
        }
    }

    private static class clearVillagerTrades extends IrreversibleAction{

        public clearVillagerTrades(int i){
            this.ID = i;
        }

        private int ID;
        @Override
        public void apply() {
            VillagerRegistry.instance().registerVillageTradeHandler(ID, new VillagerTradeCleaner(ID));
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
            CustomVillagerModContainer.instance.info("CustomVillagers properties weren't reloaded, this is not a bug, restart your client to refresh."); //MineTweakerAPI.logError(s);
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
