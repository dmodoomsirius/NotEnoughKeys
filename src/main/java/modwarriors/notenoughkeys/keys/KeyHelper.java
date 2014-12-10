package modwarriors.notenoughkeys.keys;

import com.google.gson.*;
import modwarriors.notenoughkeys.NotEnoughKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Some epic code borrowed from ProfMobius' Waila:
 * http://profmobius.blogspot.fr/
 */

public class KeyHelper {

	// GENERICS

	/**
	 * Registers a mod's keys with NEK
	 *
	 * @param modname        The NAME of the mod registering the key
	 * @param keyDecriptions A String[] (Array[String]) of the key descriptions. i.e. new String[]{"key.hotbar1"}
	 */
	public static void registerMod(String modname, String[] keyDecriptions) {
		KeyHelper.compatibleMods.put(modname, keyDecriptions);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keyDecriptions.length; i++) {
			sb.append(keyDecriptions[i]);
			if (i < keyDecriptions.length - 1)
				sb.append(", ");
		}
		NotEnoughKeys.logger.info(modname + " has be registered with keys " + sb.toString());
	}

	/**
	 * Gets a keybinding. What the heck is this for?!?! todo
	 *
	 * @param kb
	 * @return
	 */
	public static KeyBinding getKeybind(KeyBinding kb) {
		for (KeyBinding keb : Minecraft.getMinecraft().gameSettings.keyBindings) {
			if (keb.equals(kb)) {
				return keb;
			}
		}
		return null;
	}

	/**
	 * Pull all the current keybindings that are currently registered
	 */
	public static void pullKeyBindings() {
		for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			if (keyBinding != null && !KeyHelper.keybindings
					.containsKey(keyBinding.getKeyDescription())) {
				KeyHelper.keybindings.put(keyBinding.getKeyDescription(), keyBinding);
			}
		}
		KeyHelper.updateConflictCategory();
	}

	// KEY TRACKING

	/**
	 * Holds a list of compatible mod names and their corresponding keybinding descriptions
	 */
	public static HashMap<String, String[]> compatibleMods = new HashMap<String, String[]>();
	/**
	 * Holds keybindings by their description (not the same as holding by category)
	 */
	public static HashMap<String, KeyBinding> keybindings = new HashMap<String, KeyBinding>();
	/**
	 * Holds the alternates (SHIFT, CTRL, ALT) for each keybinding description
	 */
	public static HashMap<String, boolean[]> alternates = new HashMap<String, boolean[]>();

	// ~~~ Conflicting

	/**
	 * Temporary list of conflicting keys by their descriptions
	 */
	public static ArrayList<String> conflictingKeys = new ArrayList<String>();

	/**
	 * Updates the conflictingKeys list
	 */
	public static void updateConflictCategory() {
		KeyHelper.conflictingKeys.clear();
		KeyHelper.conflictingKeys.addAll(KeyHelper.getConflictingKeybinds());
	}

	/**
	 * Compares ALL keybindings currently registered and checks if they are conflicting
	 *
	 * @return A list of conflicting keys by their descriptions
	 */
	private static ArrayList<String> getConflictingKeybinds() {
		List<KeyBinding> allTheBinds = Arrays.asList(
				Minecraft.getMinecraft().gameSettings.keyBindings);
		ArrayList<String> allTheConflicts = new ArrayList<String>();

		boolean[] bind1Alts = null, bind2Alts = null;
		// todo find better way to sort than looping twice!
		for (KeyBinding bind1 : allTheBinds) {
			for (KeyBinding bind2 : allTheBinds) {
				// not same key
				if (!bind1.getKeyDescription().equals(bind2.getKeyDescription())) {
					// same key code
					if (bind1.getKeyCode() == bind2.getKeyCode()) {

						bind1Alts = KeyHelper.alternates.get(bind1.getKeyDescription());
						if (bind1Alts == null)
							bind1Alts = new boolean[] { false, false, false };

						bind2Alts = KeyHelper.alternates.get(bind2.getKeyDescription());
						if (bind2Alts == null)
							bind2Alts = new boolean[] { false, false, false };

						if (Arrays.equals(bind1Alts, bind2Alts)) {
							if (!allTheConflicts.contains(bind1.getKeyDescription()))
								allTheConflicts.add(bind1.getKeyDescription());
							if (!allTheConflicts.contains(bind2.getKeyDescription()))
								allTheConflicts.add(bind2.getKeyDescription());
						}
					}
				}
			}
		}
		return allTheConflicts;
	}

	// ~~~ Updating from ExportingImporting files

	public static String getExportFile() {
		JsonObject jsonObject = new JsonObject();
		JsonObject keyBindingObj;
		boolean[] modifiers;
		for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			keyBindingObj = new JsonObject();
			keyBindingObj.add("keyCode", new JsonPrimitive(keyBinding.getKeyCode()));
			modifiers = KeyHelper.alternates.get(keyBinding.getKeyDescription());
			if (modifiers != null) {
				keyBindingObj.add("shift", new JsonPrimitive(modifiers[0]));
				keyBindingObj.add("control", new JsonPrimitive(modifiers[1]));
				keyBindingObj.add("alt", new JsonPrimitive(modifiers[2]));
			}
			jsonObject.add(keyBinding.getKeyDescription(), keyBindingObj);
		}
		return KeyHelper.toReadableString(new Gson().toJson(jsonObject));
	}

	public static void importFile(File file) {
		try {
			JsonObject jsonObject = (new JsonParser()).parse(
					new FileReader(file)
			).getAsJsonObject();
			KeyBinding key;
			int keyCode;
			boolean shift = false, control = false, alt = false;
			for (Map.Entry<String, JsonElement> ent : jsonObject.entrySet()) {
				if (KeyHelper.keybindings.containsKey(ent.getKey())) {
					key = KeyHelper.keybindings.get(ent.getKey());
					JsonObject keyBindingObj = ent.getValue().getAsJsonObject();
					keyCode = keyBindingObj.get("keyCode").getAsInt();
					if (keyBindingObj.has("shift")) {
						shift = keyBindingObj.get("shift").getAsBoolean();
						control = keyBindingObj.get("control").getAsBoolean();
						alt = keyBindingObj.get("alt").getAsBoolean();
					}
					if (KeyHelper.alternates.containsKey(ent.getKey())) {
						KeyHelper.saveKeyBinding(
								key, keyCode, new boolean[] { shift, control, alt }
						);
					}
					else {
						KeyHelper.saveKeyBinding(key, keyCode, null);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ~~~ Little silly lib thingys

	private static String toReadableString(String json) {
		String readable = "";
		char[] chars = json.toCharArray();
		boolean isIteratingInString = false;
		int tabs = 0;
		for (char c : chars) {
			if (c == '}' || c == ']') {
				tabs -= 1;
				readable = KeyHelper.addLineAndTabs(readable, tabs);
			}
			readable += c;
			if (c == '{' || c == '[') {
				tabs += 1;
				readable = KeyHelper.addLineAndTabs(readable, tabs);
			}
			if (c == ':' && !isIteratingInString) {
				readable += " ";
			}
			if (c == '"') {
				isIteratingInString = !isIteratingInString;
			}
			if (c == ',' && !isIteratingInString) {
				readable = KeyHelper.addLineAndTabs(readable, tabs);
			}
		}
		return readable;
	}

	private static String addLineAndTabs(String current, int tabs) {
		current += '\n';
		for (int i = 0; i < tabs; i++) {
			current += "   ";
		}
		return current;
	}

	public static void saveKeyBinding(KeyBinding key, int keycode, boolean[] modifiers) {
		Minecraft.getMinecraft().gameSettings.setOptionKeyBinding(key, keycode);
		if (modifiers != null && KeyHelper.alternates.containsKey(key.getKeyDescription())) {
			KeyHelper.alternates.put(key.getKeyDescription(), modifiers);
		}
		KeyBinding.resetKeyBindingArrayAndHash();
		KeyHelper.updateConflictCategory();
		NotEnoughKeys.saveConfig();
	}

}
