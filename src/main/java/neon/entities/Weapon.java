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

package neon.entities;

import neon.entities.property.Slot;
import neon.resources.RWeapon;
import neon.resources.RWeapon.WeaponType;

public class Weapon extends Item {
	private int state = 100;
	
	public Weapon(long uid, RWeapon resource) {
		super(uid, resource);
	}
	
	/**
	 * @return	the damage this weapon does
	 */
	public String getDamage() {
		return ((RWeapon)resource).damage;
	}
	
	/**
	 * @return	the type of this weapon
	 */
	public WeaponType getWeaponType() {
		return ((RWeapon)resource).weaponType;
	}
	
	/**
	 * @return	the state this weapon is in
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Sets the state this weapon is in.
	 * 
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * @return	the item slot this weapon occupies when equiped
	 */
	public Slot getSlot() {
		if(((RWeapon)resource).weaponType.equals(WeaponType.ARROW) || ((RWeapon)resource).weaponType.equals(WeaponType.BOLT)) {
			return Slot.AMMO;
		} else {
			return Slot.WEAPON;
		}
	}
	
	/**
	 * @return	whether this weapon is ranged or not
	 */
	public boolean isRanged() {
		return ((RWeapon)resource).isRanged();
	}
}
