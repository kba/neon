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

package neon.resources;

import org.jdom2.Element;

public class RWeapon extends RItem {
	public enum WeaponType {
		BOW("bow"), CROSSBOW("crossbow"), ARROW("arrow"), BOLT("bolt"), THROWN("thrown weapon"), 
		BLADE_ONE("one-handed blade"), BLADE_TWO("two-handed blade"), UNARMED("unarmed"),
		BLUNT_ONE("one-handed blunt weapon"), BLUNT_TWO("two-handed blunt weapon"), SPEAR("spear"), 
		AXE_ONE("one-handed axe"), AXE_TWO("two-handed axe");
		
		private String description;
		
		private WeaponType(String description) {
			this.description = description;
		}
		
		public String toString() {
			return description;
		}
	}

	// algemene dingen
	public String damage;
	public WeaponType weaponType;

	// enchantment
	public int mana;

	public RWeapon(String id, Type type, String... path) {
		super(id, type, path);
	}
	
	public RWeapon(Element weapon, String... path) {
		super(weapon, path);
		damage = weapon.getAttributeValue("dmg");
		weaponType = WeaponType.valueOf(weapon.getAttributeValue("type").toUpperCase());
		if(weapon.getAttribute("mana") != null) {
			mana = Integer.parseInt(weapon.getAttributeValue("mana"));
		}
	}
	
	public Element toElement() {
		Element weapon = super.toElement();
		weapon.setAttribute("dmg", damage);
		weapon.setAttribute("type", weaponType.name());
		if(mana > 0) {
			weapon.setAttribute("mana", Integer.toString(mana));
		}
		return weapon;
	}
	
	public boolean isRanged() {
		return weaponType == WeaponType.BOW || weaponType == WeaponType.CROSSBOW || weaponType == WeaponType.THROWN;
	}
}
