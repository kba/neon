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

package neon.entities;

import java.util.*;
import neon.magic.Spell;
import neon.resources.RCreature;
import neon.ai.AI;
import neon.entities.components.*;
import neon.entities.property.*;

/**
 * This class implements a general creature. Specific types of creatures can
 * be subclassed from this class.
 * 
 * @author mdriesen
 */
public class Creature extends Entity {
	// componenten
	public final FactionComponent social;
	public final Stats stats;
	public final RCreature species;
	public AI brain;
	
	// allerlei
	protected Gender gender;
	protected String name;
	
	// lijstjes
	protected EnumMap<Skill, Float> skills;
	protected ArrayList<Spell> spells;			// active spells
	protected Set<Condition> conditions;
	
	// character attributen
	private int date = 0;						// time of death
	
	/**
	 * Initialize a creature with the given data.
	 * 
	 * @param id	the creature's id
	 * @param uid	the creature's uid
	 */
	public Creature(String id, long uid, RCreature species) {
		super(id, uid);
		
		// components
		this.species = species;
		components.putInstance(Animus.class, new Animus(this));
		components.putInstance(RenderComponent.class, new CreatureRenderComponent(this));
		social = new FactionComponent(uid);
		components.putInstance(HealthComponent.class, new HealthComponent(uid, species.hit));
		components.putInstance(Inventory.class, new Inventory(uid));
		stats = new Stats(uid, species);
		components.putInstance(Characteristics.class, new Characteristics(uid));

		// dit eerst
		gender = Gender.OTHER;
		name = species.getName();
		
		// collections initialiseren
		spells = new ArrayList<Spell>();
		skills = new EnumMap<Skill, Float>(species.skills);
		conditions = EnumSet.noneOf(Condition.class);
	}
	
	public Creature(String id, long uid, String name, RCreature species) {
		this(id, uid, species);
		this.name = name;
	}

	public FactionComponent getFactionComponent() {
		return social;
	}
	
	public HealthComponent getHealthComponent() {
		return components.getInstance(HealthComponent.class);
	}
	
	public Animus getMagicComponent() {
		return components.getInstance(Animus.class);
	}
	
	public Inventory getInventoryComponent() {
		return components.getInstance(Inventory.class);
	}
	
	public Stats getStatsComponent() {
		return stats;
	}
	
	public Characteristics getCharacteristicsComponent() {
		return components.getInstance(Characteristics.class);
	}
	
	public AI getAI() {
		return brain;
	}
	
// conditions die een actor kan hebben
	/**
	 * Adds a condition to this creature (for instance 'levitating').
	 * 
	 * @param c	the name of the condition to add
	 */
	public void addCondition(Condition c) {
		conditions.add(c);
	}
	
	/**
	 * Removes a condition from this creature.
	 * 
	 * @param c	the name of the condition to remove
	 */
	public void removeCondition(Condition c) {
		conditions.remove(c);
	}
	
	/**
	 * @return	all conditions this creature has
	 */
	public Set<Condition> getConditions() {
		return conditions;
	}
	
	/**
	 * Checks whether this creature has a condition.
	 * 
	 * @param c	the condition to check
	 * @return	<code>true</code> if the creature has the condition, <code>false</code> otherwise
	 */
	public boolean hasCondition(Condition c) {
		return conditions.contains(c);
	}
	
// dingen die met sterven te maken hebben
	/**
	 * Lets this creature die.
	 * 
	 * @param time	the time of death
	 */
	public void die(int time) {
		conditions.add(Condition.DEAD);
		date = time;
	}
	
	/**
	 * @return	the time of death
	 */
	public int getTimeOfDeath() {
		return date; // 0 is niet dood, negatief is dood voor spel begon
	}
	
	/**
	 * Adds a spell to this creature's list of active spells.
	 * 
	 * @param spell	the spell to add
	 */
	public void addActiveSpell(Spell spell) {
		spells.add(spell);
		switch(spell.getEffect()) {
		case LEVITATE: conditions.add(Condition.LEVITATE); break;
		case PARALYZE: conditions.add(Condition.PARALYZED); break;
		case BLIND: conditions.add(Condition.BLIND); break;
		case CALM: conditions.add(Condition.CALM); break;
		default: break;
		}
	}
	
	/**
	 * @return	a list of active spells
	 */
	public ArrayList<Spell> getActiveSpells() {
		return spells;
	}
	
	/**
	 * Removes a spell from the list of active spells.
	 * 
	 * @param spell	the spell to remove
	 */
	public void removeActiveSpell(Spell spell) {
		spells.remove(spell);
	}

	/**
	 * Sets a skill to a certain value
	 * 
	 * @param skill	the skill
	 * @param value	the new skill value
	 */
	public void setSkill(Skill skill, float value) {
		skills.put(skill, value);
	}
	
	public void restoreSkill(Skill skill, int value) {
		skills.put(skill, Math.min(species.skills.get(skill), skills.get(skill) + value));
	}

	/**
	 * Sets the name of this creature.
	 * 
	 * @param name	the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
/*
 * hier alle getters
 * 
 */	
	/**
	 * Returns the level of this creature. The level is based on a weighted 
	 * average of this creature's attributes.
	 * 
	 * @return 	this creature's level
	 */
	public int getLevel() {
		return Math.max(1, (int)(species.str + species.iq + species.dex + species.con + species.cha + species.wis)/6);
	}
	
	/**
	 * @return	this creature's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return	this creature's name
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * @return	this creature's gender
	 */
	public Gender getGender() {
		return gender;
	}

	/** 
	 * @param skill	the skill to check
	 * @return		the skill check
	 */
	public int getSkill(Skill skill) {
		if(skill == null) {
			return Integer.MAX_VALUE;
		} else {
			return skills.get(skill).intValue();
		}
	}
	
	/**
	 * @return	this creature's list of skills
	 */
	public EnumMap<Skill, Float> getSkills() {
		return skills;		
	}

	public boolean hasDialog() {
		return false;
	}
}
