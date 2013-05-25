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

package neon.util.spatial;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import org.apache.jdbm.*;

/**
 * This class represents a primitive R-tree for spatial indexing.
 * 
 * @author mdriesen
 */
public class RTree<E> implements Iterable<E>, SpatialIndex<E> {
	private NavigableMap<Integer, E> objects;
	private Map<Integer, Rectangle2D> boxes;
	private RNode<E> root;
	private int max;
	private int min;
	
	/**
	 * Initializes a new R-tree with the given maximum node size and minimum fill factor.
	 * 
	 * @param nodeSize		the requested node size
	 * @param fillFactor	the requested fill factor
	 */
	public RTree(int nodeSize, int fillFactor) throws IllegalArgumentException {
		if(fillFactor > nodeSize/2) { 
			throw new IllegalArgumentException("Fill factor too high."); 
		} 

		objects = new ConcurrentSkipListMap<Integer, E>();
		boxes = new HashMap<Integer, Rectangle2D>();
		min = fillFactor;
		max = nodeSize;
		root = new RNode<E>(min, max, this);
	}

	/**
	 * Initializes a new cached R-tree with the given maximum node size and 
	 * minimum fill factor. If the cache already contains items, they are 
	 * inserted in the R-tree.
	 * 
	 * @param nodeSize		the requested node size
	 * @param fillFactor	the requested fill factor
	 */
	public RTree(int nodeSize, int fillFactor, DB db, String name) throws IllegalArgumentException {
		if(fillFactor > nodeSize/2) { 
			throw new IllegalArgumentException("Fill factor too high."); 
		} 
		
		min = fillFactor;
		max = nodeSize;
		root = new RNode<E>(min, max, this);
		if(db.getTreeMap(name) != null) {
			objects = db.getTreeMap(name);
			boxes = db.getTreeMap(name + ":boxes");
			for(int i : boxes.keySet()) {
				root.add(i, boxes.get(i).getBounds());
			}
		} else {
			objects = db.createTreeMap(name);
			boxes = db.createTreeMap(name + ":boxes");
		}
	}

	/**
	 * @return	the maximum node size
	 */
	public int getNodeSize() {
		return max;
	}
	
	/**
	 * @return	the minimum fill factor
	 */
	public int getFillFactor() {
		return min;
	}
	
	/**
	 * 
	 * @param p	a point
	 * @return	all objects with a bounding rectangle that contains the given point
	 */
	public ArrayList<E> getElements(Point p) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		if(root.getBox().contains(p)) {
			indices.addAll(root.getIndices(p));
		}

		ArrayList<E> nodes = new ArrayList<E>();
		for(int i : indices) {
			nodes.add(objects.get(i));
		}
		return nodes;
	}

	/**
	 * @return	the total amount of objects in this tree
	 */
	public int size() {
		return objects.size();
	}

	/**
	 * @param r	a rectangle
	 * @return	all objects with a bounding rectangle intersecting the given rectangle
	 */
	public ArrayList<E> getElements(Rectangle r) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		if(root.getBox().intersects(r)) {
			indices.addAll(root.getIndices(r));
		}

		ArrayList<E> nodes = new ArrayList<E>();
		for(int i : indices) {
			nodes.add(objects.get(i));
		}
		return nodes;
	}

	/**
	 * @return	the width of the bounding rectangle of the tree
	 */
	public int getWidth() {
		return root.getBox().width;
	}
	
	/**
	 * @return	the height of the bounding rectangle of the tree
	 */
	public int getHeight() {
		return root.getBox().height;
	}

	/**
	 * @return	all elements in this tree
	 */
	public Collection<E> getElements() {
		return objects.values();
	}

	/**
	 * Adds an object to the tree.
	 * 
	 * @param box		the bounding rectangle of the object
	 * @param object	the object to add
	 */
	public void insert(E object, Rectangle box) {
		Rectangle2D box2D = new Rectangle2D.Double(box.x, box.y, box.width, box.height);
		int index = (objects.size() > 0) ? objects.lastKey() + 1 : 0;
		boxes.put(index, box2D);
		root.add(index, box);
		objects.put(index, object);
	}

	/**
	 * Moves an object in the tree.
	 * 
	 * @param box		the new bounding rectangle of the object
	 * @param object	the object to move
	 */
	public void move(Rectangle box, E object) {
		if(objects.containsValue(object)) {
			remove(object);
			insert(object, box);
		} else {
			throw new IllegalArgumentException("Tree does not contain requested object.");
		}
	}

	/**
	 * Removes an object from the tree.
	 * 
	 * @param object	the object to remove
	 */
	public void remove(E object) {
		if(objects.containsValue(object)) {
			int index = 0;
			for(int i : objects.keySet()) {
				if(objects.get(i) == object) {
					index = i;
				}
			}
			objects.remove(index);
			root.remove(boxes.get(index).getBounds(), index);
			boxes.remove(index);
		} 
	}

	public Iterator<E> iterator() {
		return objects.values().iterator();
	}

	public void clear() {
		objects.clear();
		boxes.clear();
		root = null;
	}
	
	Rectangle getBox(int index) {
		return boxes.get(index).getBounds();
	}
}
