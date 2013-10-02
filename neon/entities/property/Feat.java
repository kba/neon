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

public enum Feat {
	BREW_POTION("brew potion"), CRAFT_MAGIC_ARMS_AND_ARMOR("craft magic arms and armor"), TWO_WEAPON_FIGHTING("two-weapon fighting"), 
	SCRIBE_TATTOO("scribe tattoo"), MOUNTED_ARCHERY("mounted archery"), MOUNTED_COMBAT("mounted combat"), SNATCH_ARROWS("snatch arrows"),
	FORGE_RING("forge ring"), SCRIBE_SCROLL("scribe scroll");
	
	public String text;

	private Feat(String text) {
		this.text = text;
	}
}
