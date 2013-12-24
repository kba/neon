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

package neon.entities.property;

/**
 * The different skills a creature can have. Each skill is governed by an attribute. Each skill increase will 
 * also increase its attribute with a certain amount.
 * 
 * @author mdriesen
 */
public enum Skill {
	// magic
	CREATION(Attribute.INTELLIGENCE, 0.1f), DESTRUCTION(Attribute.WISDOM, 0.1f), RESTORATION(Attribute.WISDOM, 0.1f), 
	ALTERATION(Attribute.INTELLIGENCE, 0.1f), ILLUSION(Attribute.CHARISMA, 0.1f), ENCHANT(Attribute.INTELLIGENCE, 0.1f), 
	ALCHEMY(Attribute.INTELLIGENCE, 0.1f), CONJURATION(Attribute.WISDOM, 0.1f),
	
	// wapens
	ARCHERY(Attribute.DEXTERITY, 0.1f), AXE(Attribute.STRENGTH, 0.1f), BLUNT(Attribute.STRENGTH, 0.1f), 
	BLADE(Attribute.STRENGTH, 0.1f), SPEAR(Attribute.STRENGTH, 0.1f), UNARMED(Attribute.CONSTITUTION, 0.1f), 
	
	// bewegen
	CLIMBING(Attribute.CONSTITUTION, 0.1f), SWIMMING(Attribute.CONSTITUTION, 0.1f), SNEAK(Attribute.DEXTERITY, 0.1f),
	
	// combat
	HEAVY_ARMOR(Attribute.STRENGTH, 0.1f), MEDIUM_ARMOR(Attribute.CONSTITUTION, 0.1f), LIGHT_ARMOR(Attribute.DEXTERITY, 0.1f), 
	DODGING(Attribute.DEXTERITY, 0.1f), BLOCK(Attribute.STRENGTH, 0.1f), UNARMORED(Attribute.CONSTITUTION, 0.1f), 
	
	// allerlei
	MERCANTILE(Attribute.CHARISMA, 0.1f), PICKPOCKET(Attribute.DEXTERITY, 0.1f), ARMORER(Attribute.CONSTITUTION, 0.1f), 
	LOCKPICKING(Attribute.DEXTERITY, 0.1f), MEDICAL(Attribute.WISDOM, 0.1f), DISABLE(Attribute.INTELLIGENCE, 0.1f), 
	SPEECHCRAFT(Attribute.CHARISMA, 0.1f), PERFORM(Attribute.CHARISMA, 0.1f), DISGUISE(Attribute.CHARISMA, 0.1f), 
	RIDING(Attribute.WISDOM, 0.1f),
	
	// en deze
	NONE(Attribute.NONE, 0.0f);
	
	public final Attribute stat;
	public final float increase;

	private Skill(float increase) {
		this(Attribute.NONE, increase);
	}

	private Skill(Attribute stat, float increase) {
		this.increase = increase;
		this.stat = stat;
	}
}
