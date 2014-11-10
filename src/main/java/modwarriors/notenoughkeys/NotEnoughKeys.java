package modwarriors.notenoughkeys;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.keys.KeyEvents;
import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Main NEK class
 *
 * @author TheTemportalist
 */
@Mod(modid = NotEnoughKeys.modid, name = NotEnoughKeys.name, version = NotEnoughKeys.version)
public class NotEnoughKeys {

	public static final String modid = "notenoughkeys", name = "Not Enough Keys", version = "@MOD_VERSION@";

	public static Logger logger;

	private static Configuration config;

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();

		Object eventhandler = new KeyEvents();
		FMLCommonHandler.instance().bus().register(eventhandler);
		MinecraftForge.EVENT_BUS.register(eventhandler);
		KeyHelper.registerMod(NotEnoughKeys.name, new String[] { "Show Binds Console" });

		NotEnoughKeys.configure(e.getModConfigurationDirectory());

	}

	private static void configure(File configDir) {
		NotEnoughKeys.config = new Configuration(new File(configDir, "NotEnoughKeys.cfg"));
		NotEnoughKeys.config.load();
		NotEnoughKeys.loadConfig();
		NotEnoughKeys.saveConfig();
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void init(FMLInitializationEvent e) {

	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void postInit(FMLPostInitializationEvent e) {
		/*
		for (ModContainer mod : Loader.instance().getActiveModList())
			KeybindTracker.modids.add(mod.getModId());
		*/
		KeyHelper.pullKeyBindings();

		NotEnoughKeys.loadConfig();
		NotEnoughKeys.saveConfig();
	}

	private static void loadConfig() {
		KeyHelper.alternates.clear();
		for (String modid : KeyHelper.compatibleMods.keySet()) {
			for (String keyDesc : KeyHelper.compatibleMods.get(modid)) {
				KeyHelper.alternates.put(
						keyDesc,
						NotEnoughKeys.config.get(
								"Key Modifiers", keyDesc,
								new boolean[] { false, false, false }
						).getBooleanList()
				);
			}
		}
	}

	public static void saveConfig() {
		// Iterate through the categories of keybindings
		for (String keyDesc : KeyHelper.alternates.keySet()) {
			NotEnoughKeys.config.get(
					"Key Modifiers", keyDesc,
					new boolean[] { false, false, false }
			).set(KeyHelper.alternates.get(keyDesc));
		}
		NotEnoughKeys.config.save();
	}

}
