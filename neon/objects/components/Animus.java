/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2013 - Maarten Driesen
 * 
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neon.objects.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import neon.objects.entities.Creature;
import neon.objects.resources.RSpell;

/**
 * This class represents the magical component of creatures. The total mana 
 * available is determined as follows: mana = intelligence * base mana + base 
 * mana modifier + mana modifier.
 * 
 * @author mdriesen
 */
public class Animus implements Component{
	private float baseManaMod = 0;
	private float manaMod = 0;
	private RSpell spell;		// geëquipte spell
	private Set<RSpell> spells = new HashSet<RSpell>();
	private HashMap<RSpell.Power, Integer> powers = new HashMap<RSpell.Power, Integer>();
	private Creature creature;	// Creature nemen, want int kan veranderen
	
	/**
	 * 
	 * @param creature
	 */
	public Animus(Creature creature) {
		this.creature = creature;
	}
	
	/**
	 * Adds a spell to this creature.
	 * 
	 * @param spell	the spell to add
	 */
	public void addSpell(RSpell spell) {
		if(spell instanceof RSpell.Enchantment) {
			throw new IllegalArgumentException("Can not add enchantment to creature.");
		} else if(spell instanceof RSpell.Power) {
			powers.put((RSpell.Power)spell, 0);
		} else {
			spells.add(spell);
		}
	}
	
	/**
	 * Checks whether a creature can use the given power.
	 * 
	 * @param power
	 * @param time
	 * @return	whether this creature can use the given power
	 */
	public boolean canUse(RSpell.Power power, int time) {
		if(powers.containsKey(power)) {
			return powers.get(power) + power.interval - time < 1;
		} else {
			throw new IllegalArgumentException("Creature does not have power.");
		}
	}
	
	/**
	 * Sets the timer for the given power.
	 * 
	 * @param power
	 * @param time
	 */
	public void usePower(RSpell.Power power, int time) {
		if(powers.containsKey(power)) {
			powers.put(power, time);
		} else {
			throw new IllegalArgumentException("Creature does not have power.");
		}
	}
	
	/**
	 * Checks the last time when the given power was used.
	 * 
	 * @param power
	 */
	public int checkPower(RSpell.Power power) {
		if(powers.containsKey(power)) {
			return powers.get(power);
		} else {
			throw new IllegalArgumentException("Creature does not have power.");
		}
	}
	
	/**
	 * @return	a list with this creature's spells
	 */
	public Collection<RSpell> getSpells() {
		return spells;
	}
	
	/**
	 * @return	a list with this creature's powers
	 */
	public Collection<RSpell.Power> getPowers() {
		return powers.keySet();
	}
	
	/**
	 * Equips a spell.
	 * 
	 * @param spell	the spell to equip
	 */
	public void equipSpell(RSpell spell) {
		if(spell == null || spells.contains(spell) || powers.containsKey(spell)) {
			this.spell = spell;
		}
	}
	
	/**
	 * @return	the currently equiped spell
	 */
	public RSpell getSpell() {
		return spell;
	}
	
	/**
	 * Adds to the base mana modifier, lowering or raising the maximum amount of 
	 * mana.
	 * 
	 * @param value	the amount to add to the modifier
	 */
	public void addBaseModifier(float value) {
		baseManaMod += value;
	}
	
	/**
	 * Charges the magical reserves.
	 * 
	 * @param amount	the amount to charge
	 */
	public void addMana(float amount) {
		// modifier moet altijd kleiner zijn als 0
		manaMod = Math.min(0, manaMod + amount);
	}
	
	/**
	 * Returns the mana, modified with magical effects, used mana, etc.
	 * 
	 * @return this creature's mana
	 */
	public int getMana() {
		return (int)(creature.getInt() * creature.species.mana + baseManaMod + manaMod);
	}
	
	/**
	 * @return	the mana modifier
	 */
	public float getModifier() {
		return manaMod;
	}
	
	/**
	 * @return	the base mana modifier
	 */
	public float getBaseModifier() {
		return baseManaMod;
	}

	/**
	 * Sets the mana modifier.
	 * 
	 * @param mod
	 */
	public void setModifier(float mod) {
		manaMod = mod;
	}
	
	/**
	 * Sets the base mana modifier.
	 * 
	 * @param mod
	 */
	public void setBaseModifier(float mod) {
		baseManaMod = mod;
	}

	@Override
	public long getUID() {
		return creature.getUID();
	}
}
