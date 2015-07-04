package elec332.customvillagers.main;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.helper.ModInfoHelper;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.NetworkHandler;
import elec332.core.player.PlayerHelper;
import elec332.customvillagers.minetweaker.CustomVillagers;
import elec332.customvillagers.network.PacketSyncTradeContents;
import elec332.customvillagers.proxies.CommonProxy;
import minetweaker.MineTweakerAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;
import java.io.IOException;

@Mod(modid = "CustomVillagers", name = "CustomVillagers", dependencies = ModInfo.DEPENDENCIES+"@[#ELECCORE_VER#,)",
acceptedMinecraftVersions = ModInfo.ACCEPTEDMCVERSIONS, useMetadata = true, canBeDeactivated = true)
public class CustomVillagerModContainer extends ModBase{


	@SidedProxy(clientSide = "elec332.customvillagers.proxies.ClientProxy", serverSide = "elec332.customvillagers.proxies.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance("CustomVillagers")
	public static CustomVillagerModContainer instance;
	public static NetworkHandler networkHandler;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		this.modID = ModInfoHelper.getModID(event);
		this.cfg = FileHelper.getCustomConfigFileElec(event, ModInfoHelper.getModID(event), ModInfoHelper.getModID(event));
		loadConfiguration();
		CustomVillager.preInit(event);
		if (Loader.isModLoaded("MineTweaker3"))
			MineTweakerAPI.registerClass(CustomVillagers.class);

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

		notifyEvent(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		CustomVillager.serverStarting(event);
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
