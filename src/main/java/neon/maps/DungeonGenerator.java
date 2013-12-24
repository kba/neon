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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import neon.core.Engine;
import neon.entities.Container;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.EntityFactory;
import neon.entities.Item;
import neon.entities.property.Habitat;
import neon.maps.Region.Modifier;
import neon.util.Dice;
import neon.resources.RCreature;
import neon.resources.RItem;
import neon.resources.RTerrain;
import neon.resources.RZoneTheme;

/**
 * Generates a single dungeon zone.
 * 
 * @author	mdriesen
 */
public class DungeonGenerator {
	// zone info
	private RZoneTheme theme;
	private Zone zone;
	
	// dingen
	private int[][] tiles;		// informatie over het soort terrain
	private String[][] terrain;	// terrain op die positie
	
	public DungeonGenerator(RZoneTheme theme) {
		this.theme = theme;
	}
	
	public DungeonGenerator(Zone zone) {
		this.zone = zone;
		theme = zone.getTheme();
	}
	
	/**
	 * Generates a zone.
	 * 
	 * @param door		the door used to enter this zone
	 * @param previous	the zone that contains the door used to enter this zone
	 */
	public void generate(Door door, Zone previous) {
		// de map die deze zone bevat
		Dungeon map = (Dungeon)Engine.getAtlas().getMap(zone.getMap());
		
		// terrain genereren
		generateTiles();
		
		// hoogte en breedte van gegenereerde zone
		int width = tiles.length;
		int height = tiles[0].length;
		
		// regions maken van terrain
		generateEngineContent(width, height);
		zone.fix();

		// deurtje zetten naar previous zone
		Point p = new Point(0,0);
		do { 
			p.x = Dice.roll(1, width, -1);
			p.y = Dice.roll(1, height, -1);
		} while(tiles[p.x][p.y] != MapUtils.FLOOR || !zone.getItems(p).isEmpty());
		
		Rectangle bounds = door.getShapeComponent();
		Point destPoint = new Point(bounds.x, bounds.y);
		int destMap = previous.getMap();
		int destZone = previous.getIndex();
		String doorType = theme.doors.split(",")[0];
		Door tdoor = (Door)EntityFactory.getItem(doorType, p.x, p.y, Engine.getStore().createNewEntityUID());
		Engine.getStore().addEntity(tdoor);
		tiles[p.x][p.y] = MapUtils.DOOR;
		tdoor.portal.setDestination(destPoint, destZone, destMap);
		tdoor.lock.open();
		zone.addItem(tdoor);
		door.portal.setDestPos(p);

		// kijken of er een deur naar andere zone nodig is
		Collection<Integer> connections = map.getConnections(zone.getIndex());
		if(connections != null) {
			for(int to : connections) {
				ArrayList<Door> doors = new ArrayList<Door>();
				doors.add(door);
				if(to != previous.getIndex()) {
					Point pos = new Point(0,0);
					do { 
						pos.x = Dice.roll(1, width, -1);
						pos.y = Dice.roll(1, height, -1);
					} while(tiles[pos.x][pos.y] != MapUtils.FLOOR || !zone.getItems(pos).isEmpty());

					Door toDoor = (Door)EntityFactory.getItem(theme.doors.split(",")[0], 
							pos.x, pos.y, Engine.getStore().createNewEntityUID());
					Engine.getStore().addEntity(toDoor);
					tiles[pos.x][pos.y] = MapUtils.DOOR;
					toDoor.lock.open();
					toDoor.portal.setDestination(null, to, 0);
					zone.addItem(toDoor);
				} else {	// meerdere deuren tussen twee zones
					for(long uid : previous.getItems()) {
						if(Engine.getStore().getEntity(uid) instanceof Door) {
							Door fromDoor = (Door)Engine.getStore().getEntity(uid);
							if(!doors.contains(fromDoor) && fromDoor.portal.getDestMap() == 0 && 
									fromDoor.portal.getDestZone() == zone.getIndex()) {
								Point pos = new Point(0,0);
								do { 
									pos.x = Dice.roll(1, width, -1);
									pos.y = Dice.roll(1, height, -1);
								} while(tiles[pos.x][pos.y] != MapUtils.FLOOR && !zone.getItems(pos).isEmpty());

								Door toDoor = (Door)EntityFactory.getItem(theme.doors.split(",")[0], 
										pos.x, pos.y, Engine.getStore().createNewEntityUID());
								Engine.getStore().addEntity(toDoor);
								tiles[pos.x][pos.y] = MapUtils.DOOR;
								toDoor.lock.open();
								Rectangle fBounds = fromDoor.getShapeComponent();
								toDoor.portal.setDestination(new Point(fBounds.x, fBounds.y), to, 0);
								zone.addItem(toDoor);
								fromDoor.portal.setDestPos(pos);
								break;
							}
							doors.add(fromDoor);
						}
					}
				}
			}
		}
		
		// effen kijken of er een random quest object moet aangemaakt worden
		String object = Engine.getQuestTracker().getNextRequestedObject();
		if(object != null) {
			Point p1 = new Point(0,0);
			do { 
				p1.x = Dice.roll(1, width, -1);
				p1.y = Dice.roll(1, height, -1);
			} while(tiles[p1.x][p1.y] != MapUtils.FLOOR);
			if(Engine.getResources().getResource(object) instanceof RItem) {
				Item item = EntityFactory.getItem(object, p1.x, p1.y, 
						Engine.getStore().createNewEntityUID());
				Engine.getStore().addEntity(item);
				zone.addItem(item);				
			} else if(Engine.getResources().getResource(object) instanceof RCreature) {
				Creature creature = EntityFactory.getCreature(object, p1.x, p1.y, 
						Engine.getStore().createNewEntityUID());
				Engine.getStore().addEntity(creature);
				zone.addCreature(creature);				
			}
		}
	}

	/**
	 * Generates a single zone from a given theme.
	 */
	public String[][] generateTiles() {
		// hoogte en breedte van dungeon
		int width = MapUtils.random(theme.min, theme.max);
		int height = MapUtils.random(theme.min, theme.max);
		
		// basis terrein zonder features
		tiles = generateBaseTiles(theme.type, width, height);
		terrain = makeTerrain(tiles, theme.floor.split(","));
		
		// schaalfactor voor genereren van features, creatures en items
		double ratio = (width*height)/Math.pow(MapUtils.average(theme.min, theme.max), 2);
		
		// features
		generateFeatures(theme.features, ratio);
		
		// creatures
		for(String creature : theme.creatures.keySet()) {
			for(int i = (int)(Dice.roll("1d" + theme.creatures.get(creature))*ratio); i > 0; i--) {
				Point p = new Point(0,0);
				do { 
					p.x = Dice.roll(1, width, -1);
					p.y = Dice.roll(1, height, -1);
				} while(tiles[p.x][p.y] != MapUtils.FLOOR);
				
				terrain[p.x][p.y] = terrain[p.x][p.y] + ";c:" + creature;
			}
		}

		// items
		for(String item : theme.items.keySet()) {
			for(int i = (int)(Dice.roll("1d" + theme.items.get(item))*ratio); i > 0; i--) {
				Point p = new Point(0,0);
				do { 
					p.x = Dice.roll(1, width, -1);
					p.y = Dice.roll(1, height, -1);
				} while(tiles[p.x][p.y] != MapUtils.FLOOR);

				terrain[p.x][p.y] = terrain[p.x][p.y] + ";i:" + item;
			}
		}
		
		return terrain;
	}

	private static int[][] generateBaseTiles(String type, int width, int height) {
		int[][] tiles = new int[width][height];
		switch(type) {
		case "cave": 
			tiles = makeTiles(MazeGenerator.generateSquashedMaze(width, height, 3), width, height);
			break;
		case "pits":
			tiles = CaveGenerator.generateOpenCave(width, height, 3);
			break;
		case "maze":
			tiles = makeTiles(MazeGenerator.generateMaze(width, height, 3, 50), width, height);
			break;
		case "mine":
			Area mine = MazeGenerator.generateSquashedMaze(width, height, 12);
			mine.add(MazeGenerator.generateMaze(width, height, 12, 40));
			tiles = makeTiles(mine, width, height);
			break;
		case "bsp":
			tiles = ComplexGenerator.generateBSPDungeon(width, height, 5, 8);
			break;
		case "packed":
			tiles = ComplexGenerator.generatePackedDungeon(width, height, 10, 4, 7);
			break;
		default: 
			tiles = ComplexGenerator.generateSparseDungeon(width, height, 5, 5, 15);
			break;
		}
		
		return tiles;
	}
	
	private void generateFeatures(Collection<Object[]> features, double ratio) {
		int width = terrain.length;
		int height = terrain[0].length;
		for(Object[] feature : features) {
			int s = (int)(feature[2]);
			String t = feature[0].toString();
			int n = (int)feature[3]*100;
			if(n > 100) {
				n = MapUtils.random(0, (int)(n*ratio/100));
			} else {
				n = (MapUtils.random(0, (int)(n*ratio)) > 50) ? 1 : 0;
			}

			if(feature[1].equals("lake")) {	// grote patch die gewoon alles overschrijft
				int size = 100/s;
				ArrayList<Rectangle> lakes = BlocksGenerator.generateSparseRectangles(width, height, width/size, height/size, 2, n);
				for(Rectangle r : lakes) {	// meer inkwakken
					FeatureGenerator.generateLake(terrain, t, r);
				}
			} else if(feature[1].equals("patch")) {	// patch die enkel floor tiles overschrijft
				// stukken inkwakken
				ArrayList<Rectangle> patches = BlocksGenerator.generateSparseRectangles(width, height, s, s, 2, n);
				for(Rectangle r : patches) {
					Polygon patch = MapUtils.randomPolygon(r, 16);
					for(int x = r.x; x < r.x + r.width; x++) {
						for(int y = r.y; y < r.y + r.height; y++) {
							if(patch.contains(x,y) && tiles[x][y] == MapUtils.FLOOR) {
								terrain[x][y] = t;
							}
						}
					}
				}
			} else if(feature[1].equals("chunk")) {	// patch die enkel wall tiles overschrijft
				ArrayList<Rectangle> chunks = BlocksGenerator.generateSparseRectangles(width, height, s, s, 2, n);
				for(Rectangle chunk : chunks) {
					for(int x = chunk.x; x < chunk.x + chunk.width; x++) {
						for(int y = chunk.y; y < chunk.y + chunk.height; y++) {
							if(tiles[x][y] == MapUtils.WALL || tiles[x][y] == MapUtils.WALL_ROOM || 
									tiles[x][y] == MapUtils.CORNER || tiles[x][y] == MapUtils.ENTRY) {
								terrain[x][y] = t;
							}
						}
					}
				}
			} else if(feature[1].equals("stain")) {	// patch die enkel exposed wall tiles overschrijft
				ArrayList<Rectangle> stains = BlocksGenerator.generateSparseRectangles(width, height, s, s, 2, n);
				for(Rectangle stain : stains) {
					for(int x = stain.x; x < stain.x + stain.width; x++) {
						for(int y = stain.y; y < stain.y + stain.height; y++) {
							if((tiles[x][y] == MapUtils.WALL || tiles[x][y] == MapUtils.WALL_ROOM || 
									tiles[x][y] == MapUtils.CORNER || tiles[x][y] == MapUtils.ENTRY) && exposed(tiles, x, y)) {
								terrain[x][y] = t;
							}
						}
					}
				}
			} else if(feature[1].equals("river")) {
				while(n-- > 0) {	// blijkbaar eerst >, dan --
					FeatureGenerator.generateRiver(terrain, tiles, t, s);
				}
			}
		}		
	}

	private static boolean exposed(int[][] tiles, int x, int y) {
		for(int i = x - 1; i < x + 2; i++) {
			for(int j = y - 1; j < y + 2; j++) {
				if(i > -1 && i < tiles.length && j > -1 && j < tiles[i].length && tiles[i][j] == MapUtils.FLOOR) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// om een string[][] in regions, items en creatures om te zetten
	private void generateEngineContent(int width, int height) {
		byte layer = 0;
		int d = 0;
		String[] doors = theme.doors.split(",");

		RTerrain rt = (RTerrain)Engine.getResources().getResource(theme.walls, "terrain");
		zone.addRegion(new Region(theme.walls, 0, 0, width, height, null, layer, rt));
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				String id;
				switch(tiles[x][y]) {	// juiste terrein leggen
//				case WALL_ROOM: zone.addRegion(new Region("wall_blood", x, y, 1, 1, null, layer + 1)); break;
//				case CORNER: zone.addRegion(new Region("ore_iron", x, y, 1, 1, null, layer + 1)); break;
//				case ENTRY: zone.addRegion(new Region("wall_adobe", x, y, 1, 1, null, layer + 1)); break;
				case MapUtils.DOOR_LOCKED:
				case MapUtils.DOOR_CLOSED:
				case MapUtils.DOOR:
					id = terrain[x][y].split(";")[0];
					d = MapUtils.random(1, doors.length) - 1;
					addDoor(id, doors[d], x, y, layer + 1);
					break;
				default:
					if(terrain[x][y] != null) {
						id = terrain[x][y].split(";")[0];
						rt = (RTerrain)Engine.getResources().getResource(id, "terrain");
						zone.addRegion(new Region(id, x, y, 1, 1, null, layer + 1, rt));
					}
					break;
				}
				
				if(terrain[x][y] != null) {	// kijken of hier items en creatures moeten komen
					String[] content = terrain[x][y].split(";");
					if(content.length > 1) {
						for(int j = 1; j < content.length; j++) {
							if(content[j].startsWith("i")) {
								addItem(content[j], x, y);
							} else if(content[j].startsWith("c")) {
								addCreature(content[j], x, y);
							}
						}
					}
				}

			}
		}		
	}
	
	private void addDoor(String terrain, String id, int x, int y, int layer) {
		Door door = (Door)EntityFactory.getItem(id, x, y, Engine.getStore().createNewEntityUID());
		Engine.getStore().addEntity(door);
		if(tiles[x][y] == MapUtils.DOOR_LOCKED) {
			door.lock.setLockDC(10);
			door.lock.lock();						
		} else if(tiles[x][y] == MapUtils.DOOR_CLOSED) {
			door.lock.close();
		}
		zone.addItem(door);
		RTerrain rt = (RTerrain)Engine.getResources().getResource(terrain, "terrain");
		zone.addRegion(new Region(terrain, x, y, 1, 1, null, layer + 1, rt));		
	}
	
	private void addCreature(String description, int x, int y) {
		String id = description.replace("c:", "");
		Creature creature = EntityFactory.getCreature(id, x, y, Engine.getStore().createNewEntityUID());
		// geen land creatures in water
		Rectangle bounds = creature.getShapeComponent();
		Modifier modifier = zone.getRegion(bounds.getLocation()).getMovMod();
		Habitat habitat = creature.species.habitat;
		if(habitat == Habitat.LAND && !(modifier == Modifier.NONE || modifier == Modifier.ICE)) {
			return;	// landdieren alleen op land zetten
		}
		Engine.getStore().addEntity(creature);
		zone.addCreature(creature);			
	}

	private void addItem(String description, int x, int y) {
		String id = description.replace("i:", "");
		Item item = EntityFactory.getItem(id, x, y, Engine.getStore().createNewEntityUID());
		Engine.getStore().addEntity(item);
		if(item instanceof Container) {
			for(String s : ((RItem.Container)item.resource).contents) {
				Item i = EntityFactory.getItem(s, Engine.getStore().createNewEntityUID());
				((Container)item).addItem(i.getUID());
				Engine.getStore().addEntity(i);
			}
		}
		zone.addItem(item);
	}

	private static int[][] makeTiles(Area area, int width, int height) {
		int[][] tiles = new int[width][height];
		for(int j = 0; j < height; j++) {
			for(int i = 0; i < width; i++) {
				if(area.contains(i, j)) {
					tiles[i][j] = MapUtils.FLOOR;
				} else {
					tiles[i][j] = MapUtils.WALL;					
				}
			}
		}
		return tiles;	
	}
	
	private static String[][] makeTerrain(int[][] tiles, String[] floors) {
		String terrain[][] = new String[tiles.length][tiles[0].length];
		
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[0].length; y++) {
				int f = MapUtils.random(0, floors.length - 1);

				switch(tiles[x][y]) {
				case MapUtils.CORRIDOR:
				case MapUtils.FLOOR:
				case MapUtils.DOOR:
				case MapUtils.DOOR_CLOSED:
				case MapUtils.DOOR_LOCKED:
					terrain[x][y] = floors[f];
					break;
				}
			}
		}
		
		return terrain;
	}
}
