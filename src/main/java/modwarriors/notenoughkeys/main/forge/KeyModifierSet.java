package modwarriors.notenoughkeys.main.forge;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeyModifierSet {

	public static final KeyModifierSet NONE = new KeyModifierSet();

	private final Set<KeyModifier> modifiers;

	public KeyModifierSet(KeyModifier... keyModifiers) {
		this.modifiers = new HashSet<KeyModifier>();
		this.modifiers.addAll(Arrays.asList(keyModifiers));
	}

	public int getQuantityMatching(Set<KeyModifier> modifiers) {
		int matches = 0;
		for (KeyModifier modifier : this.modifiers)
			if (modifiers.contains(modifier)) matches++;
		return matches;
	}

	public boolean isActive() {
		for (KeyModifier modifier : this.modifiers)
			if (!modifier.isActive()) return false;
		return true;
	}

	public boolean matches(int keyCode) {
		for (KeyModifier modifier : this.modifiers)
			if (modifier.matches(keyCode)) return true;
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (KeyModifier m : this.modifiers)
			hashCode += 1 << m.ordinal();
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof KeyModifierSet &&
				this.modifiers.equals(((KeyModifierSet) obj).modifiers);
	}

}
