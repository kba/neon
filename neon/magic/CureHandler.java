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

import neon.objects.entities.Creature;
import neon.objects.property.Condition;
import neon.objects.resources.RSpell;

/**
 * Handles all <i>cure</i> effects: cure disease, cure poison, cure blindness,
 * cure paralyzation and lift curse.
 * 
 * @author mdriesen
 */
public class CureHandler implements EffectHandler {
	public boolean isWeaponEnchantment() {
		return false;
	}

	public boolean isClothingEnchantment() {
		return false;
	}

	public boolean onItem() {
		return false;
	}	

	public void addEffect(Spell spell) {
		Creature target = (Creature)spell.getTarget();
		switch(spell.getEffect()) {
		case CURE_DISEASE: MagicUtils.cure(target, RSpell.SpellType.DISEASE); break;
		case CURE_POISON: MagicUtils.cure(target, RSpell.SpellType.POISON); break;
		case LIFT_CURSE: MagicUtils.cure(target, RSpell.SpellType.CURSE); break;
		case CURE_PARALYZATION:
			for(Spell s : target.getActiveSpells()) {
				if(s.getEffect() == Effect.PARALYZE) {
					target.removeActiveSpell(s);
				}
			}
			target.removeCondition(Condition.PARALYZED);
			break;
		case CURE_BLINDNESS:
			for(Spell s : target.getActiveSpells()) {
				if(s.getEffect() == Effect.BLIND) {
					target.removeActiveSpell(s);
				}
			}
			target.removeCondition(Condition.BLIND);
			break;
		default:
			throw new IllegalArgumentException("The given spell does not have a cure effect.");
		}		
	}
	
	public void removeEffect(Spell spell) {}
	public void repeatEffect(Spell spell) {}
}
