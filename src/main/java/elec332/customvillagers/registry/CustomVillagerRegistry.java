package elec332.customvillagers.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.registry.VillagerRegistry;
import elec332.core.json.JsonHandler;
import elec332.core.util.EventHelper;
import elec332.customvillagers.CustomVillagers;
import elec332.customvillagers.DuplicateVillagerIDException;
import elec332.customvillagers.event.VillagerTransformer;
import elec332.customvillagers.json.VillagerData;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 1-7-2015.
 */
public class CustomVillagerRegistry {

    public static final CustomVillagerRegistry instance = new CustomVillagerRegistry();
    private CustomVillagerRegistry(){

        registeredIDs = Lists.newArrayList(cpw.mods.fml.common.registry.VillagerRegistry.getRegisteredVillagers());
        registry = Maps.newHashMap();
    }

    private static final String FILE_PREFIX = "Villager_";

    public static File getJsonFile(String s){
        return new File(CustomVillagers.baseFile, s);
    }

    public static File getJsonFile(int i){
        return getJsonFile(FILE_PREFIX+i+".json");
    }

    public boolean isVillagerRegistered(int i){
        return registeredIDs.contains(i);
    }

    private List<Integer> registeredIDs;
    private Map<Integer, VillagerData> registry;

    public void rewriteFiles(){
        for (int i : registeredIDs)
            JsonHandler.toFile(getJsonFile(i), JsonHandler.newJsonObject(JsonHandler.toJsonElement(registry.get(i), VillagerData.class), "villager"));
    }

    public VillagerData getData(int i){
        if (isVillagerRegistered(i)) {
            return registry.get(i);
        } else {
            VillagerData data = new VillagerData();
            data.ID = i;
            registry.put(i, data);
            registeredIDs.add(i);
            return data;
        }
    }

    public void init(){
        EventHelper.registerHandlerForge(new VillagerTransformer());
        List<VillagerData> dataList = Lists.newArrayList();
        for (String s : getAllVillagerFiles()) {
            try {
                if (!getJsonFile(s).exists() && !getJsonFile(s).createNewFile())
                    throw new IOException("Error creating new file: " + s);
                dataList.add(JsonHandler.getGson().fromJson(JsonHandler.getMainFileObject(getJsonFile(s)).get("villager"), VillagerData.class));
                CustomVillagers.instance.info("Successfully read file: " + s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            for (VillagerData data : dataList) {
                if (!(data.ID >=0) && isVillagerRegistered(data.ID))
                    throw new DuplicateVillagerIDException(data.ID);
                registeredIDs.add(data.ID);
                registerTexture(data.textureName, data.ID);
                VillagerRegistry.instance().registerVillagerId(data.ID);
                registry.put(data.ID, data);
                /*List<MerchantRecipe> toAdd = Lists.newArrayList();
                for (VillagerData.Trade trade : data.trades) {
                    ItemStack s1 = trade.getInput1();
                    ItemStack s2 = trade.getInput2();
                    ItemStack o = trade.getOutput();
                    if (o == null) {
                        throw new RuntimeException("Invalid output on trade for villager: " + data.ID);
                    } else if (s1 == null && s2 == null) {
                        throw new RuntimeException("Both trade inputs are null, this is impossible! Villager ID: " + data.ID);
                    } else if (s1 != null && s2 != null) {
                        toAdd.add(new MerchantRecipe(s1, s2, o));
                    } else if (s1 != null) {
                        toAdd.add(new MerchantRecipe(s1, o));
                    } else {
                        toAdd.add(new MerchantRecipe(s2, o));
                    }
                }
                VillagerRegistry.instance().registerVillageTradeHandler(data.ID, new VillageTradeHandler(data.ID, toAdd));
                //EventHelper.registerHandlerForge(new VillagerTransformer(data.villagerToOverride, data.ID, data.spawnChance));*/
            }

    }

    private void registerTexture(String png, int ID){
        if (png.contains(".png")) {
            CustomVillagers.proxy.registerVillagerSkin(ID, new ResourceLocation("textures", png));
        } else if (!png.equals("none")){
            CustomVillagers.instance.error(png + " is not a valid filename, the file-extension must be .png!");
            CustomVillagers.instance.error("Using default texture for villager ID " + ID);
            CustomVillagers.proxy.registerVillagerSkin(ID, CustomVillagers.defaultVillagerTexture);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public List<String> getAllVillagerFiles(){
        List<String> ret = Lists.newArrayList();
        for (File file : CustomVillagers.baseFile.listFiles()){
            String name = file.getName();
            if (name.contains(".json") && name.contains(FILE_PREFIX))
                ret.add(name);
        }
        return ret;
    }

}
