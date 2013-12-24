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

package neon.entities.components;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import neon.entities.property.Ability;
import neon.entities.property.Feat;
import neon.entities.property.Trait;

public class Characteristics implements Component {
	private final long uid;
	private Set<Feat> feats = EnumSet.noneOf(Feat.class);
	private Set<Trait> traits = EnumSet.noneOf(Trait.class);
	private EnumMap<Ability, Integer> abilities = new EnumMap<>(Ability.class);

	public Characteristics(long uid) {
		this.uid = uid;
	}
	
	/**
	 * Checks if this creature has a certain feat.
	 * 
	 * @param feat	the feat to check
	 * @return		whether this creature has the given feat
	 */
	public boolean hasFeat(Feat feat) {
		return feats.contains(feat);
	}
	
	public Collection<Feat> getFeats() {
		return feats;
	}
	
	/**
	 * Adds a feat to this creature
	 * 
	 * @param feat	the feat to add
	 */
	public void addFeat(Feat feat) {
		feats.add(feat);
	}
	
	/**
	 * Checks if this creature has a certain trait.
	 * 
	 * @param trait	the trait to check
	 * @return		whether this creature has the given trait
	 */
	public boolean hasTrait(Trait trait) {
		return traits.contains(trait);
	}
	
	public Collection<Trait> getTraits() {
		return traits;
	}
	
	/**
	 * Adds a trait to this creature
	 * 
	 * @param trait	the trait to add
	 */
	public void addTrait(Trait trait) {
		traits.add(trait);
	}
	
	/**
	 * Checks if this creature has a certain ability.
	 * 
	 * @param ability	the ability to check
	 * @return		whether this creature has the given ability
	 */
	public boolean hasAbility(Ability ability) {
		return abilities.containsKey(ability);
	}
	
	public Collection<Ability> getAbilities() {
		return abilities.keySet();
	}
	
	public int getAbility(Ability ability) {
		return abilities.get(ability);
	}
	
	/**
	 * Adds an ability to this creature
	 * 
	 * @param ability	the ability to add
	 * @param value		the magnitude of the ability
	 */
	public void addAbility(Ability ability, int value) {
		if(abilities.containsKey(ability)) {
			abilities.put(ability, abilities.get(ability) + value);
		} else {
			abilities.put(ability, value);
		}
	}
	
	public long getUID() {
		return uid;
	}
}
