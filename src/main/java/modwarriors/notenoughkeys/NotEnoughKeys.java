package modwarriors.notenoughkeys;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.console.Console;
import modwarriors.notenoughkeys.keys.Binds;
import modwarriors.notenoughkeys.keys.KeybindTracker;
import modwarriors.notenoughkeys.keys.Keybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

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
		KeybindTracker.modKeybinds.put("All", new ArrayList<KeyBinding>());
		ArrayList<KeyBinding> vanillaKeys = new ArrayList<KeyBinding>();
		Collections.addAll(vanillaKeys, Minecraft.getMinecraft().gameSettings.keyBindings);
		KeybindTracker.modKeybinds.put("Minecraft", vanillaKeys);

		Object eventhandler = new Keybinds();
		FMLCommonHandler.instance().bus().register(eventhandler);
		MinecraftForge.EVENT_BUS.register(eventhandler);

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

	private static void loadConfig() {
		for (String category : KeybindTracker.modKeybinds.keySet()) {
			for (KeyBinding keyBinding : KeybindTracker.modKeybinds.get(category)) {
				KeybindTracker.alternates.put(
						keyBinding,
						NotEnoughKeys.config.get(
								category, keyBinding.getKeyDescription(),
								new boolean[] { false, false, false }
						).getBooleanList()
				);
			}
		}
	}

	public static void saveConfig() {

		// Iterate through the categories of keybindings
		for (String category : KeybindTracker.modKeybinds.keySet()) {
			for (KeyBinding keyBinding : KeybindTracker.modKeybinds.get(category)) {
				NotEnoughKeys.config.get(
						category, keyBinding.getKeyDescription(),
						new boolean[] { false, false, false }
				).set(KeybindTracker.alternates.get(keyBinding));
			}
		}

		NotEnoughKeys.config.save();
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void init(FMLInitializationEvent e) {

	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void postInit(FMLPostInitializationEvent e) {
		for (ModContainer mod : Loader.instance().getActiveModList())
			KeybindTracker.modIds.put(mod.getSource().getName(), mod.getName());
		KeybindTracker.populate();

		NotEnoughKeys.loadConfig();
		NotEnoughKeys.saveConfig();
	}
}
