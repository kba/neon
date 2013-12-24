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

package neon.entities.property;

public enum Trait {
	MAGICAL_APTITUDE_ALTERATION("magical aptitude (alteration)"), MAGICAL_APTITUDE_CREATION("magical aptitude (creation)"), 
	MAGICAL_APTITUDE_DESTRUCTION("magical aptitude (destruction)"), MAGICAL_APTITUDE_ILLUSION("magical aptitude (illusion)"), 
	MAGICAL_APTITUDE_RESTORATION("magical aptitude (restoration)"), NIMBLE_FINGERS("nimble fingers"), ATHLETIC("athletic"), ACROBATIC("acrobatic"),
	AGILE("agile"), STEALTHY("stealthy"), COMBAT_CASTING("combat casting"), SPELL_FOCUS("spell focus"),
	ARMOR_PROFICIENCY_LIGHT ("light armor proficiency"), ARMOR_PROFICIENCY_MEDIUM ("medium armor proficiency"), 
	ARMOR_PROFICIENCY_HEAVY ("heavy armor proficiency"), ENDURANCE("endurance"), FAR_SHOT("far shot"), LIGHTNING_REFLEXES("lighning reflexes"),
	SHIELD_PROFICIENCY("shield proficiency"), WEAPON_FOCUS_SPEAR("weapon focus (spear)"), WEAPON_FOCUS_BLADE("weapon focus (blade)"), 
	WEAPON_FOCUS_AXE("weapon focus (axe)"), WEAPON_FOCUS_BLUNT("weapon focus (blunt)"), WEAPON_FOCUS_UNARMED("weapon focus (unarmed)"), 
	WEAPON_FOCUS_ARCHERY("weapon focus (archery)"), SPELL_MASTERY_FIRE("spell mastery (fire)"), SPELL_MASTERY_COLD("spell mastery (cold)"), 
	SPELL_MASTERY_LIGHTNING("spell mastery (lightning)"), DECEITFUL("deceitful"), WEAPON_FINESSE_SPEAR("weapon finesse (spear)"), 
	WEAPON_FINESSE_BLADE("weapon finesse (blade)"), WEAPON_FINESSE_AXE("weapon finesse (axe)"), WEAPON_FINESSE_BLUNT("weapon finesse (blunt)"), 
	WEAPON_FINESSE_UNARMED("weapon finesse (unarmed)"), WEAPON_FINESSE_ARCHERY("weapon finesse (archery)"), PRECISE_SHOT("precise shot"),
	IMPROVED_PRECISE_SHOT("improved precise shot"), PERSUASIVE("persuasive");
	
	public final String text;

	private Trait(String text) {
		this.text = text;
	}
}
