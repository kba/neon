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

package neon.systems.timing;

import java.util.*;

/**
 * This class keeps track of the time. It contains a list of listeners that are notified when
 * the timer increases.
 * 
 * @author mdriesen
 */
public class Timer {
	private ArrayList<TimerListener> listeners;
	private int ticks;
	
	/**
	 * Initializes a new timer on 0.
	 */
	public Timer() {
		ticks = 0;
		listeners = new ArrayList<TimerListener>();
	}
	
	/**
	 * Warns all current listeners that the timer has started.
	 */
	public void start() {
		for(TimerListener listener : new ArrayList<TimerListener>(listeners)) {
			listener.start(ticks);
		}		
	}
	
	/**
	 * Increases the timer with one tick and notifies the listeners.
	 */
	public void addTick() {
		ticks++;
		// hier lokale kopie maken om concurrentModificationExceptions te voorkomen
		for(TimerListener listener : new ArrayList<TimerListener>(listeners)) {
			listener.tick(ticks);
		}
	}
	
	/**
	 * Adds ticks to the timer. Listeners are not notified.
	 * 
	 * @param amount	the amount of ticks to add.
	 */
	public void addTicks(int amount) {
		for(int i = 0; i < amount; i++) {
			addTick();
		}
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
	
	/**
	 * Adds a <code>TimerListener</code> to this timer.
	 * 
	 * @param listener	the <code>TimerListener</code> to add
	 */
	public void addListener(TimerListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes the given <code>TimerListener</code> from this timer.
	 * 
	 * @param listener	the <code>TimerListener</code> to remove
	 */
	public void removeListener(TimerListener listener) {
		listeners.remove(listener);
	}
}
