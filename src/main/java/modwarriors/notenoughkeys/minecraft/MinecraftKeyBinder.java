package modwarriors.notenoughkeys.minecraft;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import modwarriors.notenoughkeys.Helper;
import modwarriors.notenoughkeys.NotEnoughKeys;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

/**
 * Created by TheTemportalist on 9/7/2015.
 */
public class MinecraftKeyBinder {

	public static MinecraftKeyBinder binder;
	private Minecraft mc;
	private GameSettings gameSettings;

	public static void preInit() {
		MinecraftKeyBinder.binder = new MinecraftKeyBinder();
		MinecraftKeyBinder.binder.mc = Minecraft.getMinecraft();
		MinecraftKeyBinder.binder.gameSettings = Minecraft.getMinecraft().gameSettings;
		MinecraftKeyBinder.binder.register();
	}

	public KeyBinding testKey;

	private void register() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);

		testKey = new KeyBinding("test", Keyboard.KEY_Q, "gameplay");
		ClientRegistry.registerKeyBinding(testKey);
		Api.registerMod("Not Enough Keys", "test");

		/*
		ArrayList<KeyBinding> whitelist = new ArrayList<>();
		for (int i = 0; i < 9; i++)
		whitelist.add(this.gameSettings.keyBindsHotbar[i]);
		whitelist.add(this.gameSettings.keyBindInventory);
		whitelist.add(this.gameSettings.keyBindDrop);
		whitelist.add(this.gameSettings.keyBindChat);
		whitelist.add(this.gameSettings.keyBindCommand);

		ArrayList<String> descss = new ArrayList<>();
		for (KeyBinding keyBinding : this.gameSettings.keyBindings) {
			if (whitelist.contains(keyBinding))
				descss.add(keyBinding.getKeyDescription());
		}
		Api.registerMod("Minecraft", descss.toArray(new String[0]));
		*/

	}

	@SubscribeEvent
	public void onKeyBindingPressed(KeyBindingPressedEvent event) {
		if (event.isKeyBindingPressed) {
			if (event.keyBinding == this.testKey)
				NotEnoughKeys.logger.info("Pressed test");
			for (int i = 0; i < 9; i++)
				if (event.keyBinding == this.gameSettings.keyBindsHotbar[i])
					this.mc.thePlayer.inventory.currentItem = i;
			if (event.keyBinding == this.gameSettings.keyBindInventory) {
				if (this.mc.playerController.isRidingHorse())
					this.mc.thePlayer.sendHorseInteraction();
				else {
					this.mc.getNetHandler().addToSendQueue(
							new C16PacketClientStatus(
									C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
					this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
				}
			}
			if (event.keyBinding == this.gameSettings.keyBindDrop)
				this.mc.thePlayer.dropOneItem(Helper.isCtrlKeyDown());
			boolean flag =
					this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;
			if (event.keyBinding == this.gameSettings.keyBindChat && flag)
				this.mc.displayGuiScreen(new GuiChat());
			if (event.keyBinding == this.gameSettings.keyBindCommand && flag &&
					this.mc.currentScreen == null)
				this.mc.displayGuiScreen(new GuiChat("/"));
		}
	}

	/*
	@SubscribeEvent
	public void onKeyEvent(InputEvent.KeyInputEvent event) {
		if (!Api.isLoaded()) {
			// do your stuff here, on normal basis. If mod IS loaded, use the KeyBindingPressedEvent (as shown below)
		}
	}
	*/

}
