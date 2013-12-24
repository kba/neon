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

import neon.entities.Creature;

public class DrainStatHandler implements EffectHandler {
	private String stat;
	
	public DrainStatHandler(String stat) {
		this.stat = stat;
	}
	
	public boolean isWeaponEnchantment() {
		return true;
	}

	public boolean isClothingEnchantment() {
		return false;
	}

	public boolean onItem() {
		return false;
	}

	public void addEffect(Spell spell) {
		Creature target = (Creature)spell.getTarget();
		switch(stat) {
		case "str": target.getStatsComponent().addStr(-(int)spell.getMagnitude()); break;
		case "dex": target.getStatsComponent().addDex(-(int)spell.getMagnitude()); break;
		case "con": target.getStatsComponent().addCon(-(int)spell.getMagnitude()); break;
		case "cha": target.getStatsComponent().addCha(-(int)spell.getMagnitude()); break;
		case "wis": target.getStatsComponent().addInt(-(int)spell.getMagnitude()); break;
		case "int": target.getStatsComponent().addWis(-(int)spell.getMagnitude()); break;
		}
	}

	public void removeEffect(Spell spell) {
		Creature target = (Creature)spell.getTarget();
		switch(stat) {
		case "str": target.getStatsComponent().addStr((int)spell.getMagnitude()); break;
		case "dex": target.getStatsComponent().addDex((int)spell.getMagnitude()); break;
		case "con": target.getStatsComponent().addCon((int)spell.getMagnitude()); break;
		case "cha": target.getStatsComponent().addCha((int)spell.getMagnitude()); break;
		case "wis": target.getStatsComponent().addInt((int)spell.getMagnitude()); break;
		case "int": target.getStatsComponent().addWis((int)spell.getMagnitude()); break;
		}
	}

	public void repeatEffect(Spell spell) {
		// geen repeat nodig
	}
}
