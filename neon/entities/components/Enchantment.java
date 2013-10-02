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

package neon.entities.components;

import java.io.Serializable;

import neon.resources.RSpell;

/**
 * This class represents an item enchantment.
 * 
 * @author mdriesen
 */
public class Enchantment implements Serializable, Component {
	private static final long serialVersionUID = 1L;
	private final RSpell spell;	// niet RSpell.Enchantment vanwege scrolls
	private final int mana;
	private float modifier = 0;
	private long uid;
	
	public Enchantment(RSpell spell, int mana, long uid) {
		this.spell = spell;
		this.mana = mana;
		this.uid = uid;
	}
	
	/**
	 * @return	the spell of this enchantment
	 */
	public RSpell getSpell() {
		return spell;
	}
	
	/**
	 * @return	the total mana
	 */
	public int getMana() {
		return (int)(mana + modifier);
	}
	
	/**
	 * @return	the base mana
	 */
	public int getBaseMana() {
		return mana;
	}
	
	/**
	 * @return	the mana modifier
	 */
	public float getModifier() {
		return modifier;
	}
	
	/**
	 * Sets the mana modifier.
	 * 
	 * @param manaMod
	 */
	public void setModifier(float manaMod) {
		modifier = manaMod;
	}
	
	/**
	 * Adds an amount of mana.
	 * 
	 * @param amount
	 */
	public void addMana(float amount) {
		// modifier moet altijd kleiner zijn als 0
		modifier = Math.min(0, modifier + amount);
	}

	@Override
	public long getUID() {
		return uid;
	}
}
