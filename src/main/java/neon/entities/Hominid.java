/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012-2013 - Maarten Driesen
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

package neon.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import neon.entities.components.Characteristics;
import neon.entities.property.Ability;
import neon.entities.property.Gender;
import neon.resources.RCreature;
import neon.resources.RTattoo;

public class Hominid extends neon.entities.Creature {
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
		Characteristics chars = getCharacteristicsComponent();
		if(chars.hasAbility(ability)) {
			chars.addAbility(ability, chars.getAbility(ability) + tattoo.magnitude);
		}
	}
	
	@Override
	public boolean hasDialog() {
		return true;
	}
}
