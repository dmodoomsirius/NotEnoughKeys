package okushama.notenoughkeys.keys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import okushama.notenoughkeys.NotEnoughKeys;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Keybinds {
	private Minecraft mc = Minecraft.getMinecraft();
	public static KeyBinding openConsole = new KeyBinding("Show Binds Console", Keyboard.KEY_C, "NotEnoughKeys");

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if(openConsole.isPressed() && mc.currentScreen == null) {
            Minecraft.getMinecraft().displayGuiScreen(NotEnoughKeys.console);
        }
    }
}