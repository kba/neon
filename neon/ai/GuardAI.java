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
import neon.entities.components.HealthComponent;
import neon.entities.components.ShapeComponent;

import java.awt.Point;

public class GuardAI extends AI {
	private int range;
	private Point home;
	
	public GuardAI(Creature creature, byte aggression, byte confidence, int range) {
		super(creature, aggression, confidence);
		this.range = range;
		ShapeComponent bounds = creature.getComponent(ShapeComponent.class);
		home = new Point(bounds.x, bounds.y);
	}

	public void act() {
		// TODO: niet alleen op player letten, maar ook op andere wezens in zicht
		ShapeComponent cBounds = creature.getComponent(ShapeComponent.class);
		ShapeComponent pBounds = Engine.getPlayer().getComponent(ShapeComponent.class);
		if(isHostile() && cBounds.getLocation().distance(pBounds.getLocation()) < range){
			HealthComponent health = creature.getComponent(HealthComponent.class);
			if(100*health.getHealth()/health.getBaseHealth() < confidence/100) {	
				// 80% kans om gewoon te vluchten, 20% kans om te healen; als geen heal spell, toch vluchten
				if(Math.random() > 0.2 || !(cure() || heal())) {	
					flee(Engine.getPlayer());
				} 
			} else {
				hunt(range, home, Engine.getPlayer());
			} 
		} else {
			wander(range, home);
		}
	}
}
