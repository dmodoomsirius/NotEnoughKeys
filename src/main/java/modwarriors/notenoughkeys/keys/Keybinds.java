package modwarriors.notenoughkeys.keys;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.Helper;
import modwarriors.notenoughkeys.NotEnoughKeys;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class Keybinds {
	private Minecraft mc = Minecraft.getMinecraft();
	public static KeyBinding openConsole = new KeyBinding("Show Binds Console", Keyboard.KEY_C,
			"NotEnoughKeys");

	@SubscribeEvent
	public void onKeyEvent(InputEvent.KeyInputEvent event) {
		if (!Api.isLoaded()) {
			// do your stuff here, on normal basis. If mod IS loaded, use the KeyBindingPressedEvent (as shown below)
		}

		// The following stuff is the handling of keybindings.
		// Iterate through all alternates (the shift ctrl alt)
		for (KeyBinding keyBinding : KeybindTracker.alternates.keySet()) {
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
