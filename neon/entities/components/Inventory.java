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

import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import neon.entities.property.Slot;

public class Inventory implements Iterable<Long>, Component {
	private CopyOnWriteArrayList<Long> items;
	private EnumMap<Slot, Long> equiped;
	private final long uid;

	public Inventory(long owner) {
		equiped = new EnumMap<Slot, Long>(Slot.class);
		items = new CopyOnWriteArrayList<Long>();
		uid = owner;
	}
	
	/**
	 * @return	this creature's items
	 */
	public Collection<Long> getItems() {
		return items;
	}
	
	/**
	 * @param slot
	 * @return	the item in the given slot
	 */
	public long get(Slot slot) {
		if(equiped.containsKey(slot)) {
			return equiped.get(slot);
		} else {
			return 0;
		}
	}
	
	/**
	 * Puts an item in the given slot. This method does not check any of the 
	 * consequences of equiping an item.
	 * 
	 * @param slot
	 * @param uid
	 */
	public void put(Slot slot, long uid) {
		equiped.put(slot, uid);
	}
	
	/**
	 * Removes the item in the given slot. This method does not check any of 
	 * the consequences of unequiping an item.
	 * 
	 * @param slot
	 */
	public void remove(Slot slot) {
		equiped.remove(slot);
	}
	
	/**
	 * @param uid
	 * @return	whether the creature has equiped the item with the given uid
	 */
	public boolean hasEquiped(long uid) {
		return equiped.containsValue(uid);
	}
	
	/**
	 * @param slot
	 * @return	whether the creature has equiped an item in the given slot
	 */
	public boolean hasEquiped(Slot slot) {
		return equiped.containsKey(slot);
	}
	
	public Set<Slot> slots() {
		return equiped.keySet();
	}
	
	@Override
	public Iterator<Long> iterator() {
		return items.iterator();
	}
	
	/**
	 * Adds an item to this inventory. This method should not be used to add money.
	 * 
	 * @param uid	the uid of the item to add
	 */
	public void addItem(long uid) {
		items.add(uid);
	}
	
	/**
	 * Removes an item from this inventory. This method does not check the 
	 * effects of unequiping said item.
	 * 
	 * @param uid	the uid of the the item to remove
	 */
	public void removeItem(long uid) {
		items.remove(uid);
	}

	@Override
	public long getUID() {
		return uid;
	}
}
