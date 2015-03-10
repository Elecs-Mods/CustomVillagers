package elec332.customvillagers;

import cpw.mods.fml.common.registry.VillagerRegistry;
import elec332.customvillagers.main.CustomVillager;
import elec332.customvillagers.main.CustomVillagerModContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

/**
 * Created by Elec332 on 21-2-2015.
 */
public class Data {
    static HashMap<Integer, ResourceLocation> textureData = new HashMap<Integer, ResourceLocation>();
    public static HashMap<Integer, ArrayList<MerchantRecipe>> tradeData = new HashMap<Integer, ArrayList<MerchantRecipe>>();
    public static ArrayList<VillagerTransformer> spawnData = new ArrayList<VillagerTransformer>();
    private static ArrayList<Integer> registeredVillagers = new ArrayList<Integer>();

    public static void registerVillagers(){
        for (int ID : textureData.keySet()){
            if (registeredVillagers.contains(ID)) {
                CustomVillagerModContainer.instance.fatal("Found a double registered villager ID: " + ID + ", preparing to crash...");
                throw new DuplicateVillagerIDException(ID);
            }
            VillagerRegistry.instance().registerVillagerId(ID);
            CustomVillagerModContainer.proxy.registerVillagerSkins(ID, textureData.get(ID));
            registeredVillagers.add(ID);
        }
        for (int ID : tradeData.keySet()){
            VillagerRegistry.instance().registerVillageTradeHandler(ID, new VillageTradeHandler(ID, tradeData.get(ID)));
        }
        for (VillagerTransformer t : spawnData){
            MinecraftForge.EVENT_BUS.register(t);
        }
    }

    public static  void registerTexture(String png, int ID){
        if (png.contains(".png")) {
            Data.textureData.put(ID, new ResourceLocation("textures", png));
        } else {
            CustomVillagerModContainer.instance.error(png + " is not a valid filename, the file-extension must be .png!");
            CustomVillagerModContainer.instance.error("Using default texture for villager ID " + ID);
            Data.textureData.put(ID, CustomVillager.defaultVillagerTexture);
        }
    }
}
