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
import neon.entities.components.Characteristics;
import neon.entities.components.HealthComponent;
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
		Characteristics characteristics = creature.getCharacteristicsComponent();
		switch(skill) {	// bonussen
		case ALTERATION: 
			if(characteristics.hasTrait(Trait.MAGICAL_APTITUDE_ALTERATION)) { check += 2; }
			break;
		case AXE: 
			if(characteristics.hasTrait(Trait.WEAPON_FINESSE_AXE)) { check += 2; }
			break;
		case BLADE: 
			if(characteristics.hasTrait(Trait.WEAPON_FINESSE_BLADE)) { check += 2; }
			break;
		case CLIMBING: 
			if(characteristics.hasTrait(Trait.AGILE)) { check += 2; }
			if(characteristics.hasTrait(Trait.ENDURANCE)) { check += 1; }
			break;
		case CREATION: 
			if(characteristics.hasTrait(Trait.MAGICAL_APTITUDE_CREATION)) { check += 2; }
			break;
		case DESTRUCTION: 
			if(characteristics.hasTrait(Trait.MAGICAL_APTITUDE_DESTRUCTION)) { check += 2; }
			break;
		case ILLUSION: 
			if(characteristics.hasTrait(Trait.MAGICAL_APTITUDE_ILLUSION)) { check += 2; }
			break;
		case LOCKPICKING: 
			if(characteristics.hasTrait(Trait.NIMBLE_FINGERS)) { check += 2; }
			break;
		case MERCANTILE: 
			if(characteristics.hasTrait(Trait.PERSUASIVE)) { check += 2; }
			break;
		case SWIMMING: 
			if(characteristics.hasTrait(Trait.ATHLETIC)) { check += 2; }
			if(characteristics.hasTrait(Trait.ENDURANCE)) { check += 1; }
			break;
		case RESTORATION: 
			if(characteristics.hasTrait(Trait.MAGICAL_APTITUDE_RESTORATION)) { check += 2; }
			break;
		case DODGING: 
			if(characteristics.hasTrait(Trait.ACROBATIC)) { check += 2; }
			if(characteristics.hasTrait(Trait.LIGHTNING_REFLEXES)) { check += 1; }
			break;
		case BLOCK: 
			if(characteristics.hasTrait(Trait.LIGHTNING_REFLEXES)) { check += 1; }
			break;
		case SNEAK: 
			if(characteristics.hasTrait(Trait.STEALTHY)) { check += 2; }
			break;
		case LIGHT_ARMOR: 
			if(characteristics.hasTrait(Trait.ARMOR_PROFICIENCY_LIGHT)) { check += 2; }
			break;
		case MEDIUM_ARMOR: 
			if(characteristics.hasTrait(Trait.ARMOR_PROFICIENCY_MEDIUM)) { check += 2; }
			break;
		case HEAVY_ARMOR: 
			if(characteristics.hasTrait(Trait.ARMOR_PROFICIENCY_HEAVY)) { check += 2; }
			break;
		case DISGUISE: 
			if(characteristics.hasTrait(Trait.DECEITFUL)) { check += 2; }
			break;
		case SPEAR: 
			if(characteristics.hasTrait(Trait.WEAPON_FINESSE_SPEAR)) { check += 2; }
			break;
		case BLUNT: 
			if(characteristics.hasTrait(Trait.WEAPON_FINESSE_BLUNT)) { check += 2; }
			break;
		case ARCHERY: 
			if(characteristics.hasTrait(Trait.WEAPON_FINESSE_ARCHERY)) { check += 2; }
			break;
		case UNARMED: 
			if(characteristics.hasTrait(Trait.WEAPON_FINESSE_UNARMED)) { check += 2; }
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
		case STRENGTH: return creature.getStatsComponent().getStr();
		case CONSTITUTION: return creature.getStatsComponent().getCon();
		case DEXTERITY: return creature.getStatsComponent().getDex();
		case INTELLIGENCE: return creature.getStatsComponent().getInt();
		case WISDOM: return creature.getStatsComponent().getWis();
		case CHARISMA: return creature.getStatsComponent().getCha();
		default: return 0;
		}		
	}

	private static void used(Skill skill, Player player) {
		int value = player.getSkill(skill);
//		System.out.println("skill check: " + skill + ", " + player.getSkill(skill));
		// snelheid van skills leren hangt af van INT
		player.trainSkill(skill, skill.increase*(float)player.getStatsComponent().getInt()/10);

		if(value < player.getSkill(skill)) {	// skill is met 1 gestegen
			Engine.post(new SkillEvent(skill));
			// kijken of er een feat unlocked wordt
			checkFeat(skill, player);

			int level = player.getLevel();
			int stat = getStatValue(skill, player);
			// kijken welke stat er stijgt
			switch(skill.stat) {
			case STRENGTH: player.addBaseStr(0.1f); break;
			case CONSTITUTION: 
				player.addBaseCon(0.1f);
				player.addBaseSpd(0.01f);
				break;
			case DEXTERITY: 
				player.addBaseDex(0.1f); 
				player.addBaseSpd(0.01f);
				break;
			case INTELLIGENCE: player.addBaseInt(0.1f); break;
			case WISDOM: player.addBaseWis(0.1f); break;
			case CHARISMA: player.addBaseCha(0.1f); break;
			default: break;
			}
			if(stat < getStatValue(skill, player)) {	// stat is met 1 gestegen
				Engine.post(new SkillEvent(skill, skill.stat));
			}
			if(level < player.getLevel()) {	// level is met 1 gestegen
				HealthComponent health = player.getHealthComponent();
				health.addBaseHealth(Dice.roll(player.species.hit));
				Engine.post(new SkillEvent(skill, true));
			}
		}
	}
	
	public static void checkFeat(Skill skill, Player player) {
		Characteristics characteristics = player.getCharacteristicsComponent();
		switch(skill) {
		case ALCHEMY:
			if(player.getSkill(skill) >= 20 && !characteristics.hasFeat(Feat.BREW_POTION)) {
				characteristics.addFeat(Feat.BREW_POTION);
			} 
			break;
		case ARCHERY:
			if(player.getSkill(skill) >= 40 && player.getSkill(Skill.RIDING) >= 40 && !characteristics.hasFeat(Feat.MOUNTED_ARCHERY)) {
				characteristics.addFeat(Feat.MOUNTED_ARCHERY);
			}
			break;
		case ARMORER:
			if(player.getSkill(skill) >= 20 && !characteristics.hasFeat(Feat.FORGE_RING)) {
				characteristics.addFeat(Feat.FORGE_RING);
			}
			if(player.getSkill(skill) >= 40 && player.getSkill(Skill.ENCHANT) >= 40 && !characteristics.hasFeat(Feat.CRAFT_MAGIC_ARMS_AND_ARMOR)) {
				characteristics.addFeat(Feat.CRAFT_MAGIC_ARMS_AND_ARMOR);
			}
			break;
		case AXE:
		case BLUNT:
		case BLADE:
			if(player.getSkill(skill) >= 40 && !characteristics.hasFeat(Feat.TWO_WEAPON_FIGHTING)) {
				characteristics.addFeat(Feat.TWO_WEAPON_FIGHTING);
			}
			break;
		case DODGING: 
			if(player.getSkill(skill) >= 60 && !characteristics.hasFeat(Feat.SNATCH_ARROWS)) {
				characteristics.addFeat(Feat.SNATCH_ARROWS);
			}
			break;
		case ENCHANT:
			if(player.getSkill(skill) >= 40 && !characteristics.hasFeat(Feat.SCRIBE_SCROLL)) {
				characteristics.addFeat(Feat.SCRIBE_SCROLL);
			}
			if(player.getSkill(skill) >= 60 && !characteristics.hasFeat(Feat.SCRIBE_TATTOO)) {
				characteristics.addFeat(Feat.SCRIBE_TATTOO);
			}
			if(player.getSkill(skill) >= 20 && player.getSkill(Skill.ARMORER) >= 20 && !characteristics.hasFeat(Feat.CRAFT_MAGIC_ARMS_AND_ARMOR)) {
				characteristics.addFeat(Feat.CRAFT_MAGIC_ARMS_AND_ARMOR);
			}
			break;
		case RIDING:
			if(player.getSkill(skill) >= 20 && !characteristics.hasFeat(Feat.MOUNTED_COMBAT)) {
				characteristics.addFeat(Feat.MOUNTED_COMBAT);
			}	
			if(player.getSkill(skill) >= 40 && player.getSkill(Skill.ARCHERY) >= 40 && !characteristics.hasFeat(Feat.MOUNTED_ARCHERY)) {
				characteristics.addFeat(Feat.MOUNTED_ARCHERY);
			}
			break;
		default:
			break;				
		}		
	}
}
