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

package neon.magic;

import neon.entities.property.Ability;
import neon.entities.property.Damage;
import neon.entities.property.Skill;

/**
 * Represents a magic effect. Every effect should belong to one of six magic
 * schools: alteration, restoration, illusion, destruction, conjuration or 
 * creation. These schools are skills a character can have.
 * 
 * Every spell can have a 'duration type': instant, per round and timed. Instant 
 * means that it is an instantaneous effect. Per round means that it can last 
 * several rounds, the effect being applied every round. Timed means a constant 
 * effect that wears of after a certain time.
 * 
 * @author 	mdriesen
 */
public enum Effect {
	// alteration effects
	LEVITATE(Skill.ALTERATION, .2f, "levitate", new DefaultHandler(), 2), 
	OPEN(Skill.ALTERATION, .2f, "open", new LockHandler(), 0), 
	LOCK(Skill.ALTERATION, .2f, "lock", new LockHandler(), 0), 
	DISARM(Skill.ALTERATION, .2f, "disarm", new LockHandler(), 0),
	LEECH_HEALTH(Skill.ALTERATION, .5f, "leech health", new LeechHandler(Damage.HEALTH), 1), 
	LEECH_MANA(Skill.ALTERATION, .5f, "leech mana", new LeechHandler(Damage.MANA), 1), 
	
	// restoration effects
	RESTORE_HEALTH(Skill.RESTORATION, .1f, "restore health", new RestoreHandler(Damage.HEALTH), 1), 
	RESTORE_MANA(Skill.RESTORATION, .9f, "restore mana", new RestoreHandler(Damage.MANA), 1), 
	CURE_DISEASE(Skill.RESTORATION, .3f, "cure disease", new CureHandler(), 0), 
	CURE_PARALYZATION(Skill.RESTORATION, .4f, "cure paralyzation", new CureHandler(), 0), 
	CURE_POISON(Skill.RESTORATION, .2f, "cure poison", new CureHandler(), 0), 
	LIFT_CURSE(Skill.RESTORATION, .4f, "lift curse", new CureHandler(), 0), 
	CURE_BLINDNESS(Skill.RESTORATION, .5f, "cure blindness", new CureHandler(), 0),
	
	// illusion effects
	PARALYZE(Skill.ILLUSION, 1f, "paralyze", new DefaultHandler(), 2), 
	BLIND(Skill.ILLUSION, .5f, "blind", new DefaultHandler(), 2), 
	BURDEN(Skill.ILLUSION, .3f, "burden", new DefaultHandler(), 2), 
	CALM(Skill.ILLUSION, .2f, "calm", new DefaultHandler(), 2), 
	CHARM(Skill.ILLUSION, .3f, "charm", new DefaultHandler(), 2),
	SILENCE(Skill.ILLUSION, .3f, "silence", new DefaultHandler(), 2), 
	
	// creation effects
	
	// conjuration effects
	FIRE_SHIELD(Skill.CONJURATION, .3f, "fire shield", new ShieldHandler(Ability.FIRE_RESISTANCE), 2), 
	FROST_SHIELD(Skill.CONJURATION, .3f, "frost shield", new ShieldHandler(Ability.COLD_RESISTANCE), 2), 
	SHOCK_SHIELD(Skill.CONJURATION, .3f, "shock shield", new ShieldHandler(Ability.SHOCK_RESISTANCE), 2),
	
	// destruction effects
	DRAIN_INTELLIGENCE(Skill.DESTRUCTION, .7f, "drain intelligence", new DrainStatHandler("int"), 2), 
	DRAIN_STRENGTH(Skill.DESTRUCTION, .7f, "drain strength", new DrainStatHandler("str"), 2), 
	DRAIN_DEXTERITY(Skill.DESTRUCTION, .7f, "drain dexterity", new DrainStatHandler("dex"), 2), 
	DRAIN_CHARISMA(Skill.DESTRUCTION, .7f, "drain charisma", new DrainStatHandler("cha"), 2),
	DRAIN_WISDOM(Skill.DESTRUCTION, .7f, "drain wisdom", new DrainStatHandler("wis"), 2), 
	DRAIN_CONSTITUTION(Skill.DESTRUCTION, .7f, "drain constitution", new DrainStatHandler("con"), 2), 
	DRAIN_CREATION(Skill.DESTRUCTION, .5f, "drain creation", new DrainSkillHandler(Skill.CREATION), 2), 
	DRAIN_DESTRUCTION(Skill.DESTRUCTION, .5f, "drain destruction", new DrainSkillHandler(Skill.DESTRUCTION), 2), 
	DRAIN_RESTORATION(Skill.DESTRUCTION, .5f, "drain restoration", new DrainSkillHandler(Skill.RESTORATION), 2), 
	DRAIN_ALTERATION(Skill.DESTRUCTION, .5f, "drain alteration", new DrainSkillHandler(Skill.ALTERATION), 2),
	DRAIN_ILLUSION(Skill.DESTRUCTION, .5f, "drain illusion", new DrainSkillHandler(Skill.ILLUSION), 2), 
	DRAIN_ALCHEMY(Skill.DESTRUCTION, .5f, "drain alchemy", new DrainSkillHandler(Skill.ALCHEMY), 2), 
	DRAIN_ENCHANT(Skill.DESTRUCTION, .5f, "drain enchanting", new DrainSkillHandler(Skill.ENCHANT), 2), 
	DRAIN_ARCHERY(Skill.DESTRUCTION, .5f, "drain archery", new DrainSkillHandler(Skill.ARCHERY), 2), 
	DRAIN_AXE(Skill.DESTRUCTION, .5f, "drain axe", new DrainSkillHandler(Skill.AXE), 2), 
	DRAIN_BLUNT(Skill.DESTRUCTION, .5f, "drain blunt", new DrainSkillHandler(Skill.BLUNT), 2),
	DRAIN_SPEAR(Skill.DESTRUCTION, .5f, "drain spear", new DrainSkillHandler(Skill.SPEAR), 2), 
	DRAIN_BLADE(Skill.DESTRUCTION, .5f, "drain blade", new DrainSkillHandler(Skill.BLADE), 2), 
	DRAIN_CLIMBING(Skill.DESTRUCTION, .5f, "drain climbing", new DrainSkillHandler(Skill.CLIMBING), 2), 
	DRAIN_SWIMMING(Skill.DESTRUCTION, .5f, "drain swimming", new DrainSkillHandler(Skill.SWIMMING), 2), 
	DRAIN_HEAVY_ARMOR(Skill.DESTRUCTION, .5f, "drain heavy armor", new DrainSkillHandler(Skill.HEAVY_ARMOR), 2), 
	DRAIN_MEDIUM_ARMOR(Skill.DESTRUCTION, .5f, "drain medium armor", new DrainSkillHandler(Skill.MEDIUM_ARMOR), 2), 
	DRAIN_LIGHT_ARMOR(Skill.DESTRUCTION, .5f, "drain light armor", new DrainSkillHandler(Skill.LIGHT_ARMOR), 2), 
	DRAIN_DODGING(Skill.DESTRUCTION, .5f, "drain dodging", new DrainSkillHandler(Skill.DODGING), 2), 
	DRAIN_BLOCK(Skill.DESTRUCTION, .5f, "drain block", new DrainSkillHandler(Skill.BLOCK), 2), 
	DRAIN_MERCANTILE(Skill.DESTRUCTION, .5f, "drain mercantile", new DrainSkillHandler(Skill.MERCANTILE), 2),
	DRAIN_PICKPOCKET(Skill.DESTRUCTION, .5f, "drain pickpocket", new DrainSkillHandler(Skill.PICKPOCKET), 2), 
	DRAIN_ARMORER(Skill.DESTRUCTION, .5f, "drain armorer", new DrainSkillHandler(Skill.ARMORER), 2), 
	DRAIN_LOCKPICKING(Skill.DESTRUCTION, .5f, "drain lockpicking", new DrainSkillHandler(Skill.LOCKPICKING), 2), 
	DRAIN_HEALTH(Skill.DESTRUCTION, .2f, "drain health", new DrainHandler(Damage.HEALTH), 1), 
	DRAIN_MANA(Skill.DESTRUCTION, .2f, "drain mana", new DrainHandler(Damage.MANA), 1),
	DAMAGE_HEALTH(Skill.DESTRUCTION, 1f, "damage health", new DamageHandler(Damage.HEALTH), 0), 
	DAMAGE_MANA(Skill.DESTRUCTION, 1f, "damage mana", new DamageHandler(Damage.MANA), 0),
	FIRE_DAMAGE(Skill.DESTRUCTION, .1f, "fire damage", new DamageHandler(Damage.FIRE), 1), 
	FROST_DAMAGE(Skill.DESTRUCTION, .1f, "frost damage", new DamageHandler(Damage.FROST), 1), 
	SHOCK_DAMAGE(Skill.DESTRUCTION, .1f, "shock damage", new DamageHandler(Damage.FIRE), 1),
	
	// andere effects
	SCRIPTED(null, 0, "scripted", new DefaultHandler(), 0);
	
	// de duur van een spell effect
	public static final int INSTANT = 0;
	public static final int REPEAT = 1;
	public static final int TIMED = 2;
	
	private final Skill school;
	private final int duration;
	private final float mana;
	private final String name;
	private final EffectHandler handler;

	/**
	 * Initializes a spell effect belonging to the given school.
	 * 
	 * @param school	school of this particular spell effect
	 */
	Effect(Skill school, float mana, String name, EffectHandler handler, int duration) {
		this.school = school;
		this.duration = duration;
		this.mana = mana;
		this.name = name;
		this.handler = handler;
	}


	/**
	 * Returns the school this spell belong to.
	 * 
	 * @return	the school this spell belongs to.
	 */
	public Skill getSchool() {
		return school;
//		return handler.getSchool();
	}
	
	/**
	 * @return	the duration class of this skill
	 */
	public int getDuration() {
		return duration;
	}
	
	/**
	 * @return	the mana needed for one unit of this effect
	 */
	public float getMana() {
		return mana;
	}
	
	public EffectHandler getHandler() {
		return handler;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
