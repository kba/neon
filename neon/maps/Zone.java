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

package neon.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

import neon.resources.RZoneTheme;
import neon.ui.graphics.*;
import neon.util.spatial.*;
import neon.core.Engine;
import neon.entities.Creature;
import neon.entities.Item;
import neon.entities.Light;

public class Zone implements Externalizable {
	private static ZComparator comparator = new ZComparator();
	private String name;
	private int map;
	private RZoneTheme theme;
	private int index;
	private HashMap<Point, Integer> lights = new HashMap<Point, Integer>();
	private SimpleIndex<Long> creatures = new SimpleIndex<Long>();
	private GridIndex<Long> items = new GridIndex<Long>();
	private RTree<Region> regions;
	private RTree<Long> top = new RTree<Long>(100,40);
	
	/**
	 * Initializes a new zone.
	 * 
	 * @param name
	 * @param map
	 * @param index
	 */
	public Zone(String name, int map, int index) {
		this.map = map;
		this.name = name;
		this.index = index;
		regions = new RTree<Region>(100, 40, Engine.getAtlas().getCache(), map + ":" + index);
	}
	
	/**
	 * Initializes a new zone with a theme.
	 * 
	 * @param name
	 * @param map
	 * @param theme
	 * @param index
	 */
	public Zone(String name, int map, RZoneTheme theme, int index) {
		this(name, map, index);
		this.theme = theme;
	}
	
	/**
	 * @param bounds
	 * @return	all renderables within the given bounds
	 */
	public Collection<Renderable> getRenderables(Rectangle bounds) {
		ArrayList<Renderable> elements = new ArrayList<Renderable>();
		for(long uid : creatures.getElements(bounds)) {
			elements.add(Engine.getStore().getEntity(uid).renderer);
		}
		for(long uid : items.getElements(bounds)) {
			elements.add(Engine.getStore().getEntity(uid).renderer);
		}
//		for(Region r : regions.getElements(bounds)) {
			elements.addAll(regions.getElements(bounds));
//		}
		for(long uid : top.getElements(bounds)) {
			elements.add(Engine.getStore().getEntity(uid).renderer);
		}
		return elements;
	}
	
	/**
	 * @return	the index of this zone
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * @return	whether this is a randomly generated zone
	 */
	public boolean isRandom() {
		return theme != null;
	}
	
	protected void fix() {
		theme = null;
	}
	
	protected RZoneTheme getTheme() {
		return theme;
	}
	
	protected int getMap() {
		return map;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Returns a list of creature in this zone.
	 * 
	 * @return	the creatures in this zone
	 */
	public Collection<Long> getCreatures() {
		return creatures.getElements();
	}
	
	/**
	 * @param box	a rectangle
	 * @return	all creatures in the given rectangle
	 */
	public Collection<Creature> getCreatures(Rectangle box) {
		ArrayList<Creature> list = new ArrayList<Creature>();
		for(long uid : creatures.getElements()) {
			Creature c = (Creature)Engine.getStore().getEntity(uid);
			if(box.contains(c.bounds.x, c.bounds.y)) {
				list.add(c);
			}
		}
		return list;
	}
	
	/**
	 * Returns the creature on the requested position.
	 * 
	 * @param p	a position
	 * @return	the creature on the given position, null if there is none
	 */
	public Creature getCreature(Point p) {
		for(long uid : creatures.getElements()) {
			Creature c = (Creature)Engine.getStore().getEntity(uid);
			if(p.distance(c.bounds.x, c.bounds.y) < 1) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Adds a creature to this zone.
	 * 
	 * @param c	the creature to add
	 */
	public void addCreature(Creature c) {
		creatures.insert(c.getUID(), c.bounds);
	}
	
	/**
	 * @return 	the name of this zone
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return 	the height of this zone
	 */
	public int getHeight() {
		return regions.getHeight();
	}

	/**
	 * @return 	the width of this zone
	 */
	public int getWidth() {
		return regions.getWidth();
	}
	
	/**
	 * @param window	a rectangle
	 * @return			all regions overlapping with the given window
	 */
	public Collection<Region> getRegions(Rectangle window) {
		return regions.getElements(window);
	}
	
	/**
	 * @param point	a point
	 * @return		all regions containing the given point
	 */
	public Collection<Region> getRegions(Point point) {
		return regions.getElements(new Rectangle(point.x, point.y, 1, 1));
	}
	
	/**
	 * @param point	a position in this zone
	 * @return	all items on the given position
	 */
	public Collection<Long> getItems(Point point) {
		return items.getElements(point);
	}
	
	/**
	 * @param box	a rectangle
	 * @return	all items within the given rectangle
	 */
	public Collection<Long> getItems(Rectangle box) {
		return items.getElements(box);
	}
	
	public Collection<Long> getItems() {
		return items.getElements();
	}
	
	/**
	 * @param p	a point
	 * @return	the highest-order region containing this point
	 */
	public Region getRegion(Point p) {
		ArrayList<Region> buffer = new ArrayList<Region>(getRegions(p));
		Collections.sort(buffer, comparator);
		return buffer.size() > 0 ? buffer.get(buffer.size() - 1) : null;
	}
	
	/**
	 * Adds a region to this map.
	 * 
	 * @param r	the region to add
	 */
	protected void addRegion(Region r) {
		regions.insert(r, r.getBounds());
	}

	/**
	 * Removes a region from this map.
	 * 
	 * @param r	the region to remove
	 */
	public void removeRegion(Region r) {
		regions.remove(r);
	}
	
	/**
	 * @return	a <code>Collection</code> with all regions in this map.
	 */
	public Collection<Region> getRegions() {
		return regions.getElements();
	}

	public void addItem(Item item) {
		if(item.resource.top) {
			top.insert(item.getUID(), item.bounds);
		} else {
			items.insert(item.getUID(), item.bounds);
		}
		if(item instanceof Light) {
			Point p = item.bounds.getLocation();
			if(!lights.containsKey(p)) {
				lights.put(p, 0);
			}
			lights.put(p, lights.get(p) + 1);
		}
	}

	/**
	 * Removes a creature from this map.
	 * 
	 * @param uid	the uid of the creature to remove
	 */
	public void removeCreature(long uid) {
		creatures.remove(uid);
	}
	
	public void removeItem(Item item) {
		items.remove(item.getUID());
		if(item instanceof Light) {
			Point point = new Point(item.bounds.x, item.bounds.y);
			lights.put(point, lights.get(point) - 1);
			if(lights.get(point) < 1) {
				lights.remove(point);
			}
		}
	}

	/**
	 * @return	a hashmap with all lights in this zone
	 */
	public HashMap<Point, Integer> getLightMap() {
		return lights;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		index = in.readInt();
		map = in.readInt();
		name = in.readUTF();
		String t = in.readUTF();
		if(!t.isEmpty()) {
			theme = (RZoneTheme)Engine.getResources().getResource(t, "theme");
		}
		
		// items
		top = new RTree<Long>(100,40);
		items = new GridIndex<Long>();
		lights = new HashMap<Point, Integer>();
		int iSize = in.readInt();
		for(int i = 0; i < iSize; i++) {
			long uid = in.readLong();
			Item item = (Item)Engine.getStore().getEntity(uid);
			addItem(item);
		}
		int tSize = in.readInt();
		for(int i = 0; i < tSize; i++) {
			long uid = in.readLong();
			Item item = (Item)Engine.getStore().getEntity(uid);
			addItem(item);
		}
				
		// creatures
		creatures = new SimpleIndex<Long>();
		int cSize = in.readInt();
		for(int i = 0; i < cSize; i++) {
			long uid = in.readLong();
			Rectangle bounds = Engine.getStore().getEntity(uid).getBounds();
			creatures.insert(uid, bounds);
		}
		
		// regions
		regions = new RTree<Region>(100, 40, Engine.getAtlas().getCache(), map + ":" + index);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(index);
		out.writeInt(map);
		out.writeUTF(name);
		if(theme != null) {
			out.writeUTF(theme.id);
		} else {
			out.writeUTF("");
		}
		
		// items
		out.writeInt(items.getElements().size());
		for(long l : items.getElements()) {
			out.writeLong(l);
		}
		out.writeInt(top.getElements().size());
		for(long l : top.getElements()) {
			out.writeLong(l);
		}
		
		// creatures
		out.writeInt(creatures.getElements().size());
		for(long l : creatures.getElements()) {
			out.writeLong(l);
		}
	}
}
