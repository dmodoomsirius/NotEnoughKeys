package modwarriors.notenoughkeys.keys;

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
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;

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
		if (event.gui instanceof GuiControls && !(event.gui instanceof GuiControlsOverride)) {
			event.gui = new GuiKeybindsMenu();
		}
	}

	@SubscribeEvent
	public void onKeyEvent(InputEvent.KeyInputEvent event) {
		if (!Api.isLoaded()) {
			// do your stuff here, on normal basis. If mod IS loaded, use the KeyBindingPressedEvent (as shown below)
		}

		// The following stuff is the handling of keybindings.
		// Iterate through all alternates (the shift ctrl alt)
		for (KeyBinding keyBinding : KeybindTracker.alternates.keySet()) {

			boolean isPressed = false;
			if (
					keyBinding.getKeyCode() < 0
							?
							Mouse.isButtonDown(keyBinding.getKeyCode() + 100)
							:
							Keyboard.isKeyDown(keyBinding.getKeyCode())
					) {
				isPressed = true;
			}
			if (keyBinding.getIsKeyPressed() != isPressed) {
				try {
					Field pressed = KeyBinding.class.getDeclaredField("pressed");
					pressed.setAccessible(true);
					pressed.setBoolean(keyBinding, isPressed);
					//NotEnoughKeys.logger.info("Set field 'pressed' for keybinding '" + keyBinding.getKeyDescription() + "' to true.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Check if the keybinding is pressed WITH valid alternates
			if (Helper.isSpecialKeyBindingPressed(keyBinding,
					KeybindTracker.alternates.get(keyBinding))) {
				// Post the event!
				MinecraftForge.EVENT_BUS.post(
						new KeyBindingPressedEvent(
								keyBinding,
								KeybindTracker.alternates.get(keyBinding)
						)
				);
				// Only 1 keybinding please!
				break;
			}
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
