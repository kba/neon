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

import neon.util.Dice;

public class HealthComponent implements Component {
	private int health;						// health/hit points
	private float healthMod, baseHealthMod;	// verschil t.o.v. base health
	private final long uid;

	public HealthComponent(long uid, String hit) {
		this.uid = uid;
		health = Dice.roll(hit);
		healthMod = 0;
		baseHealthMod = 0;
	}
	
	/**
	 * Heals this creature.
	 * 
	 * @param amount	the amount to add to health
	 */
	public void heal(float amount) {
		healthMod = Math.min(0, healthMod + amount);
	}
	
	/**
	 * Sets the base health of this creature to a new value.
	 * 
	 * @param health	the new amount of health
	 */
	public void setHealth(int health) {
		this.health = health;
	}
	
	/**
	 * Returns the base health of this creature. It is the maximum health this creature can
	 * have without using magical or other means.
	 * 
	 * @return	this creature's base health
	 */
	public int getBaseHealth() {
		return health;
	}
	
	/**
	 * @return	this creature's base health modifier
	 */
	public float getBaseHealthMod() {
		return baseHealthMod;
	}
	
	/**
	 * @return	this creature's health modifier
	 */
	public float getHealthMod() {
		return healthMod;
	}
	
	/**
	 * Returns the current health of this creature. It is the base health, modified
	 * with magical effects, battle damage, etc.
	 * 
	 * @return this creature's health
	 */
	public int getHealth() {
		return (int)(health + baseHealthMod + healthMod);
	}
	
	/**
	 * Adds to the base health modifier, lowering or raising the maximum attainable
	 * total health.
	 * 
	 * @param value	the amount to add to the base health modifier
	 */
	public void addBaseHealthMod(float value) {
		baseHealthMod += value;
	}

	/**
	 * Adds a certain amount to the base health.
	 * 
	 * @param amount	the amount to add
	 */
	public void addBaseHealth(int amount) {
		health += amount;
	}
	
	@Override
	public long getUID() {
		return uid;
	}
}
