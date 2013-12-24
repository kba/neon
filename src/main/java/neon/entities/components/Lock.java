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

import neon.resources.RItem;

/**
 * This class represents a lock on an item.
 * 
 * @author mdriesen
 */
public class Lock implements Component {
	public static final int OPEN = 0;
	public static final int CLOSED = 1;
	public static final int LOCKED = 2;
	
	private int lock = 0;
	private int state = OPEN;
	private RItem key;
	private long uid;
	
	public Lock(long uid) {
		this.uid = uid;
	}
	
	/**
	 * Sets the difficulty of this lock.
	 * 
	 * @param lock	the new difficulty of this lock
	 */
	public void setLockDC(int lock) {
		this.lock = lock;
	}
	
	/**
	 * @return	the level of the lock
	 */
	public int getLockDC() {
		return lock;
	}
	
	/**
	 * Unlocks this lock.
	 */
	public void unlock() {
		state = CLOSED;
	}
	
	/**
	 * Locks this lock.
	 */
	public void lock() {
		state = LOCKED;
	}
	
	/**
	 * @return	the status of this lock (locked or not)
	 */
	public boolean isLocked() {
		return state == LOCKED;
	}
	
	
	/**
	 * Checks whether this lock is closed.
	 * 
	 * @return 	<code>true</code> when this lock is closed, <code>false</code> otherwise
	 */
	public boolean isClosed() {
		return state == CLOSED;
	}
	
	/**
	 * Checks whether this lock is open.
	 * 
	 * @return 	<code>true</code> when this lock is closed, <code>false</code> otherwise
	 */
	public boolean isOpen() {
		return state == OPEN;
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
	 * Closes this lock.
	 */
	public void close() {
		if(state != LOCKED) {
			state = CLOSED;
		}
	}
	
	/**
	 * Opens this lock.
	 */
	public void open() {
		state = OPEN;
	}
	
	/**
	 * Returns the key used to open this lock.
	 * 
	 * @return 	the door key
	 */
	public RItem getKey() {
		return key;
	}
	
	/**
	 * Sets the key used to open this lock.
	 * 
	 * @param key	the key
	 */
	public void setKey(RItem key) {
		this.key = key;
	}
	
	/**
	 * @return whether this lock actually has a non-null key
	 */
	public boolean hasKey() {
		return key != null;
	}
	
	/**
	 * Sets the state of this lock.
	 * 
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}

	@Override
	public long getUID() {
		return uid;
	}
}
