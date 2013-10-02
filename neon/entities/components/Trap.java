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

package neon.entities.components;

/**
 * A trap on doors or containers.
 * 
 * @author mdriesen
 */
public class Trap implements Component {
	public static final int ARMED = 0;
	public static final int DISARMED = 1;
	
	private int DC;
	private int state;
	private long uid;
	
	public Trap(long uid) {
		this.uid = uid;
	}
	
	/**
	 * @return	the level of the lock
	 */
	public int getTrapDC() {
		return DC;
	}
	
	/**
	 * Sets the difficulty of this trap
	 * 
	 * @param trapDC
	 */
	public void setTrapDC(int trapDC) {
		DC = trapDC;
	}
	
	/**
	 * Returns whether this is an open, locked or closed lock.
	 * 
	 * @return 	the state of this lock
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Sets the state of this trap.
	 * 
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * Arm this trap.
	 */
	public void arm() {
		state = ARMED;
	}
	
	/**
	 * Disarm this trap.
	 */
	public void disarm() {
		state = DISARMED;
	}

	@Override
	public long getUID() {
		return uid;
	}
}
