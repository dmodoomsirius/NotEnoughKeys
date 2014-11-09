package modwarriors.notenoughkeys.api;

import cpw.mods.fml.common.Loader;

/**
 * @author TheTemportalist
 */
public class Api {

	public static boolean isLoaded() {
		return Loader.isModLoaded("notenoughkeys");
	}

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
