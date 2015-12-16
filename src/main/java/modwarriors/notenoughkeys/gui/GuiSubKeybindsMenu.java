package modwarriors.notenoughkeys.gui;

import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import java.io.IOException;

public class GuiSubKeybindsMenu extends GuiScreen {
	/**
	 * A reference to the screen object that created  Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;

	/**
	 * The title string that is displayed in the top-center of the screen.
	 */
	protected String screenTitle = "Controls";

	/**
	 * Reference to the GameSettings object.
	 */
	private GameSettings options;

	/**
	 * The ID of the button that has been pressed.
	 */
	private int buttonId = -1;

	private GuiSubKeybindsScrollPanel scrollPane;

	public String subModID = "Misc";

	public KeyBinding[] keyBindings = { };

	public GuiSubKeybindsMenu(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
		parentScreen = par1GuiScreen;
		options = par2GameSettings;
	}

	public GuiSubKeybindsMenu(GuiScreen parent, String id, String[] kbs,
			GameSettings gameSettings) {
		this(parent, gameSettings);
		subModID = id;
		keyBindings = new KeyBinding[kbs.length];
		for (int i = 0; i < kbs.length; i++)
			keyBindings[i] = KeyHelper.keybindings.get(kbs[i]);
	}

	/**
	 * Gets the distance from the left border of the window to left border of
	 * the controls screen
	 */
	private int getLeftBorder() {
		return width / 2 - 155;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	public void initGui() {
		scrollPane = new GuiSubKeybindsScrollPanel(this, options, mc, keyBindings);
		buttonList.add(new GuiButton(200, width / 2 - 100, height - 28, I18n.format("gui.done")));
		scrollPane.registerScrollButtons(7, 8);
		screenTitle = subModID + " " + I18n.format("controls.title");
		KeyHelper.updateConflictCategory();
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 200) {
			mc.displayGuiScreen(parentScreen);
		}
	}

	/**
	 * Called when the mouse is clicked.
	 */
	protected void mouseClicked(int par1, int par2, int par3) {
		KeyHelper.updateConflictCategory();
        try {
            super.mouseClicked(par1, par2, par3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2) {
		KeyHelper.updateConflictCategory();
		if (scrollPane.keyTyped(par1, par2)) {
            try {
                super.keyTyped(par1, par2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		scrollPane.drawScreen(par1, par2, par3);
		drawCenteredString(fontRendererObj, screenTitle, width / 2, 4, 0xffffff);
		super.drawScreen(par1, par2, par3);
	}
}
