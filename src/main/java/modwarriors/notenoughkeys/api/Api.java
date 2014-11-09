package modwarriors.notenoughkeys.api;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
public class Api {

	public static boolean isLoaded() {
		return Loader.isModLoaded("notenoughkeys");
	}

	/**
	 * Registers a mod's keys with NEK
	 *
	 * @param modname        The NAME of the mod registering the key
	 * @param keyDecriptions A String[] (Array[String]) of the key descriptions. i.e. new String[]{"key.hotbar1"}
	 */
	public static void registerMod(String modname, String[] keyDecriptions) {
		try {
			Class.forName("modwarriors.notenoughkeys.keys.KeyBindTracker").getMethod(
					"registerMod", String.class, String[].class
			).invoke(null, modname, keyDecriptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
