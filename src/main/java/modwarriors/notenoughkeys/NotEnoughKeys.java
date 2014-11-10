package modwarriors.notenoughkeys;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.console.Console;
import modwarriors.notenoughkeys.keys.Binds;
import modwarriors.notenoughkeys.keys.KeybindTracker;
import modwarriors.notenoughkeys.keys.Keybinds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = NotEnoughKeys.modid, name = NotEnoughKeys.name, version = NotEnoughKeys.version)
public class NotEnoughKeys {
	public static final String modid = "notenoughkeys", name = "Not Enough Keys", version = "@MOD_VERSION@";

	public static Logger logger;
	public static Console console = new Console();

	private static Configuration config;

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();

		Object eventhandler = new Keybinds();
		FMLCommonHandler.instance().bus().register(eventhandler);
		MinecraftForge.EVENT_BUS.register(eventhandler);
		KeybindTracker.registerMod(NotEnoughKeys.name, new String[] { "Show Binds Console" });

		ClientRegistry.registerKeyBinding(Keybinds.openConsole);

		Binds.init();

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
		KeybindTracker.populate();

		NotEnoughKeys.loadConfig();
		NotEnoughKeys.saveConfig();
	}

	private static void loadConfig() {
		KeybindTracker.alternates.clear();
		for (String modid : KeybindTracker.compatibleMods.keySet()) {
			for (String keyDesc : KeybindTracker.compatibleMods.get(modid)) {
				KeybindTracker.alternates.put(
						keyDesc,
						NotEnoughKeys.config.get(
								"Key Modifiers", keyDesc,
								new boolean[]{false, false, false}
						).getBooleanList()
				);
			}
		}
	}

	public static void saveConfig() {
		// Iterate through the categories of keybindings
		for (String keyDesc : KeybindTracker.alternates.keySet()) {
			NotEnoughKeys.config.get(
					"Key Modifiers", keyDesc,
					new boolean[] { false, false, false }
			).set(KeybindTracker.alternates.get(keyDesc));
		}
		NotEnoughKeys.config.save();
	}

}
