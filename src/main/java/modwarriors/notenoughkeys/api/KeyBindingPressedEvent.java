package modwarriors.notenoughkeys.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

/**
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
@Cancelable
public class KeyBindingPressedEvent extends Event {

	public KeyBinding keyBinding = null;
	public boolean shiftRequired = false, ctrlRequired = false, altRequired = false;

	public KeyBindingPressedEvent(KeyBinding keyBinding, boolean[] alts) {
		super();
		this.keyBinding = keyBinding;
		this.shiftRequired = alts[0];
		this.ctrlRequired = alts[1];
		this.altRequired = alts[2];

	}

}
