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

import neon.core.handlers.SkillHandler;
import neon.entities.Clothing;
import neon.entities.Creature;
import neon.entities.components.Enchantment;
import neon.resources.RSpell;

public class MagicUtils {
	/**
	 * This method returns a magic skill check, based on the type of spell.
	 * 
	 * @param creature	the creature that casts the spell
	 * @param spell		the spell to cast
	 * @return		a magic skill check
	 */
	public static int check(Creature creature, RSpell spell) {
		return SkillHandler.check(creature, spell.effect.getSchool());
	}

	/**
	 * Returns the amount of mana that the given spell takes to cast. 
	 * 
	 * @param formula	the requested spell
	 * @return	the mana needed for casting
	 */
	public static float getMana(RSpell formula) {
		float mana = formula.effect.getMana();
		return mana * (formula.size + 1)/3 * (formula.range + 1)/3 * (formula.duration + 1)/3;
	}
	
	/**
	 * Returns the level of the given spell. The highest level someone can cast depends on the 
	 * corresponding skill.
	 * 
	 * @param spell	the requested spell
	 * @return		the level of the spell
	 */
	public static int getLevel(RSpell spell) {
		return (int)getMana(spell)/10;
	}
	
	/**
	 * Returns the cost of the given spell. 
	 * 
	 * @param spell	the requested spell
	 * @return		the cost in copper pieces
	 */
	public static int getCost(RSpell spell) {
		return (int)getMana(spell)*10;
	}
	
	/**
	 * Heals a creature from the given ailment type.
	 * 
	 * @param target	the creature to be healed
	 * @param type		disease, poison or curse
	 */
	public static void cure(Creature target, RSpell.SpellType type) {
		for(Spell spell : target.getActiveSpells()) {
			if(spell.getType() == type) {
				removeSpell(target, spell);
			}
		}
	}

	/**
	 * Removes the given spell from the given creature.
	 * 
	 * @param creature	a creature
	 * @param spell		the spell that has to be removed
	 */
	public static void removeSpell(Creature creature, Spell spell) {
		creature.removeActiveSpell(spell);
		spell.getHandler().removeEffect(spell);
	}
	
	/**
	 * Checks whether a {@code Creature} is affected by the given {@code Effect}.
	 * 
	 * @param creature
	 * @param effect
	 * @return
	 */
	protected static boolean hasActiveEffect(Creature creature, Effect effect) {
		for(Spell spell : creature.getActiveSpells()) {
			if(spell.getEffect().equals(effect)) {
				return true;
			}
		}
		return false;
	}		

	/**
	 * Handles the consequences of equiping a magic item.
	 * 
	 * @param creature
	 * @param item
	 */
	public static void equip(Creature creature, Clothing item) {
		Enchantment enchantment = item.getComponent(Enchantment.class);
		Spell spell = new Spell(enchantment.getSpell(), 0, creature, null);
		spell.getHandler().addEffect(spell);
	}

	/**
	 * Handles the consequences of unequiping a magic item.
	 * 
	 * @param creature
	 * @param item
	 */
	public static void unequip(Creature creature, Clothing item) {
		Enchantment enchantment = item.getComponent(Enchantment.class);
		Spell spell = new Spell(enchantment.getSpell(), 0, creature, null);
		spell.getHandler().removeEffect(spell);
	}
}
