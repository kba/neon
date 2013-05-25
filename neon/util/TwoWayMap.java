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

package neon.util;

import java.util.HashMap;
import java.util.Set;

/**
 * A two-way variant of a <code>HashMap</code>. Apart from the normal key-value mapping, it is 
 * also possible to request the key that belongs to a certain value. 
 * 
 * All keys and values are unique. If an existing key or value is provided, the currently mapped 
 * key and value will be removed and overwritten. Suppose the pair (key1, value1) exist. When adding 
 * the pair (key1, value2), the (value1, key1) mapping will be deleted first, then overwritten. This means
 * that value1 is not contained in this map anymore.
 * 
 * @author mdriesen
 * @param <V>	the type of the values
 * @param <K>	the type of the keys
 */
public class TwoWayMap<K,V> {
	private HashMap<K,V> keys;
	private HashMap<V,K> values;
	
	public TwoWayMap() {
		keys = new HashMap<K,V>();
		values = new HashMap<V,K>();
	}
	
	/**
	 * @return	the number of (key, value) pairs in this map
	 */
	public int size() {
		return keys.size();
	}
	
	/**
	 * @param value	a value
	 * @return		the key that belongs to the given value
	 */
	public K getKey(V value) {
		return values.get(value);
	}

	/**
	 * @param key	a key
	 * @return		the value that belongs to this key
	 */
	public V getValue(K key) {
		return keys.get(key);
	}

	/**
	 * Adds the given (key, value) pair to this map
	 * 
	 * @param key	a key
	 * @param value	a corresponding value
	 */
	public void put(K key, V value) {
		if(keys.containsKey(key)) {
			values.remove(keys.get(key));
		}
		if(values.containsKey(value)) {
			keys.remove(values.get(value));
		}
		keys.put(key, value);
		values.put(value, key);
	}

	/**
	 * Removes the given key and the value that belongs to it.
	 * 
	 * @param key	a key
	 */
	public void removeKey(K key) {
		values.remove(keys.get(key));
		keys.remove(key);
	}
	
	/**
	 * Removes the given value and the key that belongs to it.
	 * 
	 * @param value	a value
	 */
	public void removeValue(V value) {
		keys.remove(values.get(value));
		values.remove(value);
	}
	
	/**
	 * @param key
	 * @return	whether this map contains the given key.
	 */
	public boolean containsKey(K key) {
		return keys.containsKey(key);
	}

	/**
	 * @param value
	 * @return	whether this map contains the given value.
	 */
	public boolean containsValue(V value) {
		return values.containsKey(value);
	}
	
	/**
	 * @return a set of all keys in this map
	 */
	public Set<K> keySet() {
		return keys.keySet();
	}
	
	/**
	 * @return a set of all values in this map
	 */
	public Set<V> valueSet() {
		return values.keySet();
	}
}
