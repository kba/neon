/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2010 - Maarten Driesen
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

public enum Ability {
	DARKVISION("darkvision"), SPELL_ABSORPTION("spell absorption"), COLD_RESISTANCE("cold resistance"), FIRE_RESISTANCE("fire resistance"),
	SPELL_RESISTANCE("spell resistance"), SHOCK_RESISTANCE("shock resistance"), FAST_HEALING("fast healing"), 
	TURN_RESISTANCE("turn resistance");
	
	public String text;

	private Ability(String text) {
		this.text = text;
	}
}
