package modwarriors.notenoughkeys.main.forge;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.IntHashMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBinding implements Comparable<KeyBinding>
{
	private static final List<KeyBinding> keybindArray = Lists.<KeyBinding>newArrayList();
	private static final modwarriors.notenoughkeys.main.forge.KeyBindingMap hash = new modwarriors.notenoughkeys.main.forge.KeyBindingMap();
	private static final Set<String> keybindSet = Sets.<String>newHashSet();
	private final String keyDescription;
	private final int keyCodeDefault;
	private final String keyCategory;
	private int keyCode;
	private boolean pressed;
	private int pressTime;

	public static void onTick(int keyCode)
	{
		if (keyCode != 0)
		{
			for (KeyBinding keybinding : hash.lookupActive(keyCode))

			if (keybinding != null)
			{
				++keybinding.pressTime;
			}
		}
	}

	public static void setKeyBindState(int keyCode, boolean pressed)
	{
		if (keyCode != 0)
		{
			for (KeyBinding keybinding : hash.lookupAll(keyCode))

			if (keybinding != null)
			{
				keybinding.pressed = pressed;
			}
		}
	}

	public static void updateKeyBindState()
	{
		for (KeyBinding keybinding : keybindArray)
		{
			try
			{
				setKeyBindState(keybinding.keyCode, Keyboard.isKeyDown(keybinding.keyCode));
			}
			catch (IndexOutOfBoundsException var3)
			{
				;
			}
		}
	}

	public static void unPressAllKeys()
	{
		for (KeyBinding keybinding : keybindArray)
		{
			keybinding.unpressKey();
		}
	}

	public static void resetKeyBindingArrayAndHash()
	{
		hash.clearMap();

		for (KeyBinding keybinding : keybindArray)
		{
			hash.addKey(keybinding.keyCode, keybinding);
		}
	}

	public static Set<String> getKeybinds()
	{
		return keybindSet;
	}

	public KeyBinding(String description, int keyCode, String category)
	{
		this.keyDescription = description;
		this.keyCode = keyCode;
		this.keyCodeDefault = keyCode;
		this.keyCategory = category;
		keybindArray.add(this);
		hash.addKey(keyCode, this);
		keybindSet.add(category);
	}

	public boolean isKeyDown()
	{
		return this.pressed && getKeyConflictContext().isActive() && getKeyModifierSet().isActive();
	}

	public String getKeyCategory()
	{
		return this.keyCategory;
	}

	public boolean isPressed()
	{
		if (this.pressTime == 0)
		{
			return false;
		}
		else
		{
			--this.pressTime;
			return true;
		}
	}

	private void unpressKey()
	{
		this.pressTime = 0;
		this.pressed = false;
	}

	public String getKeyDescription()
	{
		return this.keyDescription;
	}

	public int getKeyCodeDefault()
	{
		return this.keyCodeDefault;
	}

	public int getKeyCode()
	{
		return this.keyCode;
	}

	public void setKeyCode(int keyCode)
	{
		this.keyCode = keyCode;
	}

	public int compareTo(KeyBinding p_compareTo_1_)
	{
		int i = I18n.format(this.keyCategory, new Object[0]).compareTo(I18n.format(p_compareTo_1_.keyCategory, new Object[0]));

		if (i == 0)
		{
			i = I18n.format(this.keyDescription, new Object[0]).compareTo(I18n.format(p_compareTo_1_.keyDescription, new Object[0]));
		}

		return i;
	}

	/****************** Forge Start *****************************/
	private modwarriors.notenoughkeys.main.forge.KeyModifierSet keyModifierSetDefault = modwarriors.notenoughkeys.main.forge.KeyModifierSet.NONE;
	private modwarriors.notenoughkeys.main.forge.KeyModifierSet keyModifierSet = modwarriors.notenoughkeys.main.forge.KeyModifierSet.NONE;
	private net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext = net.minecraftforge.client.settings.KeyConflictContext.UNIVERSAL;

	/**
	 * Convenience constructor for creating KeyBindings with keyConflictContext set.
	 */
	public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, int keyCode, String category)
	{
		this(description, keyConflictContext, modwarriors.notenoughkeys.main.forge.KeyModifierSet.NONE, keyCode, category);
	}

	/**
	 * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
	 */
	public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, modwarriors.notenoughkeys.main.forge.KeyModifierSet keyModifier, int keyCode, String category)
	{
		this.keyDescription = description;
		this.keyCode = keyCode;
		this.keyCodeDefault = keyCode;
		this.keyCategory = category;
		this.keyConflictContext = keyConflictContext;
		this.keyModifierSet = keyModifier;
		this.keyModifierSetDefault = keyModifier;
		if (this.keyModifierSet.matches(keyCode))
		{
			this.keyModifierSet = modwarriors.notenoughkeys.main.forge.KeyModifierSet.NONE;
		}
		keybindArray.add(this);
		hash.addKey(keyCode, this);
		keybindSet.add(category);
	}

	/**
	 * Checks that the key conflict context and modifier are active, and that the keyCode matches this binding.
	 */
	public boolean isActiveAndMatches(int keyCode)
	{
		return keyCode == this.getKeyCode() && getKeyConflictContext().isActive() && getKeyModifierSet().isActive();
	}

	public void setKeyConflictContext(net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext)
	{
		this.keyConflictContext = keyConflictContext;
	}

	public net.minecraftforge.client.settings.IKeyConflictContext getKeyConflictContext()
	{
		return keyConflictContext;
	}

	public net.minecraftforge.client.settings.KeyModifier getKeyModifierDefault()
	{
		return net.minecraftforge.client.settings.KeyModifier.NONE;
	}

	public net.minecraftforge.client.settings.KeyModifier getKeyModifier()
	{
		return net.minecraftforge.client.settings.KeyModifier.NONE;
	}

	public modwarriors.notenoughkeys.main.forge.KeyModifierSet getKeyModifierSetDefault()
	{
		return keyModifierSetDefault;
	}

	public modwarriors.notenoughkeys.main.forge.KeyModifierSet getKeyModifierSet()
	{
		return keyModifierSet;
	}

	public void setKeyModifierAndCode(int keyCode, modwarriors.notenoughkeys.main.forge.KeyModifier... modifiers)
	{
		this.setKeyModifierAndCode(keyCode, new KeyModifierSet(modifiers));
	}

	public void setKeyModifierAndCode(int keyCode, modwarriors.notenoughkeys.main.forge.KeyModifierSet modifiers)
	{
		this.keyCode = keyCode;
		this.keyModifierSet = modifiers;
		hash.removeKey(this);
		hash.addKey(keyCode, this);
	}

	public void setToDefault()
	{
		setKeyModifierAndCode(getKeyCodeDefault(), getKeyModifierSetDefault());
	}

	public boolean isSetToDefaultValue()
	{
		return getKeyCode() == getKeyCodeDefault() && getKeyModifierSet() == getKeyModifierSetDefault();
	}

	/**
	 * Returns true when the other keyBinding conflicts with this one
	 */
	public boolean conflicts(KeyBinding other)
	{
		if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext()))
		{
			modwarriors.notenoughkeys.main.forge.KeyModifierSet keyModifier = getKeyModifierSet();
			modwarriors.notenoughkeys.main.forge.KeyModifierSet otherKeyModifier = other.getKeyModifierSet();
			if (keyModifier.matches(other.getKeyCode()) || otherKeyModifier.matches(getKeyCode()))
			{
				return true;
			}
			else if (keyModifier == otherKeyModifier || keyModifier == modwarriors.notenoughkeys.main.forge.KeyModifierSet.NONE || otherKeyModifier == modwarriors.notenoughkeys.main.forge.KeyModifierSet.NONE)
			{
				return getKeyCode() == other.getKeyCode();
			}
		}
		return false;
	}

	/**
	 * Returns true when one of the bindings' key codes conflicts with the other's modifier.
	 */
	public boolean hasKeyCodeModifierConflict(KeyBinding other)
	{
		if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext()))
		{
			if (getKeyModifierSet().matches(other.getKeyCode()) || other.getKeyModifierSet().matches(getKeyCode()))
			{
				return true;
			}
		}
		return false;
	}

	public String getDisplayName()
	{
		return GameSettings.getKeyDisplayString(getKeyCode());//getKeyModifier().getLocalizedComboName(getKeyCode());
	}
	/****************** Forge End *****************************/
}
