package elec332.customvillagers.minetweaker;

import cpw.mods.fml.common.registry.VillagerRegistry;
import elec332.core.main.ElecCore;
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
        apply(new registerVillager(i, s));
    }

    @ZenMethod
    public static void addTrade(int ID, IItemStack input1, IItemStack input2, IItemStack output){
        apply(new addTrade(input1, input2, output, ID));
    }

    @ZenMethod
    public static void addTrade(int ID, IItemStack input1, IItemStack output){
        apply(new addTrade(null, input1, output, ID));
    }

    @ZenMethod
    public static void clearTrades(int ID){
        apply(new clearVillagerTrades(ID));
    }

    @ZenMethod
    public static void addSpawnData(int toreplace, int chance, int ID){
        Float f = (chance/100F);
        apply(new addSpawnData(toreplace, f, ID));
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
        public void applyOnce() {
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
        public void applyOnce() {
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
        public void applyOnce() {
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
        public void applyOnce() {
            Data.registerTexture(texture, ID);
        }

        @Override
        public String describe() {
            return "Registering villager ID: "+ID;
        }
    }

    private static void apply(IUndoableAction action){
        MineTweakerAPI.apply(action);
    }

    private static boolean canApply (){
        return !CustomVillager.hasInit;
    }

    private static void sendMSG(){
        CustomVillagerModContainer.instance.info("CustomVillagers properties weren't reloaded, this is not a bug, restart your client to refresh.");
        ElecCore.proxy.addPersonalMessageToPlayer("CustomVillagers properties weren't reloaded, this is not a bug, restart your client to refresh.");
    }

    private abstract static class IrreversibleAction implements IUndoableAction{

        private boolean init = false;

        public abstract void applyOnce();

        @Override
        public void apply() {
            if (!init && canApply()){
                applyOnce();
                this.init = false;
            } else  sendMSG();
        }

        @Override
        public boolean canUndo() {
            return true;
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
