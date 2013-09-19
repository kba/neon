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

import neon.core.Engine;
import neon.entities.Creature;

public class BasicAI extends AI {
	public BasicAI(Creature creature, byte aggression, byte confidence) {
		super(creature, aggression, confidence);
	}

	public void act() {
		// TODO: niet alleen op player letten, maar ook op andere wezens in zicht
		if(isHostile() && sees(Engine.getPlayer())){
			if(100*creature.getHealth()/creature.getBaseHealth() < confidence) {	
				// 80% kans om gewoon te vluchten, 20% kans om te healen; als geen heal spell, toch vluchten
				if(Math.random() > 0.2 || !(cure() || heal())) {	
					flee(Engine.getPlayer());
				} 
			} else {
				hunt(Engine.getPlayer());
			} 
		} else {
			wander();
		}
	}
}
