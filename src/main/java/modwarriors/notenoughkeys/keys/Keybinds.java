package modwarriors.notenoughkeys.keys;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class Keybinds {
	private Minecraft mc = Minecraft.getMinecraft();
	public static KeyBinding openConsole = new KeyBinding("Show Binds Console", Keyboard.KEY_C,
			"NotEnoughKeys");

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			Binds.tick();
		}
	}

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
		KeyBinding keyBinding = null;
		for (String modid : KeybindTracker.compatibleMods.keySet()) {
			for (String keyDesc : KeybindTracker.compatibleMods.get(modid)) {
				keyBinding = KeybindTracker.keybindings.get(keyDesc);

				boolean isInternal = keyBinding.getIsKeyPressed();
				//boolean isKeyboard = Helper.isKeyPressed_KeyBoard(keyBinding);
				boolean isSpecial = Helper.isSpecialKeyBindingPressed(
						keyBinding, KeybindTracker.alternates.get(keyBinding)
				);

				if (isInternal) {
					if (!isSpecial) {
						this.setKeyPressed(keyBinding, false);
					}
				}
				if (!isInternal) {
					if (isSpecial) {
						this.setKeyPressed(keyBinding, true);
					}
				}

				// Check if the keybinding is pressed WITH valid alternates
				if (Minecraft.getMinecraft().currentScreen == null && isSpecial) {
					// Post the event!
					MinecraftForge.EVENT_BUS.post(
							new KeyBindingPressedEvent(
									keyBinding,
									KeybindTracker.alternates.get(keyBinding)
							)
					);
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
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onKeyBindingPressed(KeyBindingPressedEvent event) {
		if (!event.keyBinding.getKeyDescription().equals(Keybinds.openConsole.getKeyDescription()))
			return;

		if (openConsole.isPressed() && mc.currentScreen == null) {
			Minecraft.getMinecraft().displayGuiScreen(NotEnoughKeys.console);
		}
	}

}
