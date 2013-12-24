/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2011 - Maarten Driesen
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

package neon.entities.property;

/**
 * Represents a clothing slot. These are basically all the different things
 * a character can 'wear'.
 * 
 * @author 	mdriesen
 */
public enum Slot {
	// armor (gauntlets includes bracers)
	HELMET(0.1f), PAULDRONS(0.2f), GAUNTLETS(0.1f), 
	CUIRASS(0.4f), CHAUSSES(0.1f), BOOTS(0.1f), SHIELD(0.2f),
	
	// juwelen
	AMULET, RING, RING_LEFT, RING_RIGHT, BELT, 
	
	// kleding
	SHIRT, PANTS, CLOAK, GLOVES, SHOES,
	
	// wapens
	AMMO, WEAPON,
	
	// magic?
	MAGIC;
	
	private final float mod;
	
	Slot() {
		this(0);
	}
	
	Slot(float mod) {
		this.mod = mod;
	}
	
	public float getArmorModifier() {
		return mod;
	}
}
