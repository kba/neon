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

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

class RNode<E> {
	private Rectangle box = new Rectangle();	// minimum bounding rectangle
	private RNode<E>[] nodes;
	private int[] entries;
	private Rectangle[] boxes;
	private int max;		// node size van de tree	(max aantal elementen in een node)
	private int min;		// fill factor van de tree	(min aantal elementen in een node)
	private int size;		// totaal aantal objecten in deze node en subnodes
	private RTree<E> tree;
	
	protected RNode(int nodesize, int fillfactor, RTree<E> parent) {
		tree = parent;
		entries = new int[nodesize];
		boxes = new Rectangle[nodesize];
		max = nodesize;
		min = fillfactor;
		size = 0;
	}
	
	protected Rectangle getBox() {
		return (Rectangle)box;
	}
	
	protected RNode<E>[] getNodes() {
		return nodes;
	}
	
	protected ArrayList<Integer> getIndices(Rectangle r) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		if(isLeaf()) {
			for(int i = 0; i < size; i++) {
				if(boxes[i] != null && boxes[i].intersects(r)) {
					indices.add(entries[i]);
				}
			}
		} else {
			for(RNode<E> node : nodes) {
				if(node.getBox() != null && node.getBox().intersects(r)) {
					indices.addAll(node.getIndices(r));
				}
			}
		}
		return indices;		
	}

	protected ArrayList<Integer> getIndices(Point p) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		if(isLeaf()) {
			for(int i = 0; i < size; i++) {
				if(boxes[i] != null && boxes[i].contains(p)) {
					indices.add(entries[i]);
				}
			}
		} else {
			for(RNode<E> node : nodes) {
				if(node.getBox().contains(p)) {
					indices.addAll(node.getIndices(p));
				}
			}
		}
		return indices;
	}
	
	protected boolean remove(Rectangle box, int index) {
		boolean found = false;
		if(isLeaf()) {
			int pos = -1;
			for(int i = 0; i < entries.length; i++) {
				if(entries[i] == index) {
					found = true;
					pos = i;
				}
			}
			if(found) {
				for(int i = pos + 1; i < entries.length; i++) {
					entries[i-1] = entries[i];
					entries[i] = 0;
					boxes[i-1] = boxes[i];
					boxes[i] = null;
				}
				size--;
				if(size > 0) {
					this.box = new Rectangle(boxes[0]);
					for(Rectangle rect : boxes) {
						if(rect != null) {
							this.box.add(rect);
						}
					}
				} else {
					this.box = null;
				}
			}
		} else {
			boolean empty = false;
			for(RNode<E> node : nodes) {
				if(node.getBox().contains(box) && node.remove(box, index)) {
					found = true;
					empty = (node.getBox() == null);
				}
			}
			if(empty) {
				reOrder();
			} else if(found) {
				size--;
				this.box = new Rectangle(nodes[0].getBox());
				for(RNode<E> node : nodes) {
					this.box.add(node.getBox());
				}
			} 
		}

		return found;
	}
	
	private void reOrder() {
		int[] indices = getIndices();
		nodes = null;
		entries = new int[max];
		boxes = new Rectangle[max];
		box = new Rectangle();
		size = 0;
		for(int i : indices) {
			if(tree.getBox(i) != null) {
				add(i, tree.getBox(i));
			}
		}
	}
	
	private int[] getIndices() {
		if(nodes == null) {
			return Arrays.copyOf(entries, size);
		} else {
			int[] indices = new int[0];
			for(RNode<E> node : nodes) {
				int[] nodeIndices = node.getIndices();
				int[] temp = Arrays.copyOf(indices, indices.length + nodeIndices.length);
				System.arraycopy(nodeIndices, 0, temp, indices.length, nodeIndices.length);
				indices = temp;
			}
			return indices;
		}
	}
	
	protected void add(int index, Rectangle box) {
//		Rectangle box = box2D.getBounds();
		if(size < max) {
			if(this.box == null || this.box.isEmpty()) {
				this.box = new Rectangle(box);
			}
			entries[size] = index;
			boxes[size] = box;
		} else if(size >= max){
			if(nodes == null) {
				nodes = new RNode[max];
				for(int i = 0; i < entries.length; i++) {
					RNode<E> node = new RNode<E>(max, min, tree);
					node.add(entries[i], boxes[i]);
					nodes[i] = node;
				}
			}
			RNode<E> smallestNode = nodes[0];
			Rectangle smallestRect = box.union(smallestNode.getBox());
			int smallestArea = smallestRect.y * smallestRect.x - smallestNode.getBox().x * smallestNode.getBox().y;
			for(RNode<E> node : nodes) {
				Rectangle union = box.union(node.getBox());
				if(union.x * union.y - node.getBox().x * node.getBox().y < smallestArea) {
					smallestNode = node;
					smallestArea = union.x * union.y;
				} else if(union.x * union.y - node.getBox().x * node.getBox().y == smallestArea) {
					if(node.getBox().x * node.getBox().y < smallestNode.getBox().y * smallestNode.getBox().y) {
						smallestNode = node;
						smallestArea = union.x * union.y;
					}
				}
			}
			smallestNode.add(index, box);
		}
		this.box.add(box);
		size++;
	}

	protected boolean isLeaf() {
		return nodes == null;
	}
}
