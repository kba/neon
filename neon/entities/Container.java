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

package neon.entities;

import java.util.ArrayList;

import neon.entities.components.Lock;
import neon.entities.components.Trap;
import neon.resources.RItem;

public class Container extends Item {
	public final Lock lock;
	public final Trap trap;
	private ArrayList<Long> items = new ArrayList<Long>();

	public Container(long uid, RItem resource) {
		super(uid, resource);
		lock = new Lock(uid);
		trap = new Trap(uid);
	}
	
	/**
	 * Adds an item to this container.
	 * 
	 * @param uid
	 */
	public void addItem(long uid) {
		items.add(uid);
	}
	
	/**
	 * Removes an item from this container.
	 * 
	 * @param uid
	 */
	public void removeItem(long uid) {
		items.remove(uid);
	}
	
	/**
	 * @return	a list of all items in this container
	 */
	public ArrayList<Long> getItems() {
		return items;
	}
}
