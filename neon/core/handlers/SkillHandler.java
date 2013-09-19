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

import neon.core.Engine;
import neon.core.event.SkillEvent;
import neon.entities.Creature;
import neon.entities.Player;
import neon.entities.property.Feat;
import neon.entities.property.Skill;
import neon.entities.property.Trait;
import neon.util.Dice;

/*
 * Huidig levelling mechanisme:
 * 	- 10 skill increases => stat increase
 * 	- 10 increases in dex of con => spd increase
 */
public class SkillHandler {
	public static int check(Creature creature, Skill skill) {
		int check = getStatValue(skill, creature) + Dice.roll(1, creature.getSkill(skill), 0);
		switch(skill) {	// bonussen
		case ALTERATION: 
			if(creature.hasTrait(Trait.MAGICAL_APTITUDE_ALTERATION)) { check += 2; }
			break;
		case AXE: 
			if(creature.hasTrait(Trait.WEAPON_FINESSE_AXE)) { check += 2; }
			break;
		case BLADE: 
			if(creature.hasTrait(Trait.WEAPON_FINESSE_BLADE)) { check += 2; }
			break;
		case CLIMBING: 
			if(creature.hasTrait(Trait.AGILE)) { check += 2; }
			if(creature.hasTrait(Trait.ENDURANCE)) { check += 1; }
			break;
		case CREATION: 
			if(creature.hasTrait(Trait.MAGICAL_APTITUDE_CREATION)) { check += 2; }
			break;
		case DESTRUCTION: 
			if(creature.hasTrait(Trait.MAGICAL_APTITUDE_DESTRUCTION)) { check += 2; }
			break;
		case ILLUSION: 
			if(creature.hasTrait(Trait.MAGICAL_APTITUDE_ILLUSION)) { check += 2; }
			break;
		case LOCKPICKING: 
			if(creature.hasTrait(Trait.NIMBLE_FINGERS)) { check += 2; }
			break;
		case MERCANTILE: 
			if(creature.hasTrait(Trait.PERSUASIVE)) { check += 2; }
			break;
		case SWIMMING: 
			if(creature.hasTrait(Trait.ATHLETIC)) { check += 2; }
			if(creature.hasTrait(Trait.ENDURANCE)) { check += 1; }
			break;
		case RESTORATION: 
			if(creature.hasTrait(Trait.MAGICAL_APTITUDE_RESTORATION)) { check += 2; }
			break;
		case DODGING: 
			if(creature.hasTrait(Trait.ACROBATIC)) { check += 2; }
			if(creature.hasTrait(Trait.LIGHTNING_REFLEXES)) { check += 1; }
			break;
		case BLOCK: 
			if(creature.hasTrait(Trait.LIGHTNING_REFLEXES)) { check += 1; }
			break;
		case SNEAK: 
			if(creature.hasTrait(Trait.STEALTHY)) { check += 2; }
			break;
		case LIGHT_ARMOR: 
			if(creature.hasTrait(Trait.ARMOR_PROFICIENCY_LIGHT)) { check += 2; }
			break;
		case MEDIUM_ARMOR: 
			if(creature.hasTrait(Trait.ARMOR_PROFICIENCY_MEDIUM)) { check += 2; }
			break;
		case HEAVY_ARMOR: 
			if(creature.hasTrait(Trait.ARMOR_PROFICIENCY_HEAVY)) { check += 2; }
			break;
		case DISGUISE: 
			if(creature.hasTrait(Trait.DECEITFUL)) { check += 2; }
			break;
		case SPEAR: 
			if(creature.hasTrait(Trait.WEAPON_FINESSE_SPEAR)) { check += 2; }
			break;
		case BLUNT: 
			if(creature.hasTrait(Trait.WEAPON_FINESSE_BLUNT)) { check += 2; }
			break;
		case ARCHERY: 
			if(creature.hasTrait(Trait.WEAPON_FINESSE_ARCHERY)) { check += 2; }
			break;
		case UNARMED: 
			if(creature.hasTrait(Trait.WEAPON_FINESSE_UNARMED)) { check += 2; }
			break;
		default:
			break;
		}
		if(creature instanceof Player) { 
			used(skill, (Player)creature); 
		}
		return check;	
	}

	private static int getStatValue(Skill skill, Creature creature) {
		switch(skill.stat) {
		case Skill.STR: return creature.getStr();
		case Skill.CON: return creature.getCon();
		case Skill.DEX: return creature.getDex();
		case Skill.INT: return creature.getInt();
		case Skill.WIS: return creature.getWis();
		case Skill.CHA: return creature.getCha();
		default: return 0;
		}		
	}

	private static void used(Skill skill, Player player) {
		int value = player.getSkill(skill);
//		System.out.println("skill check: " + skill + ", " + player.getSkill(skill));
		// snelheid van skills leren hangt af van INT
		player.trainSkill(skill, skill.increase*(float)player.getInt()/10);

		if(value < player.getSkill(skill)) {	// skill is met 1 gestegen
			Engine.post(new SkillEvent(skill));
			// kijken of er een feat unlocked wordt
			checkFeat(skill, player);

			int level = player.getLevel();
			int stat = getStatValue(skill, player);
			// kijken welke stat er stijgt
			switch(skill.stat) {
			case Skill.STR: player.addBaseStr(0.1f); break;
			case Skill.CON: 
				player.addBaseCon(0.1f);
				player.addBaseSpd(0.01f);
				break;
			case Skill.DEX: 
				player.addBaseDex(0.1f); 
				player.addBaseSpd(0.01f);
				break;
			case Skill.INT: player.addBaseInt(0.1f); break;
			case Skill.WIS: player.addBaseWis(0.1f); break;
			case Skill.CHA: player.addBaseCha(0.1f); break;
			}
			if(stat < getStatValue(skill, player)) {	// stat is met 1 gestegen
				Engine.post(new SkillEvent(skill, skill.stat));
			}
			if(level < player.getLevel()) {	// level is met 1 gestegen
				player.addBaseHealth(Dice.roll(player.species.hit));
				Engine.post(new SkillEvent(skill, true));
			}
		}
	}
	
	public static void checkFeat(Skill skill, Player player) {
		switch(skill) {
		case ALCHEMY:
			if(player.getSkill(skill) >= 20 && !player.hasFeat(Feat.BREW_POTION)) {
				player.addFeat(Feat.BREW_POTION);
			} 
			break;
		case ARCHERY:
			if(player.getSkill(skill) >= 40 && player.getSkill(Skill.RIDING) >= 40 && !player.hasFeat(Feat.MOUNTED_ARCHERY)) {
				player.addFeat(Feat.MOUNTED_ARCHERY);
			}
			break;
		case ARMORER:
			if(player.getSkill(skill) >= 20 && !player.hasFeat(Feat.FORGE_RING)) {
				player.addFeat(Feat.FORGE_RING);
			}
			if(player.getSkill(skill) >= 40 && player.getSkill(Skill.ENCHANT) >= 40 && !player.hasFeat(Feat.CRAFT_MAGIC_ARMS_AND_ARMOR)) {
				player.addFeat(Feat.CRAFT_MAGIC_ARMS_AND_ARMOR);
			}
			break;
		case AXE:
		case BLUNT:
		case BLADE:
			if(player.getSkill(skill) >= 40 && !player.hasFeat(Feat.TWO_WEAPON_FIGHTING)) {
				player.addFeat(Feat.TWO_WEAPON_FIGHTING);
			}
			break;
		case DODGING: 
			if(player.getSkill(skill) >= 60 && !player.hasFeat(Feat.SNATCH_ARROWS)) {
				player.addFeat(Feat.SNATCH_ARROWS);
			}
			break;
		case ENCHANT:
			if(player.getSkill(skill) >= 40 && !player.hasFeat(Feat.SCRIBE_SCROLL)) {
				player.addFeat(Feat.SCRIBE_SCROLL);
			}
			if(player.getSkill(skill) >= 60 && !player.hasFeat(Feat.SCRIBE_TATTOO)) {
				player.addFeat(Feat.SCRIBE_TATTOO);
			}
			if(player.getSkill(skill) >= 20 && player.getSkill(Skill.ARMORER) >= 20 && !player.hasFeat(Feat.CRAFT_MAGIC_ARMS_AND_ARMOR)) {
				player.addFeat(Feat.CRAFT_MAGIC_ARMS_AND_ARMOR);
			}
			break;
		case RIDING:
			if(player.getSkill(skill) >= 20 && !player.hasFeat(Feat.MOUNTED_COMBAT)) {
				player.addFeat(Feat.MOUNTED_COMBAT);
			}	
			if(player.getSkill(skill) >= 40 && player.getSkill(Skill.ARCHERY) >= 40 && !player.hasFeat(Feat.MOUNTED_ARCHERY)) {
				player.addFeat(Feat.MOUNTED_ARCHERY);
			}
			break;
		default:
			break;				
		}		
	}
}
