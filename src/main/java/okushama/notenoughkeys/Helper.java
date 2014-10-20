package okushama.notenoughkeys;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * @author CountryGamer
 */
@SideOnly(Side.CLIENT)
public class Helper {

	private static final int shift1 = 42;
	private static final int shift2 = 54;
	private static final int ctrl1 = Minecraft.isRunningOnMac ? 219 : 29;
	private static final int ctrl2 = Minecraft.isRunningOnMac ? 220 : 157;
	private static final int ctrlMac = Minecraft.isRunningOnMac ? 29 : -100;
	private static final int alt1 = 56;
	private static final int alt2 = 184;

	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(Helper.shift1) || Keyboard.isKeyDown(Helper.shift2);
	}

	public static boolean isCtrlKeyDown() {
		return Keyboard.isKeyDown(Helper.ctrl1) || Keyboard.isKeyDown(Helper.ctrl2) ||
				Keyboard.isKeyDown(Helper.ctrlMac);
	}

	public static boolean isAltKeyDown() {
		return Keyboard.isKeyDown(Helper.alt1) || Keyboard.isKeyDown(Helper.alt2);
	}

	public static boolean isShiftKey(int keyCode) {
		return keyCode == Helper.shift1 || keyCode == Helper.shift2;
	}

	public static boolean isCtrlKey(int keyCode) {
		return keyCode == Helper.ctrl1 || keyCode == Helper.ctrl2 || keyCode == Helper.ctrlMac;
	}

	public static boolean isAltKey(int keyCode) {
		return keyCode == Helper.alt1 || keyCode == Helper.alt2;
	}

	public static boolean isSpecialKey(int keyCode) {
		return Helper.isShiftKey(keyCode) || Helper.isCtrlKey(keyCode) || Helper.isAltKey(keyCode);
	}

	public static boolean isSpecialKeyBindingPressed(KeyBinding keyBinding, boolean[] alts) {
		return keyBinding.getIsKeyPressed() &&
				(!alts[0] || Helper.isShiftKeyDown()) &&
				(!alts[1] || Helper.isCtrlKeyDown()) &&
				(!alts[2] || Helper.isAltKeyDown());
	}

}
