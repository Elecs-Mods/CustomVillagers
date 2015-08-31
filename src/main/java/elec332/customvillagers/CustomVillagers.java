package elec332.customvillagers;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.helper.ModInfoHelper;
import elec332.core.json.JsonHandler;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.NetworkHandler;
import elec332.core.player.PlayerHelper;
import elec332.customvillagers.entity.EntityCustomVillager;
import elec332.customvillagers.json.Algorithm;
import elec332.customvillagers.json.RandomisationData;
import elec332.customvillagers.json.VillagerData;
import elec332.customvillagers.json.WrappedStack;
import elec332.customvillagers.network.PacketSlotStuff;
import elec332.customvillagers.network.PacketSyncTradeContents;
import elec332.customvillagers.proxies.CommonProxy;
import elec332.customvillagers.registry.CustomVillagerRegistry;
import elec332.customvillagers.util.CorrectedTuple;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Random;

@Mod(modid = "CustomVillagers", name = "CustomVillagers", dependencies = ModInfo.DEPENDENCIES+"@[#ELECCORE_VER#,)",
acceptedMinecraftVersions = ModInfo.ACCEPTEDMCVERSIONS, useMetadata = true, canBeDeactivated = true)
public class CustomVillagers extends ModBase{


	@SidedProxy(clientSide = "elec332.customvillagers.proxies.ClientProxy", serverSide = "elec332.customvillagers.proxies.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance("CustomVillagers")
	public static CustomVillagers instance;
	public static NetworkHandler networkHandler;
	public static File baseFile;
	public static ResourceLocation defaultVillagerTexture = new ResourceLocation("textures/entity/villager/villager.png");
	public static Random random;
	public static boolean rewriteFiles = true;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		baseFile = FileHelper.getCustomConfigFolderElec(event, ModInfoHelper.getModID(event));
		this.modID = ModInfoHelper.getModID(event);
		this.cfg = new File(baseFile, modID()+".cfg");
		random = new Random();
		loadConfiguration();
		if (developmentEnvironment)
			new DebugItem();

		MCModInfo.CreateMCModInfo(event, "Created by Elec332",
				"This mod allows you to create custom villagers with config files",
				"-", "assets/elec332/logo.png",
				new String[] {"Elec332"});
		notifyEvent(event);
	}
	
	@EventHandler
    public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.registerResourcePacks();
		networkHandler = new NetworkHandler(modID);
		networkHandler.registerServerPacket(PacketSyncTradeContents.class);
		networkHandler.registerServerPacket(PacketSlotStuff.class);
		notifyEvent(event);
    }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		rewriteFiles = config.getBoolean("RewriteFiles", Configuration.CATEGORY_GENERAL, true, "Set to true to wipe all Json files from vanilla villagers and rewrite them.");
		loadConfiguration();
		EntityList.addMapping(EntityCustomVillager.class, "EntityCustomVillager", EntityRegistry.findGlobalUniqueEntityId());
		notifyEvent(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event){
		System.out.println("LoadCompleteEventFired!");
		if (rewriteFiles){
			for (int i = 0; i < 5; i++) {
				File file = CustomVillagerRegistry.getJsonFile(i);
				VillagerData data = new VillagerData();
				data.ID = i;
				data.spawnChance = 0.0f;
				data.villagerToOverride = i;
				switch (i){
					case 0:
						addNormalTrade(Items.wheat, 0.9f, data);
						addNormalTrade(Item.getItemFromBlock(Blocks.wool), 0.5f, data);
						addNormalTrade(Items.chicken, 0.5f, data);
						addNormalTrade(Items.cooked_fished, 0.4f, data);
						addBlacksmithTrade(Items.bread, 0.9f, data);
						addBlacksmithTrade(Items.melon, 0.3f, data);
						addBlacksmithTrade(Items.apple, 0.3f, data);
						addBlacksmithTrade(Items.shears, 0.3f, data);
						addBlacksmithTrade(Items.flint_and_steel, 0.3f, data);
						addBlacksmithTrade(Items.cooked_chicken, 0.3f, data);
						addBlacksmithTrade(Items.arrow, 0.5f, data);
						data.addTrade(data.newTrade(new WrappedStack(new ItemStack(Blocks.gravel, 10)), new WrappedStack(Items.emerald), new WrappedStack(Items.flint).setAmount(new RandomisationData(CorrectedTuple.newTuple(4, 6), Algorithm.NORMAl))));
						break;
					case 1:
						addNormalTrade(Items.paper, 0.8f, data);
						addNormalTrade(Items.book, 0.8f, data);
						addNormalTrade(Items.written_book, 0.3f, data);
						addBlacksmithTrade(Item.getItemFromBlock(Blocks.bookshelf), 0.8f, data);
						addBlacksmithTrade(Item.getItemFromBlock(Blocks.glass), 0.2f, data);
						addBlacksmithTrade(Items.compass, 0.2f, data);
						addBlacksmithTrade(Items.clock, 0.2f, data);
						data.addTrade(data.newRandomEnchantedBookTrade(new ItemStack(Items.emerald)).setAdjustProbability(0.07f));
						break;
					case 2:
						addBlacksmithTrade(Items.ender_eye, 0.3f, data);
						addBlacksmithTrade(Items.experience_bottle, 0.2f, data);
						addBlacksmithTrade(Items.redstone, 0.4f, data);
						addBlacksmithTrade(Item.getItemFromBlock(Blocks.glowstone), 0.3f, data);
						addToolEnchantTrade(Items.iron_sword, data);
						addToolEnchantTrade(Items.diamond_sword, data);
						addToolEnchantTrade(Items.iron_chestplate, data);
						addToolEnchantTrade(Items.diamond_chestplate, data);
						addToolEnchantTrade(Items.iron_axe, data);
						addToolEnchantTrade(Items.diamond_axe, data);
						addToolEnchantTrade(Items.iron_pickaxe, data);
						addToolEnchantTrade(Items.diamond_pickaxe, data);
						break;
					case 3:
						addNormalTrade(Items.coal, 0.7f, data);
						addNormalTrade(Items.iron_ingot, 0.5f, data);
						addNormalTrade(Items.gold_ingot, 0.5f, data);
						addNormalTrade(Items.diamond, 0.5f, data);
						addBlacksmithTrade(Items.iron_sword, 0.5f, data);
						addBlacksmithTrade(Items.diamond_sword, 0.5f, data);
						addBlacksmithTrade(Items.iron_axe, 0.3F, data);
						addBlacksmithTrade(Items.diamond_axe, 0.3F, data);
						addBlacksmithTrade(Items.iron_pickaxe, 0.5F, data);
						addBlacksmithTrade(Items.diamond_pickaxe, 0.5F, data);
						addBlacksmithTrade(Items.iron_shovel, 0.2F, data);
						addBlacksmithTrade(Items.diamond_shovel, 0.2F, data);
						addBlacksmithTrade(Items.iron_hoe, 0.2F, data);
						addBlacksmithTrade(Items.diamond_hoe, 0.2F, data);
						addBlacksmithTrade(Items.iron_boots, 0.2F, data);
						addBlacksmithTrade(Items.diamond_boots, 0.2F, data);
						addBlacksmithTrade(Items.iron_helmet, 0.2F, data);
						addBlacksmithTrade(Items.diamond_helmet, 0.2F, data);
						addBlacksmithTrade(Items.iron_chestplate, 0.2F, data);
						addBlacksmithTrade(Items.diamond_chestplate, 0.2F, data);
						addBlacksmithTrade(Items.iron_leggings, 0.2F, data);
						addBlacksmithTrade(Items.diamond_leggings, 0.2F, data);
						addBlacksmithTrade(Items.chainmail_boots, 0.1F, data);
						addBlacksmithTrade(Items.chainmail_helmet, 0.1F, data);
						addBlacksmithTrade(Items.chainmail_chestplate, 0.1F, data);
						addBlacksmithTrade(Items.chainmail_leggings, 0.1F, data);
						break;
					case 4:
						addNormalTrade(Items.coal, 0.7F, data);
						addNormalTrade(Items.porkchop, 0.5F, data);
						addNormalTrade(Items.beef, 0.5F, data);
						addBlacksmithTrade(Items.saddle, 0.1F, data);
						addBlacksmithTrade(Items.leather_chestplate, 0.3F, data);
						addBlacksmithTrade(Items.leather_boots, 0.3F, data);
						addBlacksmithTrade(Items.leather_helmet, 0.3F, data);
						addBlacksmithTrade(Items.leather_leggings, 0.3F, data);
						addBlacksmithTrade(Items.cooked_porkchop, 0.3F, data);
						addBlacksmithTrade(Items.cooked_beef, 0.3F, data);
						break;
				}
				JsonHandler.toFile(file, JsonHandler.newJsonObject(JsonHandler.toJsonElement(data, VillagerData.class), "villager"));
			}
			config.getCategory(Configuration.CATEGORY_GENERAL).get("RewriteFiles").set(false);
			config.save();
		}
		CustomVillagerRegistry.instance.init();
	}

	private static void addToolEnchantTrade(Item tool, VillagerData data){
		data.addTrade(data.newAddRandomEnchantmentTrade(new ItemStack(tool), new WrappedStack(Items.emerald).setAmount(new RandomisationData(CorrectedTuple.newTuple(2, 5), Algorithm.NORMAl))).setAdjustProbability(0.05f));
	}

	private static void addNormalTrade(Item item, float probability, VillagerData data){
		data.addTrade(data.newTrade(new WrappedStack(item).setAmount(new RandomisationData(EntityCustomVillager.fromVillagerSellingList(item), Algorithm.NORMAl)), null, new WrappedStack(Items.emerald)).setAdjustProbability(probability));
	}

	private static void addBlacksmithTrade(Item item, float probability, VillagerData data){
		data.addTrade(data.newTrade(new WrappedStack(Items.emerald).setAmount(new RandomisationData(EntityCustomVillager.fromBlackSmithSellingList(item), Algorithm.BLACKSMITH)), null, new WrappedStack(item).setAmount(new RandomisationData(EntityCustomVillager.fromBlackSmithSellingList(item), Algorithm.BLACKSMITH))).setAdjustProbability(probability));
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandBase() {
			@Override
			public String getCommandName() {
				return "CustomVillagersGui";
			}

			@Override
			public boolean canCommandSenderUseCommand(ICommandSender commandSender) {
				return commandSender instanceof EntityPlayer && PlayerHelper.isPlayerInCreative((EntityPlayer) commandSender);
			}

			@Override
			public String getCommandUsage(ICommandSender commandSender) {
				return "What's this?";
			}

			@Override
			public void processCommand(ICommandSender commandSender, String[] string) {
				if (commandSender instanceof EntityPlayer)
					((EntityPlayer) commandSender).openGui(instance, Integer.parseInt(string[0]), commandSender.getEntityWorld(), 0, 0, 0);
			}
		});
	}

	public static String getDeclaredFieldDefaultResourcePacks(){
		return developmentEnvironment ? "defaultResourcePacks" : "field_110449_ao";
	}

	File cfg;
	String modID;

	@Override
	public File configFile() {
		return cfg;
	}

	@Override
	public String modID(){
		return modID;
	}
}
