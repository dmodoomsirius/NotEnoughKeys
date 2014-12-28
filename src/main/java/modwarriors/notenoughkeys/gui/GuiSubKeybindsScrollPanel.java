package modwarriors.notenoughkeys.gui;

import modwarriors.notenoughkeys.Helper;
import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * Original code from Minecraft Forge over at http://minecraftforge.net
 */
public class GuiSubKeybindsScrollPanel extends GuiSlot {
	protected static final ResourceLocation WIDGITS = new ResourceLocation(
			"textures/gui/widgets.png");
	private GuiSubKeybindsMenu controls;
	private GameSettings options;
	private Minecraft mc;
	private String[] message;
	private int _mouseX, _mouseY, selected = -1;

	public KeyBinding[] keyBindings;

	public GuiSubKeybindsScrollPanel(GuiSubKeybindsMenu controls, GameSettings options,
			Minecraft mc, KeyBinding[] kbs) {
		super(mc, controls.width, controls.height, 16, (controls.height - 32) + 4, 25);
		this.controls = controls;
		this.options = options;
		this.mc = mc;
		keyBindings = kbs;
	}

	@Override
	protected int getSize() {
		return keyBindings.length;
	}

	@Override
	protected void elementClicked(int i, boolean doubleClick, int mouseX, int mouseY) {
		if (!doubleClick) {
			if (selected == -1) {
				selected = i;
			}
			else {
				KeyBinding glob = getGlobalKeybind(selected);
				this.saveKeyBinding(glob, -100);
			}
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
	public void drawScreen(int mX, int mY, float f) {
		_mouseX = mX;
		_mouseY = mY;

		if (selected != -1 && !Mouse.isButtonDown(0) && Mouse.getDWheel() == 0) {
			if (Mouse.next() && Mouse.getEventButtonState()) {
				KeyBinding glob = getGlobalKeybind(selected);
				this.saveKeyBinding(glob, -100 + Mouse.getEventButton());
			}
		}
		super.drawScreen(mX, mY, f);
	}

	@Override
	protected void drawSlot(int index, int xPosition, int yPosition, int l,
			int mouseX, int mouseY) {
		if (keyBindings[index] == null)
			return;

		String s = I18n.format(keyBindings[index].getKeyDescription());
		int width = 70;
		int height = 20;
		xPosition -= 20;
		boolean flag = _mouseX >= xPosition && _mouseY >= yPosition && _mouseX < xPosition + width
				&& _mouseY < yPosition + height;
		int k = (flag ? 2 : 1);

		mc.renderEngine.bindTexture(WIDGITS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		controls.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
		controls.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2,
				46 + k * 20, width / 2, height);

		controls.drawString(mc.fontRendererObj, s, xPosition + width + 4, yPosition + 6,
				0xFFFFFFFF);

		this.drawAltStrings(xPosition, yPosition, keyBindings[index]);

		boolean conflict = false;
		KeyBinding globKb = getGlobalKeybind(index);
		conflict = KeyHelper.conflictingKeys.contains(globKb);
		/*
		for (KeyBinding x : options.keyBindings) {
			if (x != globKb && x.getKeyCode() == globKb.getKeyCode()) {
				conflict = true;
				break;
			}
		}
		*/

		String str = (conflict ? EnumChatFormatting.RED : "") + GameSettings.getKeyDisplayString(
				keyBindings[index].getKeyCode());
		str = (index == selected ?
				EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + "??? "
						+ EnumChatFormatting.WHITE + "<" :
				str);
		controls.drawCenteredString(mc.fontRendererObj, str, xPosition + (width / 2),
				yPosition + (height - 8) / 2, 0xFFFFFFFF);
	}

	private void drawAltStrings(int slotX, int slotY, KeyBinding keyBinding) {
		float scale = 0.5F;
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, 1.0F);

		boolean[] modifiers = KeyHelper.alternates.get(keyBinding.getKeyDescription());
		if (modifiers != null) {
			if (modifiers[0]) {
				this.drawAltString(
						"SHIFT",
						(int) ((1 / scale) * (slotX - 4)),
						(int) ((1 / scale) * (slotY + 2)) + 3
				);
			}
			if (modifiers[1]) {
				this.drawAltString(
						"CTRL",
						(int) ((1 / scale) * (slotX - 4)),
						(int) ((1 / scale) * (slotY + 2)) + 13
				);
			}
			if (modifiers[2]) {
				this.drawAltString(
						"ALT",
						(int) ((1 / scale) * (slotX - 4)),
						(int) ((1 / scale) * (slotY + 2)) + 23
				);
			}
		}

		GL11.glPopMatrix();
	}

	private void drawAltString(String altType, int x, int y) {
		this.controls.drawString(
				this.mc.fontRendererObj, altType,
				x - this.mc.fontRendererObj.getStringWidth(altType), y, 0xFFFFFFFF
		);
	}

	public KeyBinding getGlobalKeybind(int localIndex) {
		if (localIndex < 0)
			return null;
		return KeyHelper.getKeybind(keyBindings[localIndex]);
	}

	public boolean keyTyped(char c, int keycode) {
		if (selected != -1) {
			KeyBinding keyBinding = getGlobalKeybind(selected);
			boolean isCompatible = KeyHelper.alternates.containsKey(
					keyBinding.getKeyDescription()
			);
			boolean isSpecialKey = Helper.isSpecialKey(keycode);
			if (!isCompatible || !isSpecialKey) {
				this.saveKeyBinding(
						keyBinding, keycode == Keyboard.KEY_ESCAPE ? Keyboard.KEY_NONE : keycode
				);
				return false;
			}
		}
		return true;
	}

	private void saveKeyBinding(KeyBinding key, int keycode) {
		KeyHelper.saveKeyBinding(key, keycode, new boolean[] {
				Helper.isShiftKeyDown(), Helper.isCtrlKeyDown(), Helper.isAltKeyDown()
		});
		selected = -1;
	}

}
