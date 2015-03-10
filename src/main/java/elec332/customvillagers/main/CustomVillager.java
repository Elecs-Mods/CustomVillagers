package elec332.customvillagers.main;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.helper.FileHelper;
import elec332.core.helper.ModInfoHelper;
import elec332.customvillagers.Data;
import elec332.customvillagers.DebugItem;
import elec332.customvillagers.VillagerTransformer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Elec332 on 12-2-2015.
 */
public class CustomVillager {

    public static File baseFile;
    public static Configuration main = CustomVillagerModContainer.instance.config;
    public static String [] VillageCFGNames;
    public static HashMap<String, Configuration> registry = new HashMap<String, Configuration>();
    public static boolean hasInit = false;
    public static ResourceLocation defaultVillagerTexture = new ResourceLocation(CustomVillagerModContainer.instance.modID(), "default.png");

    public static void preInit(FMLPreInitializationEvent event) {
        baseFile = FileHelper.getCustomConfigFolderElec(event, ModInfoHelper.getModID(event));
        new File(baseFile, "textures").mkdirs();
        main.load();
        VillageCFGNames = main.getStringList("Filenames", Configuration.CATEGORY_GENERAL, new String[]{"custom1.cfg", "custom2.cfg"}, "Insert the names for CFG files here");
        if (main.getBoolean("Debugger", Configuration.CATEGORY_GENERAL, false, "Setting this to true will add an Debugger item to the game.")){
            new DebugItem();
        }
        main.save();

        for (String name : VillageCFGNames){
            if (name.contains(".cfg")) {
                Configuration config = new Configuration(new File(baseFile, name));
                registry.put(name, config);
            } else {
                CustomVillagerModContainer.instance.error(name + " is not a valid filename, the file-extension must be .cfg!");
            }
        }
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        if (!hasInit) {
            for (String name : registry.keySet()) {
                Configuration configuration = registry.get(name);
                int ID = configuration.getInt("ID", Configuration.CATEGORY_GENERAL, 9999, 0, 10000, null);
                String png = configuration.getString("Texture", Configuration.CATEGORY_GENERAL, "default.png", null);
                int tradesAm = configuration.getInt("trades", Configuration.CATEGORY_GENERAL, 3, 1, 10, null);
                int toreplace = configuration.getInt("replaceID", "spawn_mechanics", 0, 0, 10000, "The profession ID this villager will replace upon spawning");
                Float chance = configuration.getFloat("chance", "spawn_mechanics", 0.1F, 0.0F, 1.0F, "The chance this villager will replace a villager of the above profession ('value' times/1), so 0.1 = once per 10 villagers");
                ArrayList<MerchantRecipe> trades = new ArrayList<MerchantRecipe>();
                for (int i = 1; i < (tradesAm + 1); i++) {
                    String item1 = configuration.getString("Input1", "trade_" + i, "minecraft:emerald:0", null);
                    int am1 = configuration.getInt("Input1amount", "trade_" + i, 1, 1, 64, null);
                    String[] splitted1 = item1.replace(":", " ").split(" ");
                    ItemStack Item1 = new ItemStack(GameRegistry.findItem(splitted1[0], splitted1[1]), am1, Integer.parseInt(splitted1[2]));
                    String item2 = configuration.getString("Input2", "trade_" + i, "minecraft:emerald:0", null);
                    int am2 = configuration.getInt("Input2amount", "trade_" + i, 1, 1, 64, null);
                    String[] splitted2 = item2.replace(":", " ").split(" ");
                    ItemStack Item2 = item2.equalsIgnoreCase("null") ? null : new ItemStack(GameRegistry.findItem(splitted2[0], splitted2[1]), am2, Integer.parseInt(splitted2[2]));
                    String item3 = configuration.getString("return", "trade_" + i, "minecraft:emerald:0", null);
                    int am3 = configuration.getInt("returnamount", "trade_" + i, 2, 1, 64, null);
                    String[] splitted3 = item3.replace(":", " ").split(" ");
                    ItemStack Item3 = new ItemStack(GameRegistry.findItem(splitted3[0], splitted3[1]), am3, Integer.parseInt(splitted3[2]));

                    trades.add(new MerchantRecipe(Item1, Item2, Item3));
                }
                configuration.save();
                Data.registerTexture(png, ID);
                Data.tradeData.put(ID, trades);
                Data.spawnData.add(new VillagerTransformer(toreplace, ID, chance));
            }
            Data.registerVillagers();
            hasInit = true;
            //MinecraftForge.EVENT_BUS.register(new CustomVillager());
            /*main.load();
            String protection = main.getString("Villager protection", Configuration.CATEGORY_GENERAL, "no", "Sets if you wanna protect villager from getting attacked, accepted values: no, all, some");
            int[] toProtect = main.get(Configuration.CATEGORY_GENERAL, "ID's to protect", new int[]{0, 1, 2, 3, 4, 5}, "If you set \"Villager Protection\" to \" some\", then define here wich villager ID's you want to protect").getIntList();
            if (protection.equalsIgnoreCase("all"))
                MinecraftForge.EVENT_BUS.register(new VillagerProtectionHandler(true, null));
            if (protection.equalsIgnoreCase("some")){

            }*/
        }
    }
}
