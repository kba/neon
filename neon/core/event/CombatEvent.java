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

package neon.core.event;

import java.util.EventObject;
import neon.objects.entities.Creature;

@SuppressWarnings("serial")
public class CombatEvent extends EventObject {
	public static final int ATTACK = 1;
	public static final int DODGE = 2;
	public static final int BLOCK = 3;
	public static final int DIE = 4;

	public static final int MELEE = 0;
	public static final int SHOOT = 1;
	public static final int FLING = 2;
	
	private Creature attacker;
	private Creature defender;
	private int result = 0;
	private int type = 0;
	
	public CombatEvent(Creature attacker, Creature defender, int result) {
		super(attacker);
		this.attacker = attacker;
		this.defender = defender;
		this.result = result;
	}
	
	public CombatEvent(Creature attacker, Creature defender) {
		super(attacker);
		this.attacker = attacker;
		this.defender = defender;
	}
	
	public CombatEvent(int type, Creature attacker, Creature defender) {
		super(attacker);
		this.type = type;
		this.attacker = attacker;
		this.defender = defender;
	}
	
	public boolean isFinished() {
		return result > 0;
	}
	
	public Creature getAttacker() {
		return attacker;
	}
	
	public Creature getDefender() {
		return defender;
	}
	
	public int getResult() {
		return result;
	}
	
	public int getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return attacker.getID() + defender.getID();
	}
}
