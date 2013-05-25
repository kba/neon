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

package neon.util.trees;

import java.util.Collection;

/**
 * This class implements something like a filesystem tree. 
 * 
 * @author mdriesen
 *
 * @param <E>
 * @param <F>
 */
public class PathTree<E, F> {
	private PathNode<E, F> top;
	
	public PathTree() {
		top = new PathNode<E, F>(0);
	}
	
	/**
	 * @param path
	 * @return	the element at the given path
	 */
	public F get(E... path) {
		return top.get(path);
	}
	
	/**
	 * Checks whether this tree contains an element at the given path.
	 * 
	 * @param path
	 * @return
	 */
	public boolean contains(E... path) {
		return top.contains(path);
	}
	
	/**
	 * Returns a list of all elements at the given path. The path can be 
	 * considered a folder, and all files in this folder are returned. 
	 * Subfolders are ignored.
	 * 
	 * @param path
	 * @return	a list of all elements at the given path
	 */
	public Collection<F> list(E... path) {
		return top.list(path);
	}
	
	/**
	 * Adds an element at the given path.
	 * 
	 * @param value
	 * @param path
	 */
	public void add(F value, E... path) {
		top.add(path, value);
	}
	
	/**
	 * Remove the element at the given path.
	 * 
	 * @param path
	 */
	public void remove(E... path) {
		top.remove(path);
	}
}
