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
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.NetworkHandler;
import elec332.core.player.PlayerHelper;
import elec332.customvillagers.entity.EntityCustomVillager;
import elec332.customvillagers.network.PacketSyncTradeContents;
import elec332.customvillagers.proxies.CommonProxy;
import elec332.customvillagers.registry.CustomVillagerRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		baseFile = FileHelper.getCustomConfigFolderElec(event, ModInfoHelper.getModID(event));
		this.modID = ModInfoHelper.getModID(event);
		this.cfg = new File(baseFile, modID());
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
		notifyEvent(event);
    }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		loadConfiguration();
		EntityList.addMapping(EntityCustomVillager.class, "EntityCustomVillager", EntityRegistry.findGlobalUniqueEntityId());
		notifyEvent(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event){
		CustomVillagerRegistry.instance.init();
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
