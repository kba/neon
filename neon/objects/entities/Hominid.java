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

package neon.objects.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import neon.objects.property.Ability;
import neon.objects.property.Gender;
import neon.objects.resources.RCreature;
import neon.objects.resources.RTattoo;

public class Hominid extends neon.objects.entities.Creature {
	private Set<RTattoo> tattoos = new HashSet<RTattoo>();
	
	public Hominid(String id, long uid, RCreature species) {
		super(id, uid, species);
	}
	
	public Hominid(String id, long uid, String name, RCreature species, Gender gender) {
		super(id, uid, name, species);
	}
	
	public Collection<RTattoo> getTattoos() {
		return tattoos;
	}
	
	public void addTattoo(RTattoo tattoo) {
		tattoos.add(tattoo);
		Ability ability = tattoo.ability;
		if(abilities.containsKey(ability)) {
			abilities.put(ability, abilities.get(ability) + tattoo.magnitude);
		}
	}
	
	/**
	 * A goblinoid.
	 * 
	 * @author mdriesen
	 */
	public static class Goblin extends Hominid {
		public Goblin(int x, int y, String type, long uid, RCreature species) {
			super(type, uid, species);
		}
	}
}
