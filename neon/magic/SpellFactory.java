/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012 - Maarten Driesen
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

package neon.magic;

import neon.core.Engine;
import neon.objects.resources.LSpell;
import neon.objects.resources.RSpell;
import neon.util.Dice;

/**
 * This is a factory class that generates spells.
 * 
 * @author 	mdriesen
 */
public class SpellFactory {
	/**
	 * Returns the spell with the given id.
	 * 
	 * @param id	the id of the requested spell
	 * @return		the spell with the given id
	 */
	public static RSpell getSpell(String id) {
		if(Engine.getResources().getResource(id, "magic") instanceof LSpell) {
			LSpell ls = (LSpell)Engine.getResources().getResource(id, "magic");
			return getSpell(ls.spells.keySet().toArray()[Dice.roll(1, ls.spells.size(), - 1)].toString());
		} else {
			return (RSpell)Engine.getResources().getResource(id, "magic");
		}
	}
	
	/**
	 * Returns the enchantment with the given id.
	 * 
	 * @param id	the id of the requested enchantment
	 * @return		the enchantment with the given id
	 */
	public static RSpell.Enchantment getEnchantment(String id) {
		if(Engine.getResources().getResource(id, "magic") instanceof RSpell.Enchantment) {
			return (RSpell.Enchantment)Engine.getResources().getResource(id, "magic");
		} else {
			throw new IllegalArgumentException("The given id (" + id + ") is not an enchantment.");
		}
	}
}
