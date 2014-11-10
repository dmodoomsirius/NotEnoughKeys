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

public class KeybindTracker {

	/**
	 * Holds a list of compatible mod names and their corresponding keybinding descriptions
	 */
	public static HashMap<String, String[]> compatibleMods = new HashMap<String, String[]>();
	/**
	 * Holds keybindings by their description (not the same as holding by category)
	 */
	public static HashMap<String, KeyBinding> keybindings = new HashMap<String, KeyBinding>();

	/**
	 * Holds all active modids
	 */
	//public static ArrayList<String> modids = new ArrayList<String>();
	/**
	 * Holds all keybindings per category string
	 */
	//public static HashMap<String, ArrayList<KeyBinding>> categoryKeybinds = new HashMap<String, ArrayList<KeyBinding>>();

	/**
	 * Holds the alternates (SHIFT, CTRL, ALT) for each keybinding description
	 */
	public static HashMap<String, boolean[]> alternates = new HashMap<String, boolean[]>();

	public static ArrayList<String> conflictingKeys = new ArrayList<String>();

	public static KeyBinding getKeybind(KeyBinding kb) {
		for (KeyBinding keb : Minecraft.getMinecraft().gameSettings.keyBindings) {
			if (keb.equals(kb)) {
				return keb;
			}
		}
		return null;
	}

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

						bind1Alts = KeybindTracker.alternates.get(bind1.getKeyDescription());
						if (bind1Alts == null)
							bind1Alts = new boolean[] { false, false, false };

						bind2Alts = KeybindTracker.alternates.get(bind2.getKeyDescription());
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

	public static void populate() {
		KeyBinding[] keyBinds = Minecraft.getMinecraft().gameSettings.keyBindings;
		/*Field keyHandlers_Field = getDeclaredField(KeyBindingRegistry.class.getName(), "keyHandlers"); //Commented out until I'm sure that we don't need this anymore
		HashMap<KeyBinding, String> tempKeys = new HashMap<KeyBinding, String>();
		try {
			Set<KeyHandler> keyHandlers = (Set<KeyHandler>) keyHandlers_Field.get(KeyBindingRegistry.instance());
			for (KeyHandler keyhandler : keyHandlers)
				for (int i = 0; i < keyhandler.getKeyBindings().length; i++)
					tempKeys.put(keyhandler.getKeyBindings()[i], idFromObject(keyhandler));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		for (KeyBinding kb : tempKeys.keySet()) {
			String s = tempKeys.get(kb);
			if (!modKeybinds.containsKey(s))
				modKeybinds.put(s, new ArrayList<KeyBinding>());
			modKeybinds.get(s).add(kb);
		}*/
		/*
		for (int i = 32; i < keyBinds.length; i++) { //Index 31 is the last vanilla keybinding.
			if (!categoryKeybinds.containsKey(keyBinds[i].getKeyCategory())) {
				categoryKeybinds.put(keyBinds[i].getKeyCategory(), new ArrayList<KeyBinding>());
			}
			categoryKeybinds.get(keyBinds[i].getKeyCategory()).add(keyBinds[i]);
		}
		*/
		for (KeyBinding keyBinding : keyBinds) {
			if (!KeybindTracker.keybindings.containsKey(keyBinding.getKeyDescription())) {
				KeybindTracker.keybindings.put(keyBinding.getKeyDescription(), keyBinding);
			}
		}
		KeybindTracker.updateConflictCategory();
	}

	public static void updateConflictCategory() {
		KeybindTracker.conflictingKeys.clear();
		KeybindTracker.conflictingKeys.addAll(KeybindTracker.getConflictingKeybinds());
	}

	public static void registerMod(String modname, String[] keyDecriptions) {
		KeybindTracker.compatibleMods.put(modname, keyDecriptions);
	}

	public static String getExportFile() {
		JsonObject jsonObject = new JsonObject();
		JsonObject keyBindingObj;
		boolean[] modifiers;
		for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			keyBindingObj = new JsonObject();
			keyBindingObj.add("keyCode", new JsonPrimitive(keyBinding.getKeyCode()));
			modifiers = KeybindTracker.alternates.get(keyBinding.getKeyDescription());
			if (modifiers != null) {
				keyBindingObj.add("shift", new JsonPrimitive(modifiers[0]));
				keyBindingObj.add("control", new JsonPrimitive(modifiers[1]));
				keyBindingObj.add("alt", new JsonPrimitive(modifiers[2]));
			}
			jsonObject.add(keyBinding.getKeyDescription(), keyBindingObj);
		}
		return KeybindTracker.toReadableString(new Gson().toJson(jsonObject));
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
				if (KeybindTracker.keybindings.containsKey(ent.getKey())) {
					key = KeybindTracker.keybindings.get(ent.getKey());
					JsonObject keyBindingObj = ent.getValue().getAsJsonObject();
					keyCode = keyBindingObj.get("keyCode").getAsInt();
					if (keyBindingObj.has("shift")) {
						shift = keyBindingObj.get("shift").getAsBoolean();
						control = keyBindingObj.get("control").getAsBoolean();
						alt = keyBindingObj.get("alt").getAsBoolean();
					}
					if (KeybindTracker.alternates.containsKey(ent.getKey())) {
						KeybindTracker.saveKeyBinding(
								key, keyCode, new boolean[] { shift, control, alt }
						);
					}
					else {
						KeybindTracker.saveKeyBinding(key, keyCode, null);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String toReadableString(String json) {
		String readable = "";
		char[] chars = json.toCharArray();
		boolean isIteratingInString = false;
		int tabs = 0;
		for (char c : chars) {
			if (c == '}' || c == ']') {
				tabs -= 1;
				readable = KeybindTracker.addLineAndTabs(readable, tabs);
			}
			readable += c;
			if (c == '{' || c == '[') {
				tabs += 1;
				readable = KeybindTracker.addLineAndTabs(readable, tabs);
			}
			if (c == ':' && !isIteratingInString) {
				readable += " ";
			}
			if (c == '"') {
				isIteratingInString = !isIteratingInString;
			}
			if (c == ',' && !isIteratingInString) {
				readable = KeybindTracker.addLineAndTabs(readable, tabs);
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
		if (modifiers != null && KeybindTracker.alternates.containsKey(key.getKeyDescription())) {
			KeybindTracker.alternates.put(key.getKeyDescription(), modifiers);
		}
		KeyBinding.resetKeyBindingArrayAndHash();
		KeybindTracker.updateConflictCategory();
		NotEnoughKeys.saveConfig();
	}

}
