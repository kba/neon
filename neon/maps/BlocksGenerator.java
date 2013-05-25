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

package neon.maps;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * This class contains methods to generate collections of rectangles that are packed in different ways.
 * 
 * @author mdriesen
 */
public class BlocksGenerator {
	/**
	 * Generates a given number of closely packed rectangles with given maximum and minimum size and aspect ratio within 
	 * a rectangle with a given width and height.
	 * 
	 * @param w
	 * @param h
	 * @param minW
	 * @param maxW
	 * @param ratio
	 * @param numRecs
	 * @return	an @code{ArrayList} of @code{Rectangle}s
	 */
	public static ArrayList<Rectangle> generatePackedRectangles(int w, int h, int minW, int maxW, double ratio, int numRecs) {
		return new RectangleGenerator(w, h, minW, maxW, ratio).generate(numRecs);
	}
	
	/**
	 * Generates a given number of loosely packed rectangles.
	 * @param w
	 * @param h
	 * @param minW
	 * @param maxW
	 * @param ratio
	 * @param numRecs
	 * @return	an @code{ArrayList} of @code{Rectangle}s
	 */
	public static ArrayList<Rectangle> generateSparseRectangles(int w, int h, int minW, int maxW, double ratio, int numRecs) {
		return new SparseGenerator(w, h, minW, maxW, ratio).generate(numRecs);
	}
	
	/**
	 * Divides a rectangle with the given width and height in smaller rectangles with given minimum and maximum width, using a
	 * BSP algorithm.
	 * 
	 * @param w
	 * @param h
	 * @param minW
	 * @param maxW
	 * @return	an @code{ArrayList} of @code{Rectangle}s
	 */
	public static ArrayList<Rectangle> generateBSPRectangles(int w, int h, int minW, int maxW) {
		return new BSPGenerator(w, h, minW, maxW).generate();
	}
	
	private static class SparseGenerator {
		private int w, h, minW, maxW;
		private double ratio;
		private ArrayList<Rectangle> rooms;
		private Area area;
		
		private SparseGenerator(int w, int h, int minW, int maxW, double ratio) {
			this.w = w;
			this.h = h;
			this.minW = minW;
			this.maxW = maxW;
			this.ratio = ratio;
			rooms = new ArrayList<Rectangle>();
		}
		
		private ArrayList<Rectangle> generate(int numRecs) {
			for(int i = 0; i < numRecs; i++) {
				Rectangle room = randomRoom();
				if(room != null) {
					rooms.add(room);
				}
			}

			return rooms;		
		}
		
		private Rectangle randomRoom() {
			Rectangle r = MapUtils.randomRectangle(minW, maxW, minW, maxW, ratio);
			
			r.x = MapUtils.random(0, w - r.width);
			r.y = MapUtils.random(0, h - r.height);
			if(rooms.size() == 0) {	// gewoon kamer in midden vlammen
				area = new Area(r);
			} else {	// kamer willekeurig wat rondbewegen
				int i = 0;
				while(area.intersects(r) && i < 100) {	// 100 keer proberen
					r.x = MapUtils.random(0, w - r.width);
					r.y = MapUtils.random(0, h - r.height);
					i++;
				}
				if(i > 99) {
					return null;
				} else {
					area.add(new Area(r));
				}
			}

			return r;
		}
	}
	
	private static class BSPGenerator {
		private int w, h, minW, maxW;
		
		private BSPGenerator(int w, int h, int minW, int maxW) {
			this.w = w;
			this.h = h;
			this.minW = minW;
			this.maxW = maxW;
		}
		
		private ArrayList<Rectangle> generate() {
			ArrayList<Rectangle> buffer = new ArrayList<Rectangle>();
			ArrayList<Rectangle> result = new ArrayList<Rectangle>();
			
			buffer.add(new Rectangle(w, h));
			
			while(buffer.size() > 0) {
				ListIterator<Rectangle> i = buffer.listIterator();
				while(i.hasNext()) {
					Rectangle r = i.next();
					i.remove();
					Rectangle r1;
					Rectangle r2;
					if(r.width < r.height) {
						int dy = MapUtils.random(r.y + minW, r.y + r.height - minW);
						r1 = new Rectangle(r.x, r.y, r.width, dy - r.y);
						r2 = new Rectangle(r.x, dy, r.width, r.y + r.height - dy);
					} else {
						int dx = MapUtils.random(r.x + minW, r.x + r.width - minW);
						r1 = new Rectangle(r.x, r.y, dx - r.x, r.height);
						r2 = new Rectangle(dx, r.y, r.x + r.width - dx, r.height);
					}
					if(r1.width < maxW && r1.height < maxW) {
						result.add(r1);
					} else {
						i.add(r1);
					}
					if(r2.width < maxW && r2.height < maxW) {
						result.add(r2);
					} else {
						i.add(r2);
					}
				}
			}
			
			return result;
		}
			
	}
	
	private static class RectangleGenerator {
		private int w, h, minW, maxW;
		private double ratio;
		private ArrayList<Rectangle> rooms;
		private Area area;
		
		private RectangleGenerator(int w, int h, int minW, int maxW, double ratio) {
			this.w = w;
			this.h = h;
			this.minW = minW;
			this.maxW = maxW;
			this.ratio = ratio;
			rooms = new ArrayList<Rectangle>();
		}
		
		private ArrayList<Rectangle> generate(int numRecs) {
			for(int i = 0; i < numRecs; i++) {
				Rectangle room = randomRoom();
				if(room != null) {
					rooms.add(room);
				}
			}

			return rooms;		
		}
		
		private Rectangle randomRoom() {
			Rectangle r = MapUtils.randomRectangle(minW, maxW, minW, maxW, ratio);
			
			r.x = w/2 - r.width/2;
			r.y = h/2 - r.height/2;
			if(rooms.size() == 0) {	// gewoon kamer in midden vlammen
				area = new Area(r);
			} else {	// kamer in spiraal rondbewegen
				int i = 0;
				while(area.intersects(r) && i < 9*w*h) {
					r.x = w/2 - r.width/2 + (int)(i/3*Math.cos(i/3));
					r.y = h/2 - r.height/2 + (int)(i/3*Math.sin(i/3));
//					System.out.println(rooms.size() + "; " + r.x + ", " + r.y + "; " + w + ", " + h);					
					i++;
				}
				if(r.x < 0 || r.y < 0 || r.x + r.width > w || r.y + r.height > h) {
					return null;	// als het niet lukt, opgeven
				}
				area.add(new Area(r));
			}
			
			return r;
		}		
	}
}
