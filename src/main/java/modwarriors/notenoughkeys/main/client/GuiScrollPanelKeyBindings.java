package modwarriors.notenoughkeys.main.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.FMLLog;

/**
 * Created by TheTemportalist on 4/10/2016.
 *
 * @author TheTemportalist
 */
class GuiScrollPanelKeyBindings extends GuiScrollPanel<KeyBinding> {

	GuiScrollPanelKeyBindings(GuiScreen parent, KeyBinding[] elements) {
		super(parent, elements, parent.width + 45, parent.height, 20, parent.height - 32, 20);
	}

	@Override
	protected void onElementClicked(KeyBinding entry, boolean isDoubleClick, int mouseX,
			int mouseY) {

	}

	@Override
	protected void drawEntry(int index, KeyBinding entry, int x, int y, int listWidth, int slotHeight,
			int mouseX, int mouseY, boolean isSelected) {
		GlStateManager.color(1, 1, 1, 1);

		int buttonWidth = 95;
		int halfWidth = buttonWidth / 2;
		int buttonHeight = slotHeight + 4;
		int buttonX = x + buttonWidth;
		int buttonY = y;

		/** Button Description **/
		this.getParent().drawString(this.mc.fontRendererObj,
				I18n.format(entry.getKeyDescription()),
				x - buttonWidth, y + slotHeight / 4, 0xffffff
		);
		/** End **/

		/** Modifiers **/

		/** End **/

		/** KeyBinding Button **/
		isSelected = buttonX <= mouseX && mouseX < buttonX + buttonWidth &&
				buttonY <= mouseY && mouseY < buttonY + buttonHeight;
		int k = isSelected ? 2 : 1;

		this.mc.getTextureManager().bindTexture(GuiScrollPanelCategories.WIDGITS);
		this.getParent().drawTexturedModalRect(
				buttonX, buttonY,
				0, 46 + k * 20,
				halfWidth, buttonHeight
		);
		this.getParent().drawTexturedModalRect(
				buttonX + halfWidth, buttonY,
				200 - halfWidth, 46 + k * 20,
				halfWidth, buttonHeight
		);

		this.getParent().drawCenteredString(this.mc.fontRendererObj,
				GameSettings.getKeyDisplayString(entry.getKeyCode()),
				buttonX + halfWidth, buttonY + 5, 0xffffff
		);
		/** End **/

		/** Reset Button **/
		buttonX += buttonWidth + 10;
		buttonWidth = 50;
		halfWidth = buttonWidth / 2;

		isSelected = buttonX <= mouseX && mouseX < buttonX + buttonWidth &&
				buttonY <= mouseY && mouseY < buttonY + buttonHeight;
		k = entry.isSetToDefaultValue() ? 0 : (isSelected ? 2 : 1);

		this.mc.getTextureManager().bindTexture(GuiScrollPanelCategories.WIDGITS);
		this.getParent().drawTexturedModalRect(
				buttonX, buttonY,
				0, 46 + k * 20,
				halfWidth, buttonHeight
		);
		this.getParent().drawTexturedModalRect(
				buttonX + halfWidth, buttonY,
				200 - halfWidth, 46 + k * 20,
				halfWidth, buttonHeight
		);

		this.getParent().drawCenteredString(this.mc.fontRendererObj,
				"Reset",
				buttonX + halfWidth, buttonY + 5, 0xffffff
		);
		/** End **/

	}

}
