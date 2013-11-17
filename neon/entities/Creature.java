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
import neon.magic.*;
import neon.resources.RCreature;
import neon.ai.AI;
import neon.core.Engine;
import neon.entities.components.Animus;
import neon.entities.components.CreatureRenderComponent;
import neon.entities.components.FactionComponent;
import neon.entities.components.HealthComponent;
import neon.entities.components.Inventory;
import neon.entities.property.*;

/**
 * This class implements a general creature. Specific types of creatures can
 * be subclassed from this class.
 * 
 * @author mdriesen
 */
public class Creature extends Entity {
	// componenten
	public final RCreature species;
	public final Inventory inventory;
	public final Animus animus;
	public final FactionComponent factions;
	public final HealthComponent health;
	public AI brain;	// kan niet final (npc probleem)
	
	// allerlei
	protected Gender gender;
	protected String name;
	
	// lijstjes
	protected EnumMap<Skill, Float> skills;
	protected ArrayList<Spell> spells;			// active spells
	protected Set<Feat> feats;
	protected Set<Trait> traits;
	protected EnumMap<Ability, Integer> abilities;
	protected Set<Condition> conditions;
	
	// character attributen
	private int strMod, conMod, dexMod, intMod, wisMod, chaMod, spdMod;	
	private int date = 0;						// time of death
	private int money = 0;
	
	/**
	 * Initialize a creature with the given data.
	 * 
	 * @param id	the creature's id
	 * @param uid	the creature's uid
	 */
	public Creature(String id, long uid, RCreature species) {
		super(id, uid);
		
		this.species = species;
		animus = new Animus(this);
		renderer = new CreatureRenderComponent(this);
		factions = new FactionComponent(uid);
		health = new HealthComponent(uid, species.hit);

		// dit eerst
		gender = Gender.OTHER;
		name = species.getName();
		
		// dan dit
		inventory = new Inventory(uid);

		// collections initialiseren
		spells = new ArrayList<Spell>();
		skills = new EnumMap<Skill, Float>(species.skills);
		conditions = EnumSet.noneOf(Condition.class);
		feats = EnumSet.noneOf(Feat.class);
		traits = EnumSet.noneOf(Trait.class);
		abilities = new EnumMap<Ability, Integer>(Ability.class);		
	}
	
	public Creature(String id, long uid, String name, RCreature species) {
		this(id, uid, species);
		this.name = name;
	}

	public void addSpeed(int i) {
		spdMod++;
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
	 * Adds a certain amount to the strength attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addStr(int amount) {
		strMod += amount;
	}
	
	/**
	 * Adds a certain amount to the dexterity attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addDex(int amount) {
		dexMod += amount;
	}
	
	/**
	 * Adds a certain amount to the constitution attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addCon(int amount) {
		conMod += amount;
	}
	
	/**
	 * Adds a certain amount to the charisma attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addCha(int amount) {
		chaMod += amount;
	}
	
	/**
	 * Adds a certain amount to the wisdom attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addWis(int amount) {
		wisMod += amount;
	}
	
	/**
	 * Adds a certain amount to the intelligence attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addInt(int amount) {
		intMod += amount;
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
	 * @return	this creature's strength
	 */
	public int getStr() {
		return (int)species.str + strMod;
	}

	/**
	 * @return	this creature's constitution
	 */
	public int getCon() {
		return (int)species.con + conMod;
	}

	/**
	 * @return	this creature's dexterity
	 */
	public int getDex() {
		return (int)species.dex + dexMod;
	}

	/**
	 * @return	this creature's intelligence
	 */
	public int getInt() {
		return (int)species.iq + intMod;
	}

	/**
	 * @return	this creature's wisdom
	 */
	public int getWis() {
		return (int)species.wis + wisMod;
	}

	/**
	 * @return	this creature's charisma
	 */
	public int getCha() {
		return (int)species.cha + chaMod;
	}
	
	/**
	 * @return	this creature's currently equiped weapon
	 */
	public Weapon getWeapon() {
		return (Weapon)Engine.getStore().getEntity(inventory.get(Slot.WEAPON));
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
	 * @return	this creature's weight
	 */
	public int getWeight() {
		float sum = 0;
		for(long uid : inventory) {
			sum += ((Item)Engine.getStore().getEntity(uid)).resource.weight;
		}
		// in geval van 'burden' spell
		for(Spell s : spells) {
			if(s.getEffect() == Effect.BURDEN) {
				sum += s.getMagnitude();
			}
		}
		return (int)sum;
	}

/*
 * alle acties. 
 */
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
	
	public int getSpeed() {
		int penalty = 3;
		if(getWeight() > 9*species.str) {
			return 0;
		} else if(getWeight() > 6*species.str) {
			penalty = 1;
		} else if(getWeight() > 3*species.str) {
			penalty = 2;
		}
		return (species.speed + spdMod)*penalty/3;
	}
		
	public int getMoney() {
		return money;
	}
	
	public void addMoney(int amount) {
		money += amount;
	}
	
	public boolean hasDialog() {
		return false;
	}
}
