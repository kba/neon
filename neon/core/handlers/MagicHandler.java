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

package neon.core.handlers;

import java.awt.Rectangle;
import java.awt.Point;
import java.util.Collection;
import neon.magic.*;
import neon.resources.RSpell;
import neon.util.Dice;
import neon.core.Engine;
import neon.core.event.MagicTask;
import neon.entities.Creature;
import neon.entities.Item;
import neon.entities.components.Characteristics;
import neon.entities.components.Enchantment;
import neon.entities.property.Ability;
import neon.entities.property.Condition;
import neon.entities.property.Skill;

/**
 * This class handles all magic casting. 
 */
public class MagicHandler {	// TODO: ombouwen naar event system
	public static final int RANGE = 0;		// verkeerde range
	public static final int NULL = 1;		// geen target geselecteerd
	public static final int MANA = 2;		// te weinig mana of charge (voor items)
	public static final int TARGET = 3;		// verkeerd type target (item of creature)
	public static final int SKILL = 4;		// skill check niet gelukt
	public static final int NONE = 5;		// geen spell equiped
	public static final int OK = 6;			// casten ok
	public static final int LEVEL = 7;		// spell te moeilijk
	public static final int SILENCED = 8;	// caster silenced
	public static final int INTERVAL = 9;	// power interval niet gedaan
	
	/**
	 * Casts a spell on a target creature. Used by traps.
	 * 
	 * @param target
	 */
	public static void cast(RSpell spell, Creature target) {
		castSpell(target, null, spell);
	}
	
	/**
	 * Casts a spell on a target point. Used by traps.
	 * 
	 * @param target
	 */
	public static void cast(RSpell spell, Point target) {
		int radius = spell.radius;
		Rectangle box = new Rectangle(target.x - radius, target.y - radius,
				radius*2 + 1, radius*2 + 1);
		
		if(spell.effect == Effect.SCRIPTED) {
			Engine.execute(spell.script);
		} else if(spell.effect.getHandler().onItem()) {
			Collection<Long> items = Engine.getAtlas().getCurrentZone().getItems(box);
			for(long uid : items) {
				castSpell((Item)Engine.getStore().getEntity(uid), spell);
			}
		} else {
			Collection<Creature> creatures = Engine.getAtlas().getCurrentZone().getCreatures(box);
			for(Creature creature : creatures) {
				castSpell(creature, null, spell);
			}
			if(box.contains(Engine.getPlayer().getShapeComponent())) {
				castSpell(Engine.getPlayer(), null, spell);
			}
		}
	}
	
	/**
	 * This method lets a creature cast a spell on a target.
	 * 
	 * @param caster	the creature casting the spell
	 * @param target	the position of the target
	 * @return	the result of the casting
	 */
	public static int cast(Creature caster, Point target) {
		Rectangle bounds = caster.getShapeComponent();
		RSpell formula = caster.getMagicComponent().getSpell();
		
		if(formula == null) {
			return NONE;	// geen spell/enchantment beschikbaar
		} else if(caster.hasCondition(Condition.SILENCED)) {
			return SILENCED;	// gesilenced
		} else if(target.distance(bounds.getLocation()) > formula.range) {
			return RANGE;	// out of range
		} else {
			if(formula instanceof RSpell.Power) {
				int time = Engine.getTimer().getTime();
				if(caster.getMagicComponent().canUse((RSpell.Power)formula, time)) {
					caster.getMagicComponent().usePower((RSpell.Power)formula, time);
				} else {	// te kort geleden power gecast
					return INTERVAL;
				}
			} else {
				int penalty = checkPenalty(caster);
				int check = caster.getSkill(formula.effect.getSchool());
				if(check < MagicUtils.getLevel(formula)) {
					return LEVEL;	// spell level te hoog
				} else if(!formula.effect.equals(Effect.SCRIPTED) && 
						MagicUtils.check(caster, formula) < 20 + penalty) {
					return SKILL;	// skill check gefaald
				} else if(caster.getMagicComponent().getMana() < MagicUtils.getMana(formula)) {
					return MANA;	// genoeg mana om te casten?
				} else {
					caster.getMagicComponent().addMana(-MagicUtils.getMana(formula));
				}
			}

			// gebied dat door de spel geraakt wordt
			int area = formula.radius;
			Rectangle box = new Rectangle(target.x - area, target.y - area,
					area*2 + 1, area*2 + 1);

			// alle items/creatures binnen bereik
			if(formula.effect == Effect.SCRIPTED) {
				Engine.execute(formula.script);
			} else if(formula.effect.getHandler().onItem()) {
				Collection<Long> items = Engine.getAtlas().getCurrentZone().getItems(box);
				for(long uid : items) {
					castSpell((Item)Engine.getStore().getEntity(uid), formula);
				}
			} else {
				Collection<Creature> creatures = Engine.getAtlas().getCurrentZone().getCreatures(box);
				if(box.contains(Engine.getPlayer().getShapeComponent())) {
					creatures.add(Engine.getPlayer());
				}
				for(Creature creature : creatures) {
					castSpell(creature, caster, formula);
				}
			}
			
			// en resultaat returnen
			return OK;
		}
	}

	/**
	 * This methods lets a creature using a magic item cast a spell on a point.
	 * 
	 * @param caster	the spell caster
	 * @param item		the spell caster's magic item
	 * @param target	the target of the spell
	 * @return	the result of the cast
	 */
	public static int cast(Creature caster, Point target, Item item) {
		Rectangle bounds = caster.getShapeComponent();
		Enchantment enchantment = item.getMagicComponent();
		RSpell formula = null;
		
		if(item instanceof Item.Scroll) {
			formula = enchantment.getSpell();
		}
		
		if(formula == null) {
			return NONE;
		} else if(!(item instanceof Item.Scroll) && MagicUtils.getMana(formula) > enchantment.getMana()) {
			return MANA;			
		} else if(target == null) {
			return NULL;
		} else if(formula.range < target.distance(bounds.getLocation())) {
			return RANGE;
		} else {
			if(item instanceof Item.Scroll) {
				InventoryHandler.removeItem(caster, item.getUID());
			} else {
				enchantment.addMana(-MagicUtils.getMana(formula));				
			}
			
			int area = formula.radius;
			Rectangle box = new Rectangle(target.x - area, target.y - area,
					area*2 + 1, area*2 + 1);
			
			if(formula.effect == Effect.SCRIPTED) {
				Engine.execute(formula.script);
			} else if(formula.effect.getHandler().onItem()) {
				Collection<Long> items = Engine.getAtlas().getCurrentZone().getItems(box);
				for(long uid : items) {
					castSpell((Item)Engine.getStore().getEntity(uid), formula);
				}
			} else {
				Collection<Creature> creatures = Engine.getAtlas().getCurrentZone().getCreatures(box);
				if(box.contains(Engine.getPlayer().getShapeComponent())) {
					creatures.add(Engine.getPlayer());
				}
				for(Creature creature : creatures) {
					castSpell(creature, caster, formula);
				}
			}
			
			// en resultaat returnen
			return OK;
		}
	}
	
	/**
	 * This methods lets a creature using a magic item cast a spell on itself.
	 * 
	 * @param caster	the spell caster
	 * @param item		the spell caster's magic item
	 * @return	the result of the cast
	 */
	public static int cast(Creature caster, Item item) {
		Enchantment enchantment = item.getMagicComponent();
		RSpell formula = null;
		
		if(item instanceof Item.Scroll) {
			formula = enchantment.getSpell();
		}

		if(formula == null) {
			return NONE;
		} else if(!(item instanceof Item.Scroll) && MagicUtils.getMana(formula) > enchantment.getMana()) {
			return MANA;			
		} else if(formula.range > 0) {
			return RANGE;
		} else {
			enchantment.addMana(-MagicUtils.getMana(formula));
			if(item instanceof Item.Scroll) {
				InventoryHandler.removeItem(caster, item.getUID());
			}
			return castSpell(caster, caster, formula);
		}
	}
	
	/**
	 * Lets a creature cast a spell on itself.
	 * 
	 * @param caster	the spellcaster
	 * @return	the result of the cast
	 */
	public static int cast(Creature caster, RSpell spell) {
		if(caster.hasCondition(Condition.SILENCED)) {
			return SILENCED;
		} else if(spell == null) {
			return NONE;
		} else if(spell.range > 0) {
			return RANGE;
		} else  {
			if(spell instanceof RSpell.Power) {
				int time = Engine.getTimer().getTime();
				if(caster.getMagicComponent().canUse((RSpell.Power)spell, time)) {
					caster.getMagicComponent().usePower((RSpell.Power)spell, time);
					castSpell(caster, caster, spell);
					return OK;
				} else {	// te kort geleden power gecast
					return INTERVAL;
				}
			} else {
				int penalty = checkPenalty(caster);
				if(caster.getSkill(spell.effect.getSchool()) < MagicUtils.getLevel(spell)) {
					return LEVEL;	// spell level te hoog
				} else if(!spell.effect.equals(Effect.SCRIPTED) && 
						MagicUtils.check(caster, spell) < 20 + penalty) {
					return SKILL;	// skill check gefaald
				} else 	if(caster.getMagicComponent().getMana() < MagicUtils.getMana(spell)) {
					return MANA;	// genoeg mana om te casten?
				} else {
					caster.getMagicComponent().addMana(-MagicUtils.getMana(spell));
					castSpell(caster, caster, spell);
					return OK;
				}
			}
		}
	}
	
	private static int castSpell(Item target, RSpell formula) {
		if(formula.effect.getHandler().onItem()) {
			Spell spell = new Spell(formula, 0, target, null);
			spell.getHandler().addEffect(spell);
			return OK;			
		} else {
			return TARGET;
		}
	}
	
	private static int castSpell(Creature target, Creature caster, RSpell formula) {
		Characteristics chars = target.getCharacteristicsComponent();
		int penalty = 0;
		
		if(chars.hasAbility(Ability.SPELL_ABSORPTION)) {
			penalty += chars.getAbility(Ability.SPELL_ABSORPTION);
			target.getMagicComponent().addMana(MagicUtils.getMana(formula)*penalty/100);
		}
		if(chars.hasAbility(Ability.SPELL_RESISTANCE)) {
			penalty += chars.getAbility(Ability.SPELL_RESISTANCE);
		}
		if(chars.hasAbility(Ability.FIRE_RESISTANCE) && formula.effect == Effect.FIRE_DAMAGE) {
			penalty += chars.getAbility(Ability.FIRE_RESISTANCE);
		}
		if(chars.hasAbility(Ability.COLD_RESISTANCE) && formula.effect == Effect.FROST_DAMAGE) {
			penalty += chars.getAbility(Ability.COLD_RESISTANCE);
		}
		if(chars.hasAbility(Ability.SHOCK_RESISTANCE) && formula.effect == Effect.SHOCK_DAMAGE) {
			penalty += chars.getAbility(Ability.SHOCK_RESISTANCE);
		}
		float mod = 1 - penalty/100;
		
		Spell spell = new Spell(formula, mod, target, caster);
		spell.getHandler().addEffect(spell);
		
		if(formula.duration > 0) {
			target.addActiveSpell(spell);
			int time = Engine.getTimer().getTime();
			MagicTask task = new MagicTask(spell, time + formula.duration);
			Engine.getQueue().add(task, time, 1, time + formula.duration);
		}
		
		return OK;
	}
	
	/*
	 * Calculates all penalties related to spellcasting
	 */
	private static int checkPenalty(Creature caster) {
		int penalty = 0;
		
		// wearing armor
		if(CombatUtils.getDV(caster) > caster.species.dv) {
			penalty += 10;
		}
		
		return penalty;
	}
	
	/**
	 * Lets a creature eat food.
	 * 
	 * @param eater
	 * @param food
	 * @return
	 */
	public static int eat(Creature eater, Item.Food food) {
		Enchantment enchantment = food.getMagicComponent();
		int check = Math.max(1, SkillHandler.check(eater, Skill.ALCHEMY)/10);
		RSpell spell = new RSpell("", 0, Dice.roll(1, check, 0), 
				enchantment.getSpell().effect.name(), 1, Dice.roll(1, check, 0), "spell");
		return castSpell(eater, eater, spell);
	}

	/**
	 * Lets a creature drink a potion.
	 * 
	 * @param drinker
	 * @param potion
	 * @return
	 */
	public static int drink(Creature drinker, Item.Potion potion) {
		Enchantment enchantment = potion.getMagicComponent();
		RSpell spell = enchantment.getSpell();
		InventoryHandler.removeItem(drinker, potion.getUID());
		return castSpell(drinker, drinker, spell);
	}
}
