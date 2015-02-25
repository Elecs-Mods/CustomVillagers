package elec332.customvillagers;

import cpw.mods.fml.common.registry.VillagerRegistry;
import elec332.customvillagers.main.CustomVillagerModContainer;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

/**
 * Created by Elec332 on 21-2-2015.
 */
public class Data {
    public static HashMap<Integer, String> textureData = new HashMap<Integer, String>();
    public static HashMap<Integer, ArrayList<MerchantRecipe>> tradeData = new HashMap<Integer, ArrayList<MerchantRecipe>>();
    public static ArrayList<VillagerTransformer> spawnData = new ArrayList<VillagerTransformer>();
    static List<Integer> registeredVillagers = new ArrayList<Integer>();

    public static void registerVillagers(){

        for (int ID : tradeData.keySet()){
            if (registeredVillagers.contains(ID)) {
                CustomVillagerModContainer.instance.fatal("Found a double registered villager ID: " + ID + ", preparing to crash...");
                throw new DuplicateVillagerIDException(ID);
            }
            VillagerRegistry.instance().registerVillagerId(ID);
            VillagerRegistry.instance().registerVillageTradeHandler(ID, new VillageTradeHandler(ID, tradeData.get(ID)));
            registeredVillagers.add(ID);
        }
        CustomVillagerModContainer.proxy.registerVillagerSkins(textureData);
        for (VillagerTransformer t : spawnData){
            MinecraftForge.EVENT_BUS.register(t);
        }
    }
}
