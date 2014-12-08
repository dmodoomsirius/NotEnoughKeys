package modwarriors.notenoughkeys.gui;

import com.google.common.io.Files;
import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GuiKeybindsMenu extends GuiScreen {
	public GuiKeybindsScrollPanel scroll;

	@Override
	public void initGui() {
		ArrayList<String> types = new ArrayList<String>();
		for (String modtype : KeyHelper.compatibleMods.keySet())
			types.add(modtype);
		scroll = new GuiKeybindsScrollPanel(this, types.toArray(new String[0]));
		scroll.registerScrollButtons(7, 8);
		buttonList.add(new GuiButton(1337, width / 2 - 100, height - 28, I18n.format("gui.done")));
		KeyHelper.updateConflictCategory();
		super.initGui();

		int yVar = height / 10;
		this.buttonList.add(new GuiButton(0, 0, yVar * 1, 75, 20, "All"));
		this.buttonList.add(new GuiButton(1, 0, yVar * 2, 75, 20, "Conflicting"));
		this.buttonList.add(new GuiButton(2, 0, yVar * 5, 75, 20, "Export"));
		this.buttonList.add(new GuiButton(3, 0, yVar * 6, 75, 20, "Import"));

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
			case 0: // all
				Minecraft.getMinecraft().displayGuiScreen(
						new GuiControlsOverride(this, Minecraft.getMinecraft().gameSettings)
				);
				break;
			case 1: // conflicting
				KeyHelper.updateConflictCategory();
				Minecraft.getMinecraft().displayGuiScreen(
						new GuiSubKeybindsMenu(
								this, "Conflicting",
								KeyHelper.conflictingKeys.toArray(new String[0]),
								Minecraft.getMinecraft().gameSettings
						)
				);
				break;
			case 2: // export
				try {
					/*
					Files.write(
							new File(
									Minecraft.getMinecraft().mcDataDir,
									"NotEnoughKeys" + new SimpleDateFormat(
											"_MM-dd-yyyy_HH-mm-ss"
									).format(new Date()) + ".json"
							).toPath(),
							KeyHelper.getExportFile().getBytes(StandardCharsets.UTF_8),
							StandardOpenOption.CREATE
					);
					*/
					Files.write(
							KeyHelper.getExportFile().getBytes(),
							new File(
									Minecraft.getMinecraft().mcDataDir,
									"NotEnoughKeys_" + new SimpleDateFormat(
											"MM-dd-yyyy_HH-mm-ss"
									).format(new Date()) + ".json"
							)
					);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 3: // import
				// todo, make this better! I know this is terrible -Temp.
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(Minecraft.getMinecraft().mcDataDir);
						chooser.setDialogTitle("Choose an import");
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);
						chooser.addChoosableFileFilter(
								new FileNameExtensionFilter("JSON file", "json"));
						// todo maybe somehow get the current jframe or jpanel?
						// looks like org.lwjgl.opengl.Display.parent Canvas thingy
						if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							KeyHelper.importFile(chooser.getSelectedFile());
						}
					}
				});
				break;
			default:
				break;
		}
		KeyHelper.updateConflictCategory();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}
}
