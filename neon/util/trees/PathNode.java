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

package neon.util.trees;

import java.util.*;

public class PathNode<E, F> {
	private HashMap<E, PathNode<E, F>> nodes;	// directories
	private HashMap<E, F> values;				// files
	private int level;
	
	protected PathNode(int level) {
		this.level = level;
		values = new HashMap<E, F>();
		nodes = new HashMap<E, PathNode<E, F>>();
	}
	
	protected F get(E[] path) {
		if(path.length == level + 1) {
			if(values.containsKey(path[level])) {
				return values.get(path[level]);
			} else {
				return null;
			}
		} else {
			if(nodes.containsKey(path[level])) {
				return nodes.get(path[level]).get(path);
			} else {
				return null;
			}
		}
	}
	
	protected void add(E[] path, F value) {
		if(path.length == level + 1) {
			values.put(path[level], value);
		} else {
			if(!nodes.containsKey(path[level])) {
				nodes.put(path[level], new PathNode<E, F>(level + 1));
			}
			nodes.get(path[level]).add(path, value);
		}
	}

	protected Collection<F> list(E... path) {
		if(path.length == level) {
			return list();
		} else {
			if(nodes.containsKey(path[level])) {
				return nodes.get(path[level]).list(path);
			} else {
				return new ArrayList<F>();
			}
		}
	}
	
	protected Collection<F> list() {
		ArrayList<F> list = new ArrayList<F>();
		list.addAll(values.values());
		for(PathNode<E,F> node : nodes.values()) {
			list.addAll(node.list());
		}
		return list;
	}
	
	protected boolean contains(E[] path) {
		if(path.length == level + 1) {
			if(values.containsKey(path[level])) {
				return true;
			} else {
				return false;
			}
		} else {
			if(nodes.containsKey(path[level])) {
				return nodes.get(path[level]).contains(path);
			} else {
				return false;
			}
		}
	}
	
	protected void remove(E[] path) {
		if(path.length == level + 1) {
			nodes.remove(path[level]);
			values.remove(path[level]);
		} else {
			if(nodes.containsKey(path[level])) {
				nodes.get(path[level]).remove(path);
			}
		}
	}
}
