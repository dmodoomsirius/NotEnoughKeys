package okushama.notenoughkeys.keys;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import okushama.notenoughkeys.Helper;
import okushama.notenoughkeys.NotEnoughKeys;
import okushama.notenoughkeys.api.Api;
import okushama.notenoughkeys.api.KeyBindingPressedEvent;
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
		for (KeyBinding keyBinding : KeybindTracker.alternates.keySet()) {
			if (Helper.isSpecialKeyBindingPressed(
					keyBinding,
					KeybindTracker.alternates.get(keyBinding)
			)
					) {
				MinecraftForge.EVENT_BUS.post(
						new KeyBindingPressedEvent(
								keyBinding,
								KeybindTracker.alternates.get(keyBinding)
						)
				);
				NotEnoughKeys.logger.info("posted");
			}
		}

	}

	// TODO, event is posting but not recieving

	@SubscribeEvent
	public void onKeyBindingPressed(KeyBindingPressedEvent event) {
		NotEnoughKeys.logger.info("recieved");
		if (!event.keyBinding.getKeyDescription().equals(Keybinds.openConsole.getKeyDescription()))
			return;

		if (openConsole.isPressed() && mc.currentScreen == null) {
			Minecraft.getMinecraft().displayGuiScreen(NotEnoughKeys.console);
		}
	}

}
