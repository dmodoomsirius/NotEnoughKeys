package okushama.notenoughkeys.api;

import cpw.mods.fml.common.Loader;

/**
 * @author CountryGamer
 */
public class Api {

	public static boolean isLoaded() {
		return Loader.isModLoaded("Not Enough Keys");
	}

}
