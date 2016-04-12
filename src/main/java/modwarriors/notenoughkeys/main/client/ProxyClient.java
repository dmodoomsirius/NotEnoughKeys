package modwarriors.notenoughkeys.main.client;

import modwarriors.notenoughkeys.main.common.ProxyCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TheTemportalist on 4/10/2016.
 *
 * @author TheTemportalist
 */
public class ProxyClient extends ProxyCommon {

	private HashMap<String, KeyBinding[]> keyCategories;

	@Override
	public void preInit() {

		MinecraftForge.EVENT_BUS.register(this);

		this.keyCategories = this.fetchKeyBindingCategories();

	}

	private HashMap<String, KeyBinding[]> fetchKeyBindingCategories() {
		HashMap<String, List<KeyBinding>> categoryMap = new HashMap<String, List<KeyBinding>>();
		for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			if (!categoryMap.containsKey(keyBinding.getKeyCategory()))
				categoryMap.put(keyBinding.getKeyCategory(), new ArrayList<KeyBinding>());
			categoryMap.get(keyBinding.getKeyCategory()).add(keyBinding);
		}

		HashMap<String, KeyBinding[]> categories = new HashMap<String, KeyBinding[]>();
		for (Map.Entry<String, List<KeyBinding>> entry : categoryMap.entrySet()) {
			categories.put(entry.getKey(), entry.getValue().toArray(new KeyBinding[0]));
		}

		return categories;
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		GuiScreen gui = event.getGui();
		if (gui != null && gui.getClass().equals(GuiControls.class)) {
			event.setGui(new GuiMenuNEK(this.keyCategories));
		}
	}

}
