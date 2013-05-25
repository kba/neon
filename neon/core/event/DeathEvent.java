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

package neon.core.event;

import java.util.EventObject;
import neon.objects.entities.Creature;

@SuppressWarnings("serial")
public class DeathEvent extends EventObject {
	private Creature creature;
	private int time;
	
	/**
	 * @param c	the creature that died
	 * @param t	the time of death
	 */
	public DeathEvent(Creature c, int t) {
		super(c);
		creature = c;
		time = t;
	}
	
	public int getTime() {
		return time;
	}
	
	public Creature getCreature() {
		return creature;
	}
	
	@Override
	public String toString() {
		return "die:" + creature.getID();
	}
}