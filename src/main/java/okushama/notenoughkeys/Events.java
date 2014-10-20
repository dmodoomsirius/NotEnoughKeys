package okushama.notenoughkeys;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.gui.GuiControls;
import net.minecraftforge.client.event.GuiOpenEvent;
import okushama.notenoughkeys.gui.GuiControlsOverride;
import okushama.notenoughkeys.gui.GuiKeybindsMenu;
import okushama.notenoughkeys.keys.Binds;

public class Events {

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

}
