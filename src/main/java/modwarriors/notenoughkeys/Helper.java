package modwarriors.notenoughkeys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author TheTemportalist
 */
public class Helper {

	/*
	 * The true ctrl key(29 on mac) on max is used for right clicking
	 */

	private static final int shift1 = 42;
	private static final int shift2 = 54;
	private static final int ctrl1 = Minecraft.isRunningOnMac ? 219 : 29;
	private static final int ctrl2 = Minecraft.isRunningOnMac ? 220 : 157;
	private static final int alt1 = 56;
	private static final int alt2 = 184;

	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(Helper.shift1) || Keyboard.isKeyDown(Helper.shift2);
	}

	public static boolean isCtrlKeyDown() {
		return Keyboard.isKeyDown(Helper.ctrl1) || Keyboard.isKeyDown(Helper.ctrl2);
	}

	public static boolean isAltKeyDown() {
		return Keyboard.isKeyDown(Helper.alt1) || Keyboard.isKeyDown(Helper.alt2);
	}

	public static boolean isShiftKey(int keyCode) {
		return keyCode == Helper.shift1 || keyCode == Helper.shift2;
	}

	public static boolean isCtrlKey(int keyCode) {
		return keyCode == Helper.ctrl1 || keyCode == Helper.ctrl2;
	}

	public static boolean isAltKey(int keyCode) {
		return keyCode == Helper.alt1 || keyCode == Helper.alt2;
	}

	public static boolean isSpecialKey(int keyCode) {
		return Helper.isShiftKey(keyCode) || Helper.isCtrlKey(keyCode) || Helper.isAltKey(keyCode);
	}

	public static boolean isKeyPressed_KeyBoard(KeyBinding keyBinding) {
		return
				keyBinding.getKeyCode() >= 0
						?
						Keyboard.isKeyDown(keyBinding.getKeyCode())
						:
						Mouse.isButtonDown(keyBinding.getKeyCode() + 100)
				;
	}

	public static boolean isSpecialKeyBindingPressed(KeyBinding keyBinding, boolean[] alts) {
		/*
		if (keyBinding.getIsKeyPressed()) {
			NotEnoughKeys.logger.info("Valid Shift: " + (!alts[0] || Helper.isShiftKeyDown()));
			NotEnoughKeys.logger.info("Valid Ctrl:  " + (!alts[1] || Helper.isCtrlKeyDown()));
			NotEnoughKeys.logger.info("Valid Alt:   " + (!alts[2] || Helper.isAltKeyDown()));
		}
		*/
		/*
		NotEnoughKeys.logger.info(keyBinding.getKeyDescription() + " should sht: " + alts[0]);
		NotEnoughKeys.logger.info(keyBinding.getKeyDescription() + " should ctl: " + alts[1]);
		NotEnoughKeys.logger.info(keyBinding.getKeyDescription() + " should alt: " + alts[2]);
		*/
		return Helper.isKeyPressed_KeyBoard(keyBinding) &&
				(alts[0] ? Helper.isShiftKeyDown() : !Helper.isShiftKeyDown()) &&
				(alts[1] ? Helper.isCtrlKeyDown() : !Helper.isCtrlKeyDown()) &&
				(alts[2] ? Helper.isAltKeyDown() : !Helper.isAltKeyDown());
	}

}
