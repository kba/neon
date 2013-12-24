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

import neon.resources.RCreature;

public class Stats implements Component {
	private final long uid;
	private final RCreature species;
	
	private int strMod, conMod, dexMod, intMod, wisMod, chaMod, spdMod;	

	public Stats(long uid, RCreature species) {
		this.uid = uid;
		this.species = species;
	}
	
	public void addSpeed(int i) {
		spdMod++;
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
	 * @return	this creature's speed
	 */
	public int getSpd() {
		return (int)species.speed + spdMod;
	}
	
	@Override
	public long getUID() {
		return uid;
	}

}
