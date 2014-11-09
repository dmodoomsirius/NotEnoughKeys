package modwarriors.notenoughkeys.gui;

import modwarriors.notenoughkeys.keys.KeybindTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;

public class GuiKeybindsMenu extends GuiScreen {
	public GuiKeybindsScrollPanel scroll;

	@Override
	public void initGui() {
		ArrayList<String> types = new ArrayList<String>();
		for (String modtype : KeybindTracker.compatibleMods.keySet())
			types.add(modtype);
		scroll = new GuiKeybindsScrollPanel(this, types.toArray(new String[0]));
		scroll.registerScrollButtons(7, 8);
		buttonList.add(new GuiButton(1337, width / 2 - 100, height - 28, I18n.format("gui.done")));
		KeybindTracker.updateConflictCategory();
		super.initGui();

		buttonList.add(new GuiButton(0, 0, height / 10 * 1, 75, 20, "All"));
		buttonList.add(new GuiButton(1, 0, height / 10 * 2, 75, 20, "Conflicting"));
		buttonList.add(new GuiButton(2, 0, height / 10 * 3, 75, 20, "Dump"));

	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		scroll.drawScreen(par1, par2, par3);
		drawCenteredString(mc.fontRendererObj, "Controls", width / 2, 5, 0xffffff);
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		super.actionPerformed(par1GuiButton);
		switch (par1GuiButton.id) {
			case 1337:
				Minecraft.getMinecraft().displayGuiScreen(
						new GuiOptions(null, Minecraft.getMinecraft().gameSettings)
				);
				break;
			case 0:
				Minecraft.getMinecraft().displayGuiScreen(
						new GuiControlsOverride(this, Minecraft.getMinecraft().gameSettings)
				);
				break;
			case 1:
				Minecraft.getMinecraft().displayGuiScreen(
						new GuiSubKeybindsMenu(
								this, "Conflicting",
								KeybindTracker.conflictingKeys.toArray(new String[0]),
								Minecraft.getMinecraft().gameSettings
						)
				);
				break;
			case 2:
				// todo, dump all keydesc's, keycodes, key modifiers to file
				break;
			default:
				break;
		}
		KeybindTracker.updateConflictCategory();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}
}
