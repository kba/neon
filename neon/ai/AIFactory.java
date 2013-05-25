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
import neon.objects.entities.Creature;
import neon.objects.resources.RCreature;
import neon.objects.resources.RPerson;
import neon.objects.resources.RCreature.AIType;

public class AIFactory {
	/**
	 * Loads the AI of an NPC.
	 * 
	 * @param creature
	 * @param rp
	 * @return	the AI of an NPC
	 */
	public AI getAI(Creature creature, RPerson rp) {
		// standaard waardes voor species van npc
		AIType type = creature.species.aiType;
		int aggression = creature.species.aiAggr;
		int confidence = creature.species.aiConf;
		int range = creature.species.aiRange;
		
		// controleren of waardes zijn geherdefinieerd in npc resource
		if(rp.aiType != null) {
			type = rp.aiType;			
		} 
		if(rp.aiAggr > -1) {
			aggression = rp.aiAggr;			
		} 
		if(rp.aiConf > -1) {
			confidence = rp.aiConf;			
		} 
		if(rp.aiRange > -1) {
			range = rp.aiRange;			
		} 
		return getAI(type, creature, (byte)aggression, (byte)confidence, range);
	}
	
	/**
	 * Loads the AI of a creature.
	 * 
	 * @param creature
	 * @return	the AI of the given creature.
	 */
	public AI getAI(Creature creature) {
		RCreature rc = creature.species;
		AIType type = rc.aiType;
		int confidence = rc.aiConf;
		int aggression = rc.aiAggr;
		int range = rc.aiRange;
		return getAI(type, creature, (byte)aggression, (byte)confidence, range);
	}
	
	private AI getAI(AIType type, Creature creature, byte aggression, byte confidence, int range) {
		switch(type) {
		case wander:
			return new BasicAI(creature, aggression, confidence);
		case guard:
			return new GuardAI(creature, aggression, confidence, range);
		case schedule:
			return new ScheduleAI(creature, aggression, confidence, new Point[0]);
		default:
			return new GuardAI(creature, aggression, confidence, range);
		}		
	}
}
