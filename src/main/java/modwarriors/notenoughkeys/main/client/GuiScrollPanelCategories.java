package modwarriors.notenoughkeys.main.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

/**
 * Created by TheTemportalist on 4/10/2016.
 *
 * @author TheTemportalist
 */
public class GuiScrollPanelCategories extends GuiScrollPanel<String> {

	protected static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");

	private HashMap<String, KeyBinding[]> keyBindingsByCategory;

	GuiScrollPanelCategories(GuiScreen parent, String[] categories,
			HashMap<String, KeyBinding[]> keyBindingsByCategory) {
		super(parent, categories, parent.width + 45, parent.height, 63, parent.height - 32, 20);
		this.keyBindingsByCategory = keyBindingsByCategory;
	}

	@Override
	protected void onElementClicked(String entry, boolean isDoubleClick, int mouseX, int mouseY) {
		this.mc.displayGuiScreen(new GuiMenuKeyBindings(
				this.getParent(), entry, this.keyBindingsByCategory.get(entry)));
	}

	@Override
	protected void drawEntry(int index, String entry, int x, int y, int listWidth, int slotHeight, int mouseX,
			int mouseY, boolean isSelected) {
		this.mc.getTextureManager().bindTexture(GuiScrollPanelCategories.WIDGITS);
		GlStateManager.color(1, 1, 1, 1);

		slotHeight += 4;
		int slotWidth = 170;
		int k = isSelected ? 2 : 1;
		int halfWidth = slotWidth / 2;
		this.getParent().drawTexturedModalRect(
				x, y, 0, 46 + k * 20, halfWidth, slotHeight
		);
		this.getParent().drawTexturedModalRect(
				x + halfWidth, y, 200 - halfWidth, 46 + k * 20, halfWidth, slotHeight
		);
		this.getParent().drawCenteredString(this.mc.fontRendererObj,
				I18n.format(entry), x + halfWidth, y + 5, 0xffffff
		);

	}

}
