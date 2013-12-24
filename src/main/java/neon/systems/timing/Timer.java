/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012-2013 - Maarten Driesen
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

package neon.systems.timing;

/**
 * This class keeps track of the time. 
 * 
 * @author mdriesen
 */
public class Timer {
	private int ticks;
	
	/**
	 * Initializes a new timer on 0.
	 */
	public Timer() {
		ticks = 0;
	}
	
	/**
	 * Increases the timer with one tick.
	 */
	public int addTick() {
		return ++ticks;
	}
	
	/**
	 * Adds ticks to the timer.
	 * 
	 * @param amount	the amount of ticks to add.
	 */
	public int addTicks(int amount) {
		for(int i = 0; i < amount; i++) {
			addTick();
		}
		return ticks;
	}
	
	/**
	 * @return	the total amount of time that has passed
	 */
	public int getTime() {
		return ticks;
	}
	
	/**
	 * Sets the time
	 * 
	 * @param time
	 */
	public void setTime(int time) {
		ticks = time;
	}	
}
