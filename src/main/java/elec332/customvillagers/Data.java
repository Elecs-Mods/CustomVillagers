package elec332.customvillagers;

import cpw.mods.fml.common.registry.VillagerRegistry;
import elec332.customvillagers.main.CustomVillagerModContainer;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Elec332 on 21-2-2015.
 */
public class Data {
    public static HashMap<Integer, String> textureData = new HashMap<Integer, String>();
    public static HashMap<Integer, ArrayList<MerchantRecipe>> tradeData = new HashMap<Integer, ArrayList<MerchantRecipe>>();
    public static ArrayList<VillagerTransformer> spawnData = new ArrayList<VillagerTransformer>();

    public static void registerVillagers(){
        CustomVillagerModContainer.proxy.registerVillagerSkins(textureData);
        for (int ID : tradeData.keySet()){
            Set<Integer> IDS = tradeData.keySet();
            if (IDS.contains(ID)) {
                CustomVillagerModContainer.instance.fatal("Found a double registered villager ID: " + ID + ", preparing to crash...");
                throw new DuplicateVillagerIDException(ID);
            }
            VillagerRegistry.instance().registerVillagerId(ID);
            VillagerRegistry.instance().registerVillageTradeHandler(ID, new VillageTradeHandler(ID, tradeData.get(ID)));
        }
        for (VillagerTransformer t : spawnData){
            MinecraftForge.EVENT_BUS.register(t);
        }
    }
}
