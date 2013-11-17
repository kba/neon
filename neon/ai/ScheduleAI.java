/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2010 - Maarten Driesen
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
import neon.entities.Creature;

// TODO: schedule in editor
public class ScheduleAI extends AI {
	private Point[] schedule;
	private int current = 0;
	
	public ScheduleAI(Creature creature, byte aggression, byte confidence, Point[] schedule) {
		super(creature, aggression, confidence);
		this.schedule = schedule;
	}

	public void act() { 
		if(isHostile() && sees(Engine.getPlayer())){
			if(100*creature.health.getHealth()/creature.health.getBaseHealth() < confidence) {	
				// 80% kans om gewoon te vluchten, 20% kans om te healen; als geen heal spell, toch vluchten
				if(Math.random() > 0.2 || !(cure() || heal())) {	
					flee(Engine.getPlayer());
				} 
			} else {
				hunt(Engine.getPlayer());
			} 
		} else {
			if(creature.bounds.getLocation().equals(schedule[current])) {
				current++;
				if(current >= schedule.length) {
					current = 0;
				}
			}
			wander(schedule[current]);
		}
	}
}
