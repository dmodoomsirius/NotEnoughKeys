package modwarriors.notenoughkeys.keys;

import modwarriors.notenoughkeys.Helper;
import modwarriors.notenoughkeys.NotEnoughKeys;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import modwarriors.notenoughkeys.gui.GuiControlsOverride;
import modwarriors.notenoughkeys.gui.GuiKeybindsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyEvents {
	private Minecraft mc = Minecraft.getMinecraft();

	/**
	 * Takes care of overriding the controls screen
	 *
	 * @param event
	 */
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		if (event.gui != null && event.gui.getClass().equals(GuiControls.class)
				&& !(event.gui instanceof GuiControlsOverride)) {
			event.gui = new GuiKeybindsMenu();
		}
	}

	@SubscribeEvent
	public void onKeyEvent(InputEvent.KeyInputEvent event) {
		this.refreshBindings(-1);
	}

	@SubscribeEvent
	public void onMouseButtonEvent(MouseEvent event) {
		if (event.button >= 0)
			// adds 100 because that's how you correct the mouse buttons overlapping key buttons
			this.refreshBindings(event.button + 100);
	}

	private void refreshBindings(int keycode) {
		boolean isInternal, isKeyboard, isSpecial;
		for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			// todo the following check will probably need adjustment. This is just a theory.
			// the goal of this is to prevent excessive looping and checking.
			if (keycode >= 0 && keyBinding.getKeyCode() != keycode) continue;
			// todo end block theory check

			isInternal = keyBinding.isKeyDown();

			isKeyboard = Helper.isKeyPressed_KeyBoard(keyBinding);
			if (!KeyHelper.alternates.containsKey(keyBinding.getKeyDescription())) {
				if (isInternal != isKeyboard) {
					this.setKeyPressed(keyBinding, isKeyboard);
				}
			}
			else {
				isSpecial = KeyHelper.isKeyBindingPressed(keyBinding);
				// in order to accomodate the ability to have a minecraft keybinding key
				// also be used in another keybinding, we should allow for keybindings to be pressed
				// but not send the key event unless the special keys are also pressed
				//if (isInternal != isSpecial) this.setKeyPressed(keyBinding, isSpecial);

				// note, removed the isSpecial check because we want to notify users that keys
				// have been released as well as pressed
				// if (isSpecial) {
				if (Minecraft.getMinecraft().currentScreen == null) {
					// Post the event!
					MinecraftForge.EVENT_BUS.post(
							new KeyBindingPressedEvent(
									keyBinding,
									KeyHelper.alternates.get(keyBinding.getKeyDescription()),
									isSpecial
							)
					);
				}
				//}
			}
		}
	}

	private void setKeyPressed(KeyBinding keyBinding, boolean isPressed) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(
					KeyBinding.class, keyBinding, isPressed, "pressed", "field_74513_e"
			);
			if (isPressed) {
				int pressTime = ObfuscationReflectionHelper.getPrivateValue(
						KeyBinding.class, keyBinding, "pressTime", "field_151474_i"
				);
				ObfuscationReflectionHelper.setPrivateValue(
						KeyBinding.class, keyBinding, pressTime + 1, "pressTime", "field_151474_i"
				);
			}
		} catch (Exception e) {
			NotEnoughKeys.logger
					.error("A key with the description \'" + keyBinding.getKeyDescription()
							+ "\' from category \'" + keyBinding.getKeyCategory()
							+ "\' and keycode \'" + keyBinding.getKeyCode()
							+ "\' could not be set from pressed state \'" + keyBinding
							.isPressed() + "\' to state \'" + isPressed
							+ "\'. This is an eror. PLEASE report this to the issues stub on github.");
			e.printStackTrace();
		}
	}

}
