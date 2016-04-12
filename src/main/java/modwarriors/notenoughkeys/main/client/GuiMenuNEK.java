package modwarriors.notenoughkeys.main.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by TheTemportalist on 4/10/2016.
 *
 * @author TheTemportalist
 */
public class GuiMenuNEK extends GuiScrollContainer<String> {

	private Runnable[] sidebarActions;

	private String[] keyBindingCategories;
	private HashMap<String, KeyBinding[]> keyBindingsByCategory;

	GuiMenuNEK(HashMap<String, KeyBinding[]> keyBindingCategories) {
		this.keyBindingCategories = keyBindingCategories.keySet().toArray(new String[0]);
		this.keyBindingsByCategory = keyBindingCategories;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.initSidebar();
		this.buttonList.add(new GuiButton(1337, width / 2 - 100, height - 28, I18n.format("gui.done")));
	}

	private void initSidebar() {
		this.initSidebarActions();

		int boxTop = 63;
		int boxBottom = this.height - 32;
		int labelHeight = 20;
		int centerY = (boxTop + boxBottom) / 2;
		int spread = 10;

		String[] sidebarLabels = new String[] {"Conflicts", "Export", "Import"};

		int startY = centerY;
		startY -= (labelHeight * sidebarLabels.length) / 2;
		startY -= (spread * (sidebarLabels.length - 1)) / 2;

		for (int i = 0; i < sidebarLabels.length; i++) {
			this.buttonList.add(new GuiButton(i, 0, startY + (labelHeight + spread) * i, 75, labelHeight, sidebarLabels[i]));
		}

	}

	private void initSidebarActions() {
		this.sidebarActions = new Runnable[]{
				new Runnable() {
					@Override public void run() {
						onClick_Conflicts();
					}
				},
				new Runnable() {
					@Override public void run() {
						onClick_Export();
					}
				},
				new Runnable() {
					@Override public void run() {
						onClick_Import();
					}
				}
		};
	}

	private void onClick_Conflicts() {

	}

	private void onClick_Export() {

	}

	private void onClick_Import() {

	}

	@Override
	protected GuiScrollPanel<String> createScrollPanel() {
		return new GuiScrollPanelCategories(this, this.keyBindingCategories, this.keyBindingsByCategory);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id < this.sidebarActions.length)
			this.sidebarActions[button.id].run();
		else if (button.id == 1337)
			this.mc.displayGuiScreen(new GuiOptions(null, this.mc.gameSettings));
	}

	@Override
	protected void drawScreenPre(int mouseX, int mouseY, float partialTicks) {
		this.drawCenteredString(this.fontRendererObj, "Controls", this.width / 2, 5, 0xffffff);
	}

}
