/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2011 - Maarten Driesen
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

package neon.util.spatial;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 * This is a Quadtree version of a {@code SpatialIndex}.
 * 
 * @author mdriesen
 *
 * @param <E>
 */
public class QuadTree<E> implements SpatialIndex<E> {
	private Node root;
	private Rectangle bounds;
	private int capacity;
	
	public QuadTree(int capacity, Rectangle bounds) {
		this.capacity = capacity;
		this.bounds = bounds;
		root = new Node(bounds);
	}
	
	public List<E> getElements() {
		return null;
	}

	public List<E> getElements(Rectangle bounds) {
		return null;
	}

	public void insert(E e, Rectangle bounds) {
		root.insert(e, bounds);
	}

	public void remove(E e) {
		
	}

	public void move(E e, Point p) {
		
	}
	
	public void resize(E e, Dimension d) {
		
	}
	
	public void clear() {
		
	}

	public int getWidth() {
		return bounds.width;
	}

	public int getHeight() {
		return bounds.height;
	}
	
	private class Node {
		private Node NW;
		private Node NE;
		private Node SW;
		private Node SE;
		
		private Node(Rectangle bounds) {
			
		}
		
		private void insert(E e, Rectangle bounds) {
			
		}
	}
}
