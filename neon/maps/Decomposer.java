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

package neon.maps;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collection;

public class Decomposer {
	/**
	 * This method uses a quadtree to try and split an area in rectangles. For areas with holes in,
	 * this is not guaranteed to work perfectly.
	 * 
	 * @param area	the area to split
	 * @return	a collection of rectangles
	 */
	public static Collection<Rectangle> split(Area area) {
		PathIterator iterator = area.getPathIterator(null);
		Tree tree = new Tree(area.getBounds());
		
		while(!iterator.isDone()) {
			double[] point = new double[2];
			iterator.currentSegment(point);
			tree.addPoint((int)(point[0]), (int)(point[1]));
			iterator.next();
		}
		
		return tree.getRectangles();
	}
	
	private static class Tree {
		protected Node node;
		
		public Tree(Rectangle bounds) {
			node = new Node(bounds);
		}
		
		public void addPoint(int x, int y) {
			if(node.bounds.contains(x, y)) {
				node.addPoint(x, y);
			}
		}
		
		public Collection<Rectangle> getRectangles() {
			ArrayList<Rectangle> list = new ArrayList<Rectangle>();
			node.getRectangles(list);
			return list;
		}
	}
	
	private static class Node {
		protected Rectangle bounds;
		protected Node one;
		protected Node two;
		protected Node three;
		protected Node four;
		
		public Node(Rectangle bounds) {
			this.bounds = bounds;
		}

		public void addPoint(int x, int y) {
			if(one == null) {
				Rectangle rOne = new Rectangle(bounds.x, bounds.y, Math.abs(x - bounds.x), Math.abs(y - bounds.y));
				one = new Node(rOne);
				Rectangle rTwo = new Rectangle(x, bounds.y, Math.abs(bounds.width - (x - bounds.x)), Math.abs(y - bounds.y));
				two = new Node(rTwo);
				Rectangle rThree = new Rectangle(bounds.x, y, Math.abs(x - bounds.x), Math.abs(bounds.height - (y - bounds.y)));
				three = new Node(rThree);
				Rectangle rFour = new Rectangle(x, y, Math.abs(bounds.width - (x - bounds.x)), Math.abs(bounds.height - (y - bounds.y)));
				four = new Node(rFour);
			} else if(!one.bounds.isEmpty() && one.bounds.contains(x, y)){
				one.addPoint(x, y);
			} else if(!two.bounds.isEmpty() && two.bounds.contains(x, y)) {
				two.addPoint(x, y);
			} else if(!three.bounds.isEmpty() && three.bounds.contains(x, y)) {
				three.addPoint(x, y);
			} else if(!four.bounds.isEmpty() && four.bounds.contains(x, y)) {
				four.addPoint(x, y);
			}
		}
		
		public void getRectangles(Collection<Rectangle> list) {
			if(one == null) {
				if(!bounds.isEmpty()) {
					list.add(bounds);
				}
			} else {
				one.getRectangles(list);
				two.getRectangles(list);
				three.getRectangles(list);
				four.getRectangles(list);
			}
		}
	}
}
