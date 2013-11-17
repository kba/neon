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

import neon.entities.Creature;
import neon.entities.property.Damage;

public class LeechHandler implements EffectHandler {
	private Damage type;
	
	public LeechHandler(Damage type) {
		this.type = type;
	}

	public boolean isWeaponEnchantment() {
		return true;
	}

	public boolean isClothingEnchantment() {
		return false;
	}

	public boolean onItem() {
		return false;
	}

	public void addEffect(Spell spell) {
		repeatEffect(spell);	// repeat doet toch niks speciaals
	}

	public void repeatEffect(Spell spell) {
		Creature target = (Creature)spell.getTarget();
		Creature caster = (Creature)spell.getCaster();
		switch(type) {
		case HEALTH: 
			target.health.heal(-spell.getMagnitude());
			caster.health.heal(spell.getMagnitude());
			break;
		case MANA: 
			target.animus.addMana(-spell.getMagnitude()); 
			caster.animus.addMana(spell.getMagnitude()); 
			break;
		default:
			throw new IllegalArgumentException("The given spell does not have a leech effect.");
		}
	}

	public void removeEffect(Spell spell) {
		// remove stopt het drainen
	}
}
