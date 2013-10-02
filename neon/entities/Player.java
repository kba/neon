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

import java.util.EnumMap;

import neon.core.Engine;
import neon.core.handlers.SkillHandler;
import neon.entities.components.Lock;
import neon.entities.components.PRenderer;
import neon.entities.property.Gender;
import neon.entities.property.Skill;
import neon.entities.property.Slot;
import neon.narrative.Journal;
import neon.resources.RCreature;
import neon.resources.RWeapon.WeaponType;

public class Player extends Hominid {
	private final int baseLevel;
	private Journal journal = new Journal();	
	private Specialisation spec;
	private String profession;
	private EnumMap<Skill, Float> mods;
	private String sign;
	private boolean sneak = false;
	private Creature mount;

	public Player(RCreature species, String name, Gender gender, Specialisation spec, String profession) {
		super(species.id, 0, species); 
		renderer = new PRenderer(this);
		this.name = name;
		this.gender = gender;
		this.spec = spec;
		this.profession = profession;
		baseLevel = getLevel();
		mods = new EnumMap<Skill, Float>(Skill.class);
		for(Skill skill : Skill.values()) {
			mods.put(skill, 0f);
		}
	}
	
	@Override
	public String getID() {
		return "player";
	}
	
/*
 * allerlei actions die de player kan ondernemen en niet in een aparte handler staan
 */	
	public boolean pickLock(Lock lock) {
		if(SkillHandler.check(this, Skill.LOCKPICKING) > lock.getLockDC()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setSneaking(boolean sneaking) {
		sneak = sneaking;
	}
	
	public boolean isSneaking() {
		return sneak;
	}
	
	public String getAVString() {
		String damage;
		
		if(inventory.hasEquiped(Slot.WEAPON)) {
			Weapon weapon = (Weapon)Engine.getStore().getEntity(inventory.get(Slot.WEAPON));
			damage = weapon.getDamage();
			if(weapon.getWeaponType().equals(WeaponType.BOW) || 
					weapon.getWeaponType().equals(WeaponType.CROSSBOW)) {
				Weapon ammo = (Weapon)Engine.getStore().getEntity(inventory.get(Slot.AMMO));
				damage += " : " + ammo.getDamage();
			}	
		} else if(inventory.hasEquiped(Slot.AMMO)) {
			Weapon ammo = (Weapon)Engine.getStore().getEntity(inventory.get(Slot.AMMO));
			damage = ammo.getDamage();
		} else {
			damage = species.av;
		}
		
		return damage;
	}
	
	/**
	 * @return	the player's journal
	 */
	public Journal getJournal() {
		return journal;
	}
	
	// algehele level van de player. Wordt berekend a.d.h.v. de stats
	@Override
	public int getLevel() {
		int mmod = 1;
		int cmod = 1;
		int smod = 1;
		switch(spec) {
		case magic:	mmod = 2; break;
		case stealth: smod = 2; break;
		case combat: cmod = 2; break;
		}
		return Math.max(0, (int)(cmod*species.str + mmod*species.iq + smod*species.dex + 
				cmod*species.con + smod*species.cha + mmod*species.wis)/8 - baseLevel);
	}
	
	
	
	public Specialisation getSpecialisation() {
		return spec;
	}
	
	public float getFloatSkill(Skill skill) {
		return skills.get(skill);
	}
	
	/**
	 * Adds a certain amount to the speed attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseHealth(int amount) {
		health += amount;
	}
	
	/**
	 * Adds a certain amount to the speed attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseSpd(float amount) {
		species.speed += amount;
	}
	
	/**
	 * Adds a certain amount to the strength attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseStr(float amount) {
		species.str += amount;
	}
	
	/**
	 * Adds a certain amount to the dexterity attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseDex(float amount) {
		species.dex += amount;
	}
	
	/**
	 * Adds a certain amount to the constitution attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseCon(float amount) {
		species.con += amount;
	}
	
	/**
	 * Adds a certain amount to the charisma attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseCha(float amount) {
		species.cha += amount;
	}
	
	/**
	 * Adds a certain amount to the wisdom attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseWis(float amount) {
		species.wis += amount;
	}
	
	/**
	 * Adds a certain amount to the intelligence attribute.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseInt(float amount) {
		species.iq += amount;
	}
	
	public String getProfession() {
		return profession;
	}
	
	public enum Specialisation {
		combat, magic, stealth;
	}
	
	public void trainSkill(Skill skill, float mod) {
		mods.put(skill, mods.get(skill) + mod);
		skills.put(skill, skills.get(skill) + mod);
	}
	
	@Override
	public void restoreSkill(Skill skill, int value) {
		skills.put(skill, Math.min(species.skills.get(skill) + mods.get(skill), skills.get(skill) + value));
	}
	
	public String getSign() {
		return sign;
	}
	
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public void mount(Creature mount) {
		this.mount = mount;
	}
	
	public void unmount() {
		mount = null;
	}
	
	public Creature getMount() {
		return mount;
	}
	
	public boolean isMounted() {
		return mount != null;
	}
}
