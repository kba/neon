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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * A multimap is a kind of hashmap where every key can hold multiple values.
 * 
 * @author mdriesen
 *
 * @param <K>	the type of the values
 * @param <V>	the type of the keys
 */
public class MultiMap<K,V> {
	private HashMap<K, ArrayList<V>> map;
	
	public MultiMap() {
		map = new HashMap<K, ArrayList<V>>();
	}
	
	public MultiMap(MultiMap<K,V> parent) {
		map = new HashMap<K, ArrayList<V>>();
		for(K key : parent.keySet()) {
			map.put(key, new ArrayList<V>(parent.get(key)));
		}
	}
	
	public void put(K key, V value) {
		if(!map.containsKey(key)) {
			map.put(key, new ArrayList<V>());
		}
		map.get(key).add(value);
	}
	
	public void putAll(K key, Collection<V> values) {
		if(!map.containsKey(key)) {
			map.put(key, new ArrayList<V>());
		}
		for(V value : values) {
			map.get(key).add(value);
		}
	}

	public void putAll(MultiMap<K,V> map) {
		for(K k : map.keySet()) {
			putAll(k, map.get(k));
		}
	}

	public void remove(K key) {
		map.remove(key);
	}
	
	public void remove(K key, V value) {
		map.get(key).remove(value);
	}
	
	public Collection<V> get(K key) {
		return map.get(key);
	}
	
	public Set<K> keySet() {
		return map.keySet();
	}
	
	public void clear() {
		map.clear();
	}
	
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
	
	public Collection<V> values() {
		ArrayList<V> values = new ArrayList<V>();
		for(Collection<V> c : map.values()) {
			values.addAll(c);
		}
		return values;
	}
}
