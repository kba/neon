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

package neon.core.handlers;

import neon.maps.*;
import java.awt.Point;
import java.util.Collection;
import javax.swing.SwingConstants;
import neon.core.Engine;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.Entity;
import neon.entities.components.Lock;
import neon.entities.property.Condition;
import neon.entities.property.Habitat;
import neon.entities.property.Skill;

/**
 * This class takes care of all motion-related actions. Walking, climbing, swimming and teleporting 
 * can be handled.
 * 
 * @author mdriesen
 */
public class MotionHandler {
	public static final byte OK = 0;
	public static final byte BLOCKED = 1;
	public static final byte SWIM = 2;
	public static final byte CLIMB = 3;
	public static final byte DOOR = 4;
	public static final byte NULL = 5;
	public static final byte HABITAT = 6;
	
	/**
	 * Teleports a creature. Two results are possible:
	 * <ul>
	 * 	<li>OK - creature was teleported</li>
	 * 	<li>DOOR - this portal is just a door and does not support teleporting</li>
	 * </ul>
	 * 
	 * @param creature	the creature to teleport.
	 * @param door		the portal that the creature used
	 * @return			the result
	 */
	public static byte teleport(Creature creature, Door door) {
		if(door.portal.isPortal()) {
			Zone previous = Engine.getAtlas().getCurrentZone();	// effe huidige zone bufferen
			if(door.portal.getDestMap() != 0) {
				// effen map laden en deur terug laten verwijzen
				Map map = Engine.getAtlas().getMap(door.portal.getDestMap());
				Zone zone = map.getZone(door.portal.getDestZone());
				for(long uid : zone.getItems(door.portal.getDestPos())) {
					Entity i = Engine.getStore().getEntity(uid);
					if(i instanceof Door) {
						((Door)i).portal.setDestMap(Engine.getAtlas().getCurrentMap());
					}
				}
				Engine.getAtlas().setMap(map);
				Engine.getScriptEngine().put("map", map);
				door.portal.setDestMap(Engine.getAtlas().getCurrentMap());
			} else if(door.portal.getDestTheme() != null) {
				Dungeon dungeon = MapLoader.loadDungeon(door.portal.getDestTheme());
				Engine.getAtlas().setMap(dungeon);
				door.portal.setDestMap(Engine.getAtlas().getCurrentMap());
			}
			
			Engine.getAtlas().enterZone(door, previous);
			
			walk(creature, door.portal.getDestPos());
			// kijken of er op de bestemming een deur staat, zo ja, deze deur unlocken en openen
			for(long uid : Engine.getAtlas().getCurrentZone().getItems(creature.bounds)) {
				Entity i = Engine.getStore().getEntity(uid);
				if(i instanceof Door) {
					((Door)i).lock.open();
				}
			}

			// als er een sign op de deur staat, nu laten zien
			if(door.hasSign()) {
				Engine.getUI().showMessage(door.toString(), 3, SwingConstants.BOTTOM);
			}
			return OK;
		}
		return DOOR;
	}
	
	/**
	 * Lets a creature move (walking, climbing or swimming). The possible results are:
	 * <ul>
	 * 	<li>OK - creature could move</li>
	 * 	<li>NULL - the point this creature wanted to move to doesn't exist</li>
	 * 	<li>SWIM - the creature wanted to swim, but failed a swim check</li>
	 * 	<li>CLIMB - the creature wanted to climb, but failed a climb check</li>
	 * 	<li>BLOCKED - the point this creature wanted to move to was blocked</li>
	 * 	<li>DOOR - the point this creature wanted to move to is blocked by a closed door</li>
	 * 	<li>HABITAT - creature tried to move to the wrong habitat type</li>
	 * </ul>
	 * 
	 * @param actor	the creature that wants to move
	 * @param p		the point the creature wants to move to
	 * @return		the result of the movement
	 */
	public static byte move(Creature actor, Point p) {
		Region region = Engine.getAtlas().getCurrentZone().getRegion(p);
		if(p == null || region == null) {
			return NULL;
		}
		
		// effen kijken of er geen gesloten deur aanwezig is
		Collection<Long> items = Engine.getAtlas().getCurrentZone().getItems(p);
		for(long uid : items) {
			Entity i = Engine.getStore().getEntity(uid);
			if(i instanceof Door) {
				if(((Door)i).lock.getState() != Lock.OPEN) {
					return DOOR;
				}
			}
		}
		
		// type grond bepalen:
		Region.Modifier mov = region.getMovMod();
		
		// kijken of actor aan het leviteren/vliegen is
		if(actor.hasCondition(Condition.LEVITATE) || actor.species.habitat == Habitat.AIR) {
			if(mov != Region.Modifier.BLOCK) {
				mov = Region.Modifier.NONE;
			}
		}

		switch(mov) {
		case NONE: return walk(actor, p);
		case SWIM: return swim(actor, p);
		case CLIMB: return climb(actor, p);
		default: return BLOCKED;
		}
	}

	/**
	 * Lets a creature move.
	 * 
	 * @param creature
	 * @param x
	 * @param y
	 * @return	the result of the movement
	 */
	public static byte move(Creature creature, int x, int y) {
		return move(creature, new Point(x, y));
	}
	
	private static byte swim(Creature actor, Point p) {
		if(actor.species.habitat == Habitat.WATER) {
			return OK;
		} else if(SkillHandler.check(actor, Skill.SWIMMING) > 20) {
			actor.getBounds().setLocation(p.x, p.y);
			return OK;
		} else {
			return SWIM;
		}
	}
	
	/*
	 * Methode om te klimmen. De skill check
	 * moet groter zijn dan 25 (later meer terreinvarieteiten).
	 * 
	 * @param tile
	 */
	private static byte climb(Creature actor, Point p) {
		if(actor.species.habitat == Habitat.WATER) {
			return HABITAT;
		} if(SkillHandler.check(actor, Skill.CLIMBING) > 25) {
			actor.getBounds().setLocation(p.x, p.y);
			return OK;
		} else {
			return CLIMB;
		}
	}
	
	private static byte walk(Creature actor, Point p) {
		if(actor.species.habitat == Habitat.WATER) {
			return HABITAT;
		} else {
			actor.getBounds().setLocation(p.x, p.y);
			return OK;
		}
	}
}
