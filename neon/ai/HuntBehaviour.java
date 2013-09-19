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

package neon.ai;

import java.awt.Point;
import neon.core.Engine;
import neon.core.handlers.MagicHandler;
import neon.entities.Creature;
import neon.entities.property.Skill;
import neon.resources.RSpell;
import neon.util.Dice;

public class HuntBehaviour implements Behaviour {
	private Creature creature;
	private Creature prey;
	
	public HuntBehaviour(Creature hunter, Creature prey) {
		creature = hunter;
		this.prey = prey;
	}
	
	public void act() {
		int dice = Dice.roll(1,2,0);

		if(dice == 1) {
			int time = Engine.getTimer().getTime();
			for(RSpell.Power power : creature.animus.getPowers()) {
				if(power.effect.getSchool().equals(Skill.DESTRUCTION) && creature.animus.canUse(power, time) && 
						power.range >= Point.distance(creature.getBounds().x, creature.getBounds().y, 
								prey.getBounds().x, prey.getBounds().y)) {
					creature.animus.equipSpell(power);
					MagicHandler.cast(creature, prey.bounds.getLocation());
					return;	// hunt afbreken van zodra er een spell is gecast
				}
			}
			for(RSpell spell : creature.animus.getSpells()) {
				if(spell.effect.getSchool().equals(Skill.DESTRUCTION) && 
						spell.range >= Point.distance(creature.getBounds().x, creature.getBounds().y, 
								prey.getBounds().x, prey.getBounds().y)) {
					creature.animus.equipSpell(spell);
					MagicHandler.cast(creature, prey.bounds.getLocation());
					return;	// hunt afbreken van zodra er een spell is gecast
				}
			}
		} 

		Point p;
		if(creature.getInt() < 5) {		// als wezen lomp is, gewoon kortste weg proberen
			int dx = 0;
			int dy = 0;
			if(creature.getBounds().x < prey.getBounds().x) { 
				dx = 1; 
			} else if(creature.getBounds().x > prey.getBounds().x) { 
				dx = -1; 
			}
			if(creature.getBounds().y < prey.getBounds().y) { 
				dy = 1; 
			} else if(creature.getBounds().y > prey.getBounds().y) { 
				dy = -1; 
			}				
			p = new Point(creature.getBounds().x + dx, creature.getBounds().y + dy);
		} else {						// als wezen slimmer is, A* proberen
			p = PathFinder.findPath(creature, creature.bounds.getLocation(), prey.bounds.getLocation())[0];
		}

		if(p.distance(prey.getBounds().x, prey.getBounds().y) < 1) {
//			if(creature.inventory.hasEquiped(Slot.WEAPON) && creature.getWeapon().isRanged()) {
//				if(!(creature.inventory.getWeaponType().equals(WeaponType.THROWN) || equip(Slot.AMMO))) {
//					InventoryHandler.unequip(creature.getWeapon().getUID(), creature);
//				}
//			} else if(!creature.inventory.hasEquiped(Slot.WEAPON)) {
//				equip(Slot.WEAPON);
//			} 
//			Engine.post(new CombatEvent(creature, prey));
//		} else if(Atlas.getCurrentZone().getCreature(p) == null) {
//			if(MotionHandler.move(creature, p) == MotionHandler.DOOR) {
//				open(p);	// deur opendoen indien nodig
//			}
		} else {	// als een ander creature in de weg staat
//			wander();
		}		
	}
}
