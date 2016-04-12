package modwarriors.notenoughkeys.main.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;
import scala.Tuple2;

import java.io.IOException;

/**
 * Created by TheTemportalist on 4/10/2016.
 *
 * @author TheTemportalist
 */
class GuiScrollPanelKeyBindings extends GuiScrollPanel<KeyBinding> {

	private int selectedIndex = -1;
	private GuiButton[] keyButtons;
	private GuiButton[] resetButtons;

	GuiScrollPanelKeyBindings(GuiScreen parent, KeyBinding[] elements) {
		super(parent, elements, parent.width, parent.height, 20, parent.height - 32, 20);

		this.keyButtons = new GuiButton[this.getSize()];
		this.resetButtons = new GuiButton[this.getSize()];
		for (int i = 0; i < this.getSize(); i++) {
			this.keyButtons[i] = new GuiButtonTypedIndex(0, i, "");
			this.resetButtons[i] = new GuiButtonTypedIndex(1, i, I18n.format("controls.reset"));
		}

	}

	@Override
	protected boolean mousePressed(int index, KeyBinding entry, int mouseX, int mouseY,
			int mouseEvent, int relativeX, int relativeY) {
		//NotEnoughKeys.log(index + "");
		if (this.keyButtons[index].mousePressed(this.mc, mouseX, mouseY)) {
			//NotEnoughKeys.log(index + " key");
			this.selectedIndex = index;
			return true;
		}
		else if (this.resetButtons[index].mousePressed(this.mc, mouseX, mouseY)) {
			//NotEnoughKeys.log(index + " reset");
			entry.setToDefault();
			this.mc.gameSettings.setOptionKeyBinding(entry, entry.getKeyCodeDefault());
			KeyBinding.resetKeyBindingArrayAndHash();
			BindingHelper.setModifiers(entry, false, false, false);
			return true;
		}
		return false;
	}

	@Override
	protected void onElementClicked(int index, KeyBinding entry, boolean isDoubleClick, int mouseX,
			int mouseY) {
		/*
		NotEnoughKeys.log(entry.getKeyDescription());
		this.selectedIndex = index;
		*/
	}

	@Override
	protected boolean keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.selectedIndex >= 0 && !BindingHelper.isModifierKeyCode(keyCode)) {
			KeyBinding keyBinding = this.getElement(this.selectedIndex);

			if (keyCode == Keyboard.KEY_ESCAPE) {
				keyCode = 0;
			}
			else if (keyCode == 0 && typedChar > 0) {
				keyCode = typedChar + 256;
			}

			keyBinding.setKeyModifierAndCode(KeyModifier.NONE, keyCode);
			this.mc.gameSettings.setOptionKeyBinding(keyBinding, keyCode);
			KeyBinding.resetKeyBindingArrayAndHash();
			if (keyCode != 0)
				BindingHelper.setModifiers(keyBinding);
			else
				BindingHelper.setModifiers(keyBinding, false, false, false);

			this.selectedIndex = -1;
			return true;
		}
		return super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void drawEntry(int index, KeyBinding entry, int x, int y, int listWidth, int slotHeight,
			int mouseX, int mouseY, boolean isSelected) {
		GlStateManager.color(1, 1, 1, 1);

		//this.getParent().drawString(this.mc.fontRendererObj, "t", x, y, 0x00ff00);

		slotHeight += 4;
		int textHeight = y + slotHeight / 5 * 2;

		/** Button Description **/
		{
			this.getParent().drawString(this.mc.fontRendererObj,
					I18n.format(entry.getKeyDescription()),
					x, textHeight, 0xffffff
			);
		}

		/** Reset Button **/
		int resetWidth = 50;
		{
			this.mc.getTextureManager().bindTexture(GuiScrollPanelCategories.WIDGITS);
			this.resetButtons[index].xPosition = x + listWidth - resetWidth - 10;
			this.resetButtons[index].yPosition = y;
			this.resetButtons[index].width = resetWidth;
			this.resetButtons[index].height = slotHeight;
			this.resetButtons[index].enabled = !entry.isSetToDefaultValue();
			this.resetButtons[index].drawButton(this.mc, mouseX, mouseY);
		}

		/** KeyBinding Button **/
		int keyWidth = 95;
		{
			this.mc.getTextureManager().bindTexture(GuiScrollPanelCategories.WIDGITS);
			this.keyButtons[index].xPosition = this.resetButtons[index].xPosition - 10 - keyWidth;
			this.keyButtons[index].yPosition = y;
			this.keyButtons[index].width = keyWidth;
			this.keyButtons[index].height = slotHeight;
			this.keyButtons[index].displayString = this.getButtonName(index, entry);
			this.keyButtons[index].drawButton(this.mc, mouseX, mouseY);
		}
		/** End **/

		/** Modifiers **/
		Tuple2<String, Boolean>[] modifiers = new Tuple2[]{
				new Tuple2<String, Boolean>("Ctrl", BindingHelper.hasModifierCtrl(entry)),
				new Tuple2<String, Boolean>("Alt", BindingHelper.hasModifierAlt(entry)),
				new Tuple2<String, Boolean>("Shift", BindingHelper.hasModifierShift(entry))
		};

		for (int i = 0; i < modifiers.length; i++) {
			if (modifiers[i]._2)
				this.getParent().drawCenteredString(this.mc.fontRendererObj,
						modifiers[i]._1,
						this.keyButtons[index].xPosition - keyWidth + 10 + (30 * i),
						textHeight, 0xffffff);
		}
		/** End **/

	}

	private String getButtonName(int index, KeyBinding keyBinding) {
		String displayString = GameSettings.getKeyDisplayString(keyBinding.getKeyCode());
		if (this.selectedIndex == index) { // if is changing
			return TextFormatting.WHITE + "> " + TextFormatting.YELLOW + displayString + TextFormatting.WHITE + " <";
		}
		else {
			TextFormatting prefix = null;
			if (BindingHelper.isConflictingKey(keyBinding))
				prefix = TextFormatting.RED;
			else if (BindingHelper.isConflictingModifier(keyBinding))
				prefix = TextFormatting.GOLD;
			return (prefix != null ? prefix : "") + displayString;
		}
	}

	private class GuiButtonTypedIndex extends GuiButton {

		private final int type, index;

		GuiButtonTypedIndex(int type, int index, String content) {
			super(10 + (2 * index) + type, 0, 0, content);
			this.type = type;
			this.index = index;
		}

	}

}
