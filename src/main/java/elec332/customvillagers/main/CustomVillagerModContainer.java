package elec332.customvillagers.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.helper.ModInfoHelper;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.modInfo;
import elec332.customvillagers.proxies.CommonProxy;

import java.io.File;

@Mod(modid = "CustomVillagers", name = "CustomVillagers", dependencies = modInfo.DEPENDENCIES+"@[#ELECCORE_VER#,)",
acceptedMinecraftVersions = modInfo.ACCEPTEDMCVERSIONS, useMetadata = true, canBeDeactivated = true)
public class CustomVillagerModContainer extends ModBase{


	@SidedProxy(clientSide = "elec332.customvillagers.proxies.ClientProxy", serverSide = "elec332.customvillagers.proxies.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance("CustomVillagers")
	public static CustomVillagerModContainer instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.modID = ModInfoHelper.getModID(event);
		this.cfg = FileHelper.getCustomConfigFileElec(event, ModInfoHelper.getModID(event), ModInfoHelper.getModID(event));
		loadConfiguration();
		CustomVillager.preInit(event);


		MCModInfo.CreateMCModInfo(event, "Created by Elec332",
				"This mod allows you to create custom villagers with config files",
				"-", "assets/elec332/logo.png",
				new String[] {"Elec332"});
		notifyEvent(event);
	}
	
	@EventHandler
    public void init(FMLInitializationEvent event) {

		proxy.registerResourcePacks();
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
