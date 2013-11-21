/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012 - mdriesen
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

package neon.core.handlers;

import neon.core.Engine;
import neon.entities.Armor;
import neon.entities.Creature;
import neon.entities.Entity;
import neon.entities.Weapon;
import neon.entities.components.Inventory;
import neon.entities.property.Skill;
import neon.entities.property.Slot;
import neon.resources.RClothing;
import neon.resources.RWeapon.WeaponType;
import neon.util.Dice;

public class CombatUtils {
	/**
	 * Does an attack roll. This is used to determine if this creature is
	 * able to hit anything with an attack.
	 * 
	 * @return 	an attack roll
	 */
	protected static int attack(Creature creature) {
		switch(getWeaponType(creature)) {
		case BLADE_ONE:
		case BLADE_TWO:
			return SkillHandler.check(creature, Skill.BLADE);
		case BLUNT_ONE:
		case BLUNT_TWO:
			return SkillHandler.check(creature, Skill.BLUNT);
		case AXE_ONE:
		case AXE_TWO:
			return SkillHandler.check(creature, Skill.AXE);
		case SPEAR:
			return SkillHandler.check(creature, Skill.SPEAR);
		case BOW:
		case CROSSBOW:
		case THROWN:
			return SkillHandler.check(creature, Skill.ARCHERY);
		default:
			return SkillHandler.check(creature, Skill.UNARMED);
		}
	}

	/**
	 * The attack value determines how much damage this creature's weapons
	 * can deal.
	 * 
	 * @return	the attack value
	 */
	protected static int getAV(Creature creature) {
		Inventory inventory = creature.getInventoryComponent();
		
		int damage;
		if(inventory.hasEquiped(Slot.WEAPON)) {
			Weapon weapon = (Weapon)Engine.getStore().getEntity(inventory.get(Slot.WEAPON));
			damage = Dice.roll(weapon.getDamage());
			if(weapon.getWeaponType().equals(WeaponType.BOW) || 
					weapon.getWeaponType().equals(WeaponType.CROSSBOW)) {
				Weapon ammo = (Weapon)Engine.getStore().getEntity(inventory.get(Slot.AMMO));
				damage = (damage + Dice.roll(ammo.getDamage()))/2;
			}	
		} else if(inventory.hasEquiped(Slot.AMMO)) {
			Weapon ammo = (Weapon)Engine.getStore().getEntity(inventory.get(Slot.AMMO));
			damage = Dice.roll(ammo.getDamage());
		} else {
			damage = Dice.roll(creature.species.av);
		}
		
		float mod = 1f;
		switch(getWeaponType(creature)) {
		case BLADE_ONE:
		case BLADE_TWO:
		case BLUNT_ONE:
		case BLUNT_TWO:
		case AXE_ONE:
		case AXE_TWO:
		case SPEAR:
			mod = creature.species.dex/20; break;
		case BOW:
		case CROSSBOW:
		case THROWN:
			mod = creature.species.str/20; break;
		default: break;
		}
		return (int)(damage*mod);
	}
	
	/**
	 * Does a block skill check. It is used to determine if this creature
	 * can block an attack. When no shield or similar protective gear is equiped,
	 * this method returns 0;
	 * 
	 * @return	a block skill check
	 */
	protected static int block(Creature creature) {
		if(creature.getInventoryComponent().hasEquiped(Slot.SHIELD)) {
			float mod = 1f;
			Armor armor = (Armor)Engine.getStore().getEntity(creature.getInventoryComponent().get(Slot.SHIELD));
			switch(((RClothing)(armor.resource)).kind) {
			case LIGHT: mod = creature.getSkill(Skill.LIGHT_ARMOR)/20f; break;
			case MEDIUM: mod = creature.getSkill(Skill.MEDIUM_ARMOR)/20f; break;
			case HEAVY: mod = creature.getSkill(Skill.HEAVY_ARMOR)/20f; break;
			default: break;
			}
			
			return (int)(SkillHandler.check(creature, Skill.BLOCK) * mod);
		} else {
			return 0;
		}
	}

	/**
	 * Does a dodge skill check. It is used to determine if this creature can
	 * dodge an attack.
	 * 
	 * @return	a dodge skill check
	 */
	protected static int dodge(Creature creature) {
		return SkillHandler.check(creature, Skill.DODGING);
	}
	
	/**
	 * The defense value determines how much damage this creature can take 
	 * without losing health.
	 * 
	 * @return	the defense value
	 */
	public static int getDV(Creature creature) {
		float AR = creature.species.dv;
		for(Slot s : creature.getInventoryComponent().slots()) {
			Entity item = Engine.getStore().getEntity(creature.getInventoryComponent().get(s));
			if(item instanceof Armor) {
				Armor c = (Armor)item;
				int mod = 0;
				switch(((RClothing)c.resource).kind) {
				case LIGHT:	mod = 1 + creature.getSkill(Skill.LIGHT_ARMOR)/20; break;
				case MEDIUM: mod = 1 + creature.getSkill(Skill.MEDIUM_ARMOR)/20; break;			
				case HEAVY: mod = 1 + creature.getSkill(Skill.HEAVY_ARMOR)/20; break;
				default: break;
				}
				AR += ((RClothing)c.resource).rating * s.getArmorModifier() * mod;
			}
		}
		return (int)AR;
	}
	
	/**
	 * @param creature
	 * @return	the type of the currently equiped weapon
	 */
	public static WeaponType getWeaponType(Creature creature) {
		Inventory inventory = creature.getInventoryComponent();
		if(inventory.hasEquiped(Slot.WEAPON)) {
			Weapon weapon = (Weapon)Engine.getStore().getEntity(inventory.get(Slot.WEAPON));
			return (weapon.getWeaponType());
		} else if(inventory.hasEquiped(Slot.AMMO) && 
				((Weapon)Engine.getStore().getEntity(inventory.get(Slot.AMMO))).getWeaponType() == WeaponType.THROWN) {
			return WeaponType.THROWN;
		} else {
			return WeaponType.UNARMED;
		}
	}
}
