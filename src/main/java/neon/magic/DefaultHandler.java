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
import neon.entities.property.Condition;

/**
 * Handles all effects not handled by another {@code EffectHandler}.
 * 
 * @author mdriesen
 */
public class DefaultHandler implements EffectHandler {
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
		// grote switch voor alle spell effects
		switch(spell.getEffect()) {
		case PARALYZE: target.addCondition(Condition.PARALYZED); break; 
		case LEVITATE: target.addCondition(Condition.LEVITATE); break;
		case BLIND: target.addCondition(Condition.BLIND); break;
		case CALM: target.brain.calm(); break;
		case SILENCE: target.addCondition(Condition.SILENCED); break;
		case BURDEN: target.addCondition(Condition.BURDENED); break;
		case CHARM: target.brain.charm((Creature)spell.getCaster(), (int)spell.getMagnitude()); break;
//		case SCRIPTED: neon.core.Engine.execute(spell.getScript()); break;
		default: System.out.println("not implemented: " + spell.getEffect()); break;
		}		
	}

	public void repeatEffect(Spell spell) {}
	public void removeEffect(Spell spell) {
		Creature target = (Creature)spell.getTarget();
		switch(spell.getEffect()) {
		case LEVITATE: target.removeCondition(Condition.LEVITATE); break;
		case PARALYZE: target.removeCondition(Condition.PARALYZED); break;
		case BLIND: target.removeCondition(Condition.BLIND); break;
		case CALM: target.removeCondition(Condition.CALM); break;
		case SILENCE: target.removeCondition(Condition.SILENCED); break;
		case BURDEN: target.removeCondition(Condition.BURDENED); break;
		case CHARM: target.brain.charm((Creature)spell.getCaster(), -(int)spell.getMagnitude()); break;
		default: System.out.println("not implemented: " + spell.getEffect()); break;
		}				
	}
}
