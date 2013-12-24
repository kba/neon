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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class GridIndex<E> implements SpatialIndex<E> {
	private Multimap<Point, E> elements = ArrayListMultimap.create();

	public List<E> getElements() {
		ArrayList<E> list = new ArrayList<E>();
		for(Point p : elements.keySet()) {
			list.addAll(elements.get(p));
		}
		return list;
	}

	public synchronized List<E> getElements(Rectangle bounds) {
		ArrayList<E> list = new ArrayList<E>();
		for(Point p : elements.keySet()) {
			if(bounds.contains(p)) {
				list.addAll(elements.get(p));
			}
		}
		return list;
	}

	public Collection<E> getElements(Point point) {
		if(elements.get(point) != null) {
			return elements.get(point);			
		} else {
			return new ArrayList<E>();
		}
	}

	public synchronized void insert(E e, Rectangle bounds) {
		for(int x = bounds.x; x < bounds.x + bounds.width; x++) {
			for(int y = bounds.y; y < bounds.y + bounds.height; y++) {
				elements.put(new Point(x, y), e);
			}
		}
	}

	public void remove(E e) {
		for(Point p : elements.keySet()) {
			elements.get(p).remove(e);
		}
	}
	
	public void clear() {
		elements.clear();
	}
	
	public int getWidth() {
		int w = 0;
		for(Point p : elements.keySet()) {
			w = Math.max(w, p.x);
		}
		return w;
	}

	public int getHeight() {
		int h = 0;
		for(Point p : elements.keySet()) {
			h = Math.max(h, p.y);
		}
		return h;
	}
}
