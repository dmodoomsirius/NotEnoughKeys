package okushama.notenoughkeys;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import okushama.notenoughkeys.console.Console;
import okushama.notenoughkeys.keys.Binds;
import okushama.notenoughkeys.keys.KeybindTracker;
import okushama.notenoughkeys.keys.Keybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = NotEnoughKeys.modid, name = NotEnoughKeys.name, version = NotEnoughKeys.version)
public class NotEnoughKeys {
	public static final String modid = "notenoughkeys", name = "Not Enough Keys", version = "@MOD_VERSION@";

	public static Logger logger;
	public static Console console = new Console();

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
		KeybindTracker.modKeybinds.put("All", new ArrayList<KeyBinding>());
		ArrayList<KeyBinding> vanillaKeys = new ArrayList<KeyBinding>();
        Collections.addAll(vanillaKeys, Minecraft.getMinecraft().gameSettings.keyBindings);
		KeybindTracker.modKeybinds.put("Minecraft", vanillaKeys);
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void init(FMLInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(new Ticker());
		ClientRegistry.registerKeyBinding(Keybinds.openConsole);
		Binds.init();
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public static void postInit(FMLPostInitializationEvent e) {
		for (ModContainer mod : Loader.instance().getActiveModList())
			KeybindTracker.modIds.put(mod.getSource().getName(), mod.getName());
		KeybindTracker.populate();
	}
}