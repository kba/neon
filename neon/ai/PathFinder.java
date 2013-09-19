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

package neon.ai;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import neon.core.Engine;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.property.Skill;
import neon.maps.Region;
import neon.resources.RItem;

public class PathFinder {
	private static HashMap<Point, Integer> evaluated;
	private static Point to;
	private static Creature mover;
	
	public static Point[] findPath(Creature creature, Point origin, Point destination) {
		// punten
		Point from = origin;
		to = destination;
		mover = creature;
		
		// lijstjes van nodes
		evaluated = new HashMap<Point, Integer>();							// de bezochte nodes en hun kost vanaf from
		Queue<Point> todo = new PriorityQueue<>(10, new NodeComparator());	// de nodes die nog moeten bekeken worden
		HashMap<Point, Point> links = new HashMap<Point, Point>();			// om te kijken hoe een node bereikt is
		
		// startpunt
		todo.add(from);
		evaluated.put(from, 0);
		
		int i = 10;
		while(!todo.isEmpty() && i-- > 0) {
			Point next = todo.poll();

			for(Point neighbour : neighbours(next)) {
				if(neighbour.equals(to)) {
					todo.clear();
					links.put(to, next);
					next = null;
					break;
				} else if(Engine.getAtlas().getCurrentZone().getRegion(neighbour).getMovMod() == Region.Modifier.BLOCK) {
					continue;	// als terrein geblokkeerd is, volgende punt
				}
				int penalty = doorPenalty(neighbour) + terrainPenalty(neighbour);
				int cost = evaluated.get(next) + manhattan(to, neighbour) + 1 + penalty;	// huidige kost van gebuur
				// als gebuur al in todo lijst zit met hogere kost: eruit halen
				if(todo.contains(neighbour) && cost < evaluated.get(neighbour) + manhattan(to, neighbour)) {
					todo.remove(neighbour);
				}
				// als gebuur al evaluated is met hogere kost: eruit halen
				if(evaluated.containsKey(neighbour) && cost < evaluated.get(neighbour) + manhattan(to, neighbour)) {
					evaluated.remove(neighbour);
				}
				// als gebuur nog nergens in zit (of er juist uitgehaald is): in todo steken
				if(!todo.contains(neighbour) && !evaluated.containsKey(neighbour)) {
					links.put(neighbour, next);
					evaluated.put(neighbour, evaluated.get(next) + 1 + penalty);
					todo.add(neighbour);
				}
			}
		}
		
		ArrayList<Point> path = new ArrayList<Point>();
		if(!links.containsKey(to)) {
			to = todo.poll();	// als path afgebroken werd, met huidige schatting voortdoen
		}						// dit geeft bijwijle wel vreemd gedrag
		while(!from.equals(to)) {
			path.add(0, to);
			to = links.get(to);
		}
		return path.toArray(new Point[path.size()]);
	}
	
	private static Point[] neighbours(Point current) {
		Point[] neighbours = new Point[8];
		neighbours[0] = new Point(current.x - 1, current.y);
		neighbours[1] = new Point(current.x + 1, current.y);
		neighbours[2] = new Point(current.x - 1, current.y - 1);
		neighbours[3] = new Point(current.x + 1, current.y - 1);
		neighbours[4] = new Point(current.x, current.y - 1);
		neighbours[5] = new Point(current.x - 1, current.y + 1);
		neighbours[6] = new Point(current.x + 1, current.y + 1);
		neighbours[7] = new Point(current.x , current.y + 1);
		return neighbours;
	}
	
	/*
	 * manhattan distance tussen punten
	 */
	private static int manhattan(Point one, Point two) {
		return Math.abs(one.x - two.x) + Math.abs(one.y - two.y);
	}

	private static int terrainPenalty(Point neighbour) {
		// betere modifiers?
		switch(Engine.getAtlas().getCurrentZone().getRegion(neighbour).getMovMod()) {
		case SWIM: return (100 - mover.getSkill(Skill.SWIMMING))/5;
		case CLIMB: return (100 - mover.getSkill(Skill.CLIMBING))/5;
		default: return 0;
		}
	}
	
	private static int doorPenalty(Point neighbour) {
		for(long uid : Engine.getAtlas().getCurrentZone().getItems(neighbour)) {
			if(Engine.getStore().getEntity(uid) instanceof Door) {
				Door door = (Door)Engine.getStore().getEntity(uid);
				if(door.lock.isLocked()) {
					RItem key = door.lock.getKey();
					if(key != null && hasItem(mover, key)) {
						return 2;
					} else {
						return 100;	
					}
				} else if(door.lock.isClosed()) {
					return 1;
				}
			}
		}
		return 0;
	}
	
	@SuppressWarnings("serial")
	private static class NodeComparator implements Comparator<Point>, Serializable {
		public int compare(Point one, Point two) {
			return (evaluated.get(one) + manhattan(one, to)) - (evaluated.get(two) + manhattan(two, to));
	    }		
	}
	
	private static boolean hasItem(Creature creature, RItem item) {
		for(long uid : creature.inventory) {
			if(Engine.getStore().getEntity(uid).getID().equals(item.id)) {
				return true;
			}
		}
		return false;
	}
}
