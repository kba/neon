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

package neon.objects.property;

public enum Skill {
	// magic
	CREATION(4, 0.1f), DESTRUCTION(5, 0.1f), RESTORATION(5, 0.1f), ALTERATION(4, 0.1f), ILLUSION(6, 0.1f), ENCHANT(4, 0.1f), ALCHEMY(4, 0.1f),
	CONJURATION(5, 0.1f),
	
	// wapens
	ARCHERY(3, 0.1f), AXE(1, 0.1f), BLUNT(1, 0.1f), BLADE(1, 0.1f), SPEAR(1, 0.1f), UNARMED(2, 0.1f), 
	
	// bewegen
	CLIMBING(2, 0.1f), SWIMMING(2, 0.1f), SNEAK(3, 0.1f),
	
	// combat
	HEAVY_ARMOR(1, 0.1f), MEDIUM_ARMOR(2, 0.1f), LIGHT_ARMOR(3, 0.1f), DODGING(3, 0.1f), BLOCK(1, 0.1f), UNARMORED(2, 0.1f), 
	
	// allerlei
	MERCANTILE(6, 0.1f), PICKPOCKET(3, 0.1f), ARMORER(2, 0.1f), LOCKPICKING(3, 0.1f), MEDICAL(5, 0.1f), DISABLE(4, 0.1f), SPEECHCRAFT(6, 0.1f), 
	PERFORM(6, 0.1f), DISGUISE(6, 0.1f), RIDING(5, 0.1f);
	
	public final static int STR = 1;
	public final static int CON = 2;
	public final static int DEX = 3;
	public final static int INT = 4;
	public final static int WIS = 5;
	public final static int CHA = 6;
	public final static int NONE = 0;
	
	public final int stat;
	public final float increase;

	private Skill(float increase) {
		this(NONE, increase);
	}

	private Skill(int stat, float increase) {
		this.increase = increase;
		this.stat = stat;
	}

	public String getStat() {
		switch(stat) {
		case 1: return "strength";
		case 2: return "constitution";
		case 3: return "dexterity";
		case 4: return "intelligence";
		case 5: return "wisdom";
		case 6: return "charisma";
		default: return "none";
		}
	}
}
