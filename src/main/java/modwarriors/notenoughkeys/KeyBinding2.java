package modwarriors.notenoughkeys;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

import java.util.*;

@SideOnly(Side.CLIENT)
public class KeyBinding2 implements Comparable {
	private static final List keybindArray = new ArrayList();
	private static final ArrayList<KeyBinding2> hash = new ArrayList<KeyBinding2>();
	private static final Set keybindSet = new HashSet();
	private final String keyDescription;
	private final int keyCodeDefault;
	private final String keyCategory;
	private int keyCode;
	/**
	 * because _303 wanted me to call it that(Caironater)
	 */
	private boolean pressed;
	private int pressTime;
	private static final String __OBFID = "CL_00000628";

	public static void onTick(int p_74507_0_) {
		if (p_74507_0_ != 0) {
			for (KeyBinding2 keybinding : KeyBinding2.getKeyBindingsWithKey(p_74507_0_)) {
				if (keybinding != null) {
					++keybinding.pressTime;
				}
			}
		}
	}

	public static void setKeyBindState(int p_74510_0_, boolean p_74510_1_) {
		if (p_74510_0_ != 0) {
			for (KeyBinding2 keybinding : KeyBinding2.getKeyBindingsWithKey(p_74510_0_)) {
				if (keybinding != null) {
					keybinding.pressed = p_74510_1_;
				}
			}
		}
	}

	public static void unPressAllKeys() {
		Iterator iterator = keybindArray.iterator();

		while (iterator.hasNext()) {
			KeyBinding2 keybinding = (KeyBinding2) iterator.next();
			keybinding.unpressKey();
		}
	}

	public static void resetKeyBindingArrayAndHash() {
		hash.clear();
		Iterator iterator = keybindArray.iterator();

		while (iterator.hasNext()) {
			KeyBinding2 keybinding = (KeyBinding2) iterator.next();
			hash.add(keybinding);
		}
	}

	public static Set getKeybinds() {
		return keybindSet;
	}

	public KeyBinding2(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
		this.keyDescription = p_i45001_1_;
		this.keyCode = p_i45001_2_;
		this.keyCodeDefault = p_i45001_2_;
		this.keyCategory = p_i45001_3_;
		keybindArray.add(this);
		hash.add(this);
		keybindSet.add(p_i45001_3_);
	}

	public boolean getIsKeyPressed() {
		return this.pressed;
	}

	public String getKeyCategory() {
		return this.keyCategory;
	}

	public boolean isPressed() {
		if (this.pressTime == 0) {
			return false;
		}
		else {
			--this.pressTime;
			return true;
		}
	}

	private void unpressKey() {
		this.pressTime = 0;
		this.pressed = false;
	}

	public String getKeyDescription() {
		return this.keyDescription;
	}

	public int getKeyCodeDefault() {
		return this.keyCodeDefault;
	}

	public int getKeyCode() {
		return this.keyCode;
	}

	public void setKeyCode(int p_151462_1_) {
		this.keyCode = p_151462_1_;
	}

	public int compareTo(KeyBinding2 p_compareTo_1_) {
		int i = I18n.format(this.keyCategory, new Object[0])
				.compareTo(I18n.format(p_compareTo_1_.keyCategory, new Object[0]));

		if (i == 0) {
			i = I18n.format(this.keyDescription, new Object[0])
					.compareTo(I18n.format(p_compareTo_1_.keyDescription, new Object[0]));
		}

		return i;
	}

	public int compareTo(Object p_compareTo_1_) {
		return this.compareTo((KeyBinding2) p_compareTo_1_);
	}

	private static KeyBinding2[] getKeyBindingsWithKey(int keyCode) {
		ArrayList<KeyBinding2> validKeys = new ArrayList<KeyBinding2>();
		for (KeyBinding2 keyBinding : KeyBinding2.hash) {
			if (keyBinding.getKeyCode() == keyCode) {
				validKeys.add(keyBinding);
			}
		}
		return validKeys.toArray(new KeyBinding2[0]);
	}

}
