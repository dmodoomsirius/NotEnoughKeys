package modwarriors.notenoughkeys.api;

import cpw.mods.fml.common.Loader;

/**
 * @author TheTemportalist
 */
public class Api {

	public static boolean isLoaded() {
		return Loader.isModLoaded("notenoughkeys");
	}

}
