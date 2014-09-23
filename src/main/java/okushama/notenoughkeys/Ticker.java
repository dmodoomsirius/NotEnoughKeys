package okushama.notenoughkeys;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import okushama.notenoughkeys.gui.GuiControlsOverride;
import okushama.notenoughkeys.gui.GuiKeybindsMenu;
import okushama.notenoughkeys.keys.Binds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;

public class Ticker {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            Binds.tick();
            if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiControls && !(Minecraft.getMinecraft().currentScreen instanceof GuiControlsOverride)) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiKeybindsMenu());
                NotEnoughKeys.logger.info("Replaced the instance of Minecraft controls gui!");
            }
        }
    }
}