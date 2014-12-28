package modwarriors.notenoughkeys.keys;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.Helper;
import modwarriors.notenoughkeys.NotEnoughKeys;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import modwarriors.notenoughkeys.gui.GuiControlsOverride;
import modwarriors.notenoughkeys.gui.GuiKeybindsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
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
		if (!Api.isLoaded()) {
			// do your stuff here, on normal basis. If mod IS loaded, use the KeyBindingPressedEvent (as shown below)
		}

		// The following stuff is the handling of keybindings.
		this.refreshBindings();
	}

	@SubscribeEvent
	public void onMouseEvent(InputEvent.MouseInputEvent event) {
		this.refreshBindings();
	}

	private void refreshBindings() {
		boolean isInternal, isKeyboard, isSpecial;
		for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			isInternal = keyBinding.isPressed();
			isKeyboard = Helper.isKeyPressed_KeyBoard(keyBinding);
			if (!KeyHelper.alternates.containsKey(keyBinding.getKeyDescription())) {
				if (isInternal != isKeyboard) {
					this.setKeyPressed(keyBinding, isKeyboard);
				}
			}
			else {
				isSpecial = Helper.isSpecialKeyBindingPressed(
						keyBinding, KeyHelper.alternates.get(keyBinding.getKeyDescription())
				);
				if (isInternal != isSpecial) {
					this.setKeyPressed(keyBinding, isSpecial);

					if (Minecraft.getMinecraft().currentScreen == null) {
						// Post the event!
						MinecraftForge.EVENT_BUS.post(
								new KeyBindingPressedEvent(
										keyBinding,
										KeyHelper.alternates.get(keyBinding.getKeyDescription())
								)
						);
					}
				}
			}
		}
	}

	private void setKeyPressed(KeyBinding keyBinding, boolean isPressed) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(
					KeyBinding.class, keyBinding, isPressed, "pressed", "field_74513_e"
			);
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

	@SubscribeEvent
	public void onKeyBindingPressed(KeyBindingPressedEvent event) {
		/*
		if (!event.keyBinding.getKeyDescription().equals("keyBinding.getKeyDescription()"))
			return;

		if (keyBinding.getIsKeyPressed()) {
			// Do action
		}
		*/
	}

}
