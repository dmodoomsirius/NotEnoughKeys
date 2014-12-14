package modwarriors.notenoughkeys.gui;

import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Mostly original code from Minecraft Forge over at http://minecraftforge.net
 */
public class GuiKeybindsScrollPanel extends GuiSlot {
	protected static final ResourceLocation WIDGITS = new ResourceLocation(
			"textures/gui/widgets.png");
	private GuiKeybindsMenu controls;
	private String[] message, buttonNames;
	private int _mouseX, _mouseY, selected = -1;

	public GuiKeybindsScrollPanel(GuiKeybindsMenu cntrls, String[] buttons) {
		super(Minecraft.getMinecraft(), cntrls.width, cntrls.height, 16, (cntrls.height - 32) + 4,
				25);
		controls = cntrls;
		buttonNames = buttons;
	}

	@Override
	protected int getSize() {
		return buttonNames.length;
		// return options.keyBindings.length;
	}

	@Override
	protected void elementClicked(int i, boolean flag, int mouseX, int mouseZ) {
		if (!flag) {
			if (selected == -1)
				selected = i;
			String type = buttonNames[selected];
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord
					.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
			Minecraft.getMinecraft().displayGuiScreen(
					new GuiSubKeybindsMenu(
							controls, type,
							KeyHelper.compatibleMods.get(type),
							Minecraft.getMinecraft().gameSettings
					)
			);
			KeyHelper.updateConflictCategory();
			selected = -1;
		}
	}

	@Override
	protected boolean isSelected(int i) {
		return false;
	}

	@Override
	protected void drawBackground() {
	}

    @Override
    protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int p_180791_5_, int p_180791_6_) {

    }

    @Override
	public void drawScreen(int mX, int mY, float f) {
		_mouseX = mX;
		_mouseY = mY;
		super.drawScreen(mX, mY, f);
	}


	protected void drawSlot(int index, int xPosition, int yPosition, int l, Tessellator tessellator,
			int mouseX, int mouseY) {
		String s = I18n.format(buttonNames[index]);
		int width = 70, height = 20;
		xPosition += 10;
		boolean flag = _mouseX >= xPosition && _mouseY >= yPosition && _mouseX < xPosition + width
				&& _mouseY < yPosition + height;
		int k = (flag ? 2 : 1);

		Minecraft.getMinecraft().renderEngine.bindTexture(WIDGITS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		controls.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
		controls.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2,
				46 + k * 20, width / 2, height);

		controls.drawString(Minecraft.getMinecraft().fontRendererObj, s, xPosition + width + 4,
				yPosition + 6, 0xFFFFFFFF);

		controls.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "Configure",
				xPosition + (width / 2), yPosition + (height - 8) / 2, 0xFFFFFFFF);
	}

	public boolean keyTyped(char c, int i) {
		if (selected != -1) {
			KeyHelper.updateConflictCategory();
			selected = -1;
			return false;
		}
		return true;
	}
}
