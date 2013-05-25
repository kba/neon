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

package neon.maps;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Arrays;

public class MapUtils {
	protected static final int WALL = 0;
	protected static final int FLOOR = 1;
	protected static final int DOOR = 2;
	protected static final int DOOR_CLOSED = 3;
	protected static final int DOOR_LOCKED = 4;
	protected static final int CORRIDOR = 5;
	protected static final int WALL_ROOM = 6;
	protected static final int ENTRY = 7;
	protected static final int CORNER = 8;
	protected static final int TEMP = 9;
	
	/**
	 * Returns a rectangle with the given min/max width and height.
	 * 
	 * @param minW
	 * @param maxW
	 * @param minH
	 * @param maxH
	 * @return	a random Rectangle with its origin a (0,0)
	 */
	public static Rectangle randomRectangle(int minW, int maxW, int minH, int maxH) {
		int w = random(minW, maxW);
		int h = random(minH, maxH);
		return new Rectangle(w, h);
	}
	
	/**
	 * Returns a square with the given min/max side length.
	 * 
	 * @param minW
	 * @param maxW
	 * @return	a square with its origin at (0,0)
	 */
	public static Rectangle randomSquare(int minW, int maxW) {
		int w = random(minW, maxW);
		return new Rectangle(w, w);
	}
	
	/**
	 * Returns a rectangle with the given min/max dimensions and a maximum width/height or height/width ratio.
	 * 
	 * @param minW
	 * @param maxW
	 * @param minH
	 * @param maxH
	 * @param ratio
	 * @return	a random Rectangle with its origin at (0,0)
	 */
	public static Rectangle randomRectangle(int minW, int maxW, int minH, int maxH, double ratio) {
		int w = random(minW, maxW);
		int hMin = Math.max(minH, (int)(w/ratio));
		int hMax = Math.min(maxH, (int)(w*ratio));
		int h = random(hMin, hMax);
		return new Rectangle(w, h);
	}
	
	/**
	 * Generates a random rectangle within the given rectangle.
	 * 
	 * @param minW
	 * @param bounds
	 * @return	a @code{Rectangle}
	 */
	public static Rectangle randomRectangle(int minW, Rectangle bounds) {
		int w = random(minW, bounds.width - 1);
		int h = random(minW, bounds.height - 1);
		Rectangle rec = new Rectangle(w, h);
		
		while(!bounds.contains(rec)) {
			rec.x = random(bounds.x, bounds.x + bounds.width - w);
			rec.y = random(bounds.y, bounds.y + bounds.height - h);
		}
		return rec;
	}

	/**
	 * @param min
	 * @param max
	 * @return	a random int between min and max (min and max included)
	 */
	public static int random(int min, int max) {
		return (int)(min + (max - min + 1)*Math.random());
	}
	
	/**
	 * Returns a random polygon with approximately the given number of vertices. The polygon is 
	 * not guaranteed to be convex, but will not intersect itself.
	 * 
	 * @param r			a rectangle
	 * @param corners	the number of vertices
	 * @return	a polygon that is bounded by the given rectangle
	 */
	public static Polygon randomPolygon(Rectangle r, int corners) {
		Rectangle up = new Rectangle(r.x + r.width/4, r.y, r.width/2, r.height/4);
		Rectangle right = new Rectangle(r.x + 3*r.width/4, r.y + r.height/4, r.width/4, r.height/2);
		Rectangle down = new Rectangle(r.x + r.width/4, r.y + 3*r.height/4, r.width/2, r.height/4);
		Rectangle left = new Rectangle(r.x, r.y + r.height/4, r.width/4, r.height/2);
		
		int numPoints = corners/4;
		
		int[] xPoints = new int[4*numPoints];
		int[] yPoints = new int[4*numPoints];
		int[] buffer = new int[numPoints];
		
		// boven
		for(int i = 0; i < numPoints; i++) {
			Point p = randomPoint(up);
			buffer[i] = p.x;
			yPoints[i] = p.y;
		}
		Arrays.sort(buffer);
		System.arraycopy(buffer, 0, xPoints, 0, numPoints);
		
		// rechts
		for(int i = 0; i < numPoints; i++) {
			Point p = randomPoint(right);
			xPoints[numPoints + i] = p.x;
			buffer[i] = p.y;
		}
		Arrays.sort(buffer);
		System.arraycopy(buffer, 0, yPoints, numPoints, numPoints);
		
		// onder
		for(int i = 0; i < numPoints; i++) {
			Point p = randomPoint(down);
			buffer[i] = p.x;
			yPoints[2*numPoints + i] = p.y;
		}
		Arrays.sort(buffer);
		System.arraycopy(reverse(buffer), 0, xPoints, 2*numPoints, numPoints);
		
		// links
		for(int i = 0; i < numPoints; i++) {
			Point p = randomPoint(left);
			xPoints[3*numPoints + i] = p.x;
			buffer[i] = p.y;
		}
		Arrays.sort(buffer);
		System.arraycopy(reverse(buffer), 0, yPoints, 3*numPoints, numPoints);
		
		return new Polygon(xPoints, yPoints, 4*numPoints);		
	}
	
	private static int[] reverse(int[] array) {
		int length = array.length;
		int[] reverse = new int[length];
		for(int i = 0; i < length; i++) {
			reverse[length - i - 1] = array[i];
		}
		
		return reverse;
	}
	
	/**
	 * @param r
	 * @return	a random point in the given rectangle
	 */
	public static Point randomPoint(Rectangle r) {
		return new Point(random(r.x, r.x + r.width), random(r.y, r.y + r.height));
	}
	
	/**
	 * Returns a ribbon of width one, running from one side of a rectangle to the opposite one.
	 * 
	 * @param r
	 * @param horizontal
	 * @return	an array of points contained in the ribbon
	 */
	public static Point[] randomRibbon(Rectangle r, boolean horizontal) {
		// richting: true is horizontaal, false is verticaal
		Point ribbon[]; 
		
		if(horizontal) {
			ribbon = new Point[r.width];
			// startpositie
			int y = random(r.y, r.y + r.height);
			ribbon[0] = new Point(r.x, y);	
			for(int i = 1; i < r.width; i++) {	// volgende punten
				y = random(Math.max(r.y, y - 1), Math.min(r.y + r.height, y + 1));
				ribbon[i] = new Point(r.x + i, y);
			}
		} else {
			ribbon = new Point[r.height];			
			// startpositie
			int x = random(r.x, r.x + r.width);
			ribbon[0] = new Point(x, r.y);
			for(int i = 1; i < r.height; i++) {
				x = random(Math.max(r.x, x - 1), Math.min(r.x + r.width, x + 1));
				ribbon[i] = new Point(x, r.y + i);				
			}
		}
		
		return ribbon;
	}
	
	/**
	 * @param array
	 * @param ref
	 * @return	the number of times the given boolean occurs in the given array
	 */
	public static int amount(boolean[] array, boolean ref) {
		int count = 0;
		for(boolean o : array) {
			if(ref == o) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * @param x1
	 * @param x2
	 * @return	the average of two integers
	 */
	public static int average(int x1, int x2) {
		return (x1 + x2)/2;
	}
}
