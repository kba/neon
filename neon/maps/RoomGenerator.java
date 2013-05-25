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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class RoomGenerator {
	/*
	 * Makes a room consisting of two interlocking rectangles.
	 */
	protected static Room makePolyRoom(int[][] tiles, Rectangle room) {
		Rectangle rec1 = MapUtils.randomRectangle(room.width/2, room);
		Rectangle rec2 = MapUtils.randomRectangle(room.width/2, room);
		
		for(int x = room.x + 1; x < room.x + room.width; x++) {
			for(int y = room.y + 1; y < room.y + room.height; y++) {
				if(rec1.contains(x, y) || rec2.contains(x, y)) {
					tiles[x][y] = MapUtils.FLOOR;				
				}
			}			
		}
		
		for(int x = room.x; x < room.x + room.width + 1; x++) {
			for(int y = room.y; y < room.y + room.height + 1; y++) {
				if(tiles[x][y] == MapUtils.WALL  && isCorner(tiles, x, y)) {
					tiles[x][y] = MapUtils.CORNER;				
				} else if(tiles[x][y] == MapUtils.WALL && exposed(tiles, x, y)){
//					System.out.println("exposed");
					tiles[x][y] = MapUtils.WALL_ROOM;					
				}
			}			
		}
		
		// om ervoor te zorgen dat de buitenmuur ook deel uitmaakt van de room
		room.width++;
		room.height++;
		
		return new Room(rec1, rec2);
	}
	
	/*
	 * Makes a simple rectangular room.
	 */
	protected static Room makeRoom(int[][] tiles, Rectangle room) {
		// kamer zelf bevloeren
		for(int x = room.x + 1; x < room.x + room.width; x++) {
			for(int y = room.y + 1; y < room.y + room.height; y++) {
				tiles[x][y] = MapUtils.FLOOR;
			}
		}
		
		// outline: hierlangs mogen tunnels binnenkomen
		for(int x = room.x; x < room.x + room.width + 1; x++) {
			if(tiles[x][room.y] != MapUtils.CORNER) {
				tiles[x][room.y] = MapUtils.WALL_ROOM;
			}
			if(tiles[x][room.y + room.height] != MapUtils.CORNER) {
				tiles[x][room.y + room.height] = MapUtils.WALL_ROOM;
			}
		}
		for(int y = room.y; y < room.y + room.height + 1; y++) {
			if(tiles[room.x][y] != MapUtils.CORNER) {
				tiles[room.x][y] = MapUtils.WALL_ROOM;
			}
			if(tiles[room.x + room.width][y] != MapUtils.CORNER) {
				tiles[room.x + room.width][y] = MapUtils.WALL_ROOM;
			}
		}

		// hoeken: hierlangs mogen tunnels niet binnenkomen
		tiles[room.x][room.y] = MapUtils.CORNER;
		tiles[room.x][room.y + room.height] = MapUtils.CORNER;
		tiles[room.x + room.width][room.y] = MapUtils.CORNER;
		tiles[room.x + room.width][room.y + room.height] = MapUtils.CORNER;
		
		// om ervoor te zorgen dat de buitenmuur ook deel uitmaakt van de room
		room.width++;
		room.height++;
		
		return new Room(new Rectangle(room));
	}
	
	/*
	 * Makes a room with irregular walls.
	 */
	protected static Room makeCaveRoom(int[][] tiles, Rectangle room) {
		Point2D center = new Point2D.Double(room.getCenterX(), room.getCenterY());
		Line2D line;
		float l = 0.8f;
		int d;
		RoundRectangle2D round = new RoundRectangle2D.Float(room.x, room.y, room.width, room.height, room.width, room.height);
		
		// floor tiles maken
		for(int x = room.x; x < room.x + room.width; x++) {
			d = MapUtils.random(1, (room.width + room.height)/3);
			line = new Line2D.Float(center, new Point(x, room.y));
			for(int lx = room.x + d; lx < room.x + room.width - d; lx++) {
				for(int ly = room.y + d; ly < room.y + room.height - d; ly++) {
					if(line.ptLineDist(lx, ly) < l && round.contains(lx, ly)) {
						tiles[lx][ly] = MapUtils.FLOOR;
					}
				}				
			}

			d = MapUtils.random(1, (room.width + room.height)/3);
			line = new Line2D.Float(center, new Point(x, room.y + room.height));
			for(int lx = room.x + d; lx < room.x + room.width - d; lx++) {
				for(int ly = room.y + d; ly < room.y + room.height - d; ly++) {
					if(line.ptLineDist(lx, ly) < l && round.contains(lx, ly)) {
						tiles[lx][ly] = MapUtils.FLOOR;
					}
				}				
			}
		}
		for(int y = room.y; y < room.y + room.height; y++) {
			d = MapUtils.random(1, (room.width + room.height)/3);
			line = new Line2D.Float(center, new Point(room.x, y));
			for(int lx = room.x + d; lx < room.x + room.width - d; lx++) {
				for(int ly = room.y + d; ly < room.y + room.height - d; ly++) {
					if(line.ptLineDist(lx, ly) < l && round.contains(lx, ly)) {
						tiles[lx][ly] = MapUtils.FLOOR;
					}
				}				
			}

			d = MapUtils.random(1, (room.width + room.height)/3);
			line = new Line2D.Float(center, new Point(room.x + room.width, y));
			for(int lx = room.x + d; lx < room.x + room.width - d; lx++) {
				for(int ly = room.y + d; ly < room.y + room.height - d; ly++) {
					if(line.ptLineDist(lx, ly) < l && round.contains(lx, ly)) {
						tiles[lx][ly] = MapUtils.FLOOR;
					}
				}				
			}
		}
		
		return new Room(room);
	}
	
	private static boolean isCorner(int[][] tiles, int x, int y) {
		int count = 0;
		if(y < tiles[x].length - 2 && isFloor(tiles[x][y + 1])) { count++; }
		if(y > 0 && isFloor(tiles[x][y - 1])) { count++; }
		if(x < tiles.length - 2 && isFloor(tiles[x + 1][y])) { count++; }
		if(x > 0 && isFloor(tiles[x - 1][y])) { count++; }
		if(x > 0 && y < tiles[x].length - 2 && isFloor(tiles[x - 1][y + 1])) { count++; }
		if(x > 0 && y > 0 && isFloor(tiles[x - 1][y - 1])) { count++; }
		if(x < tiles.length - 2 && y < tiles[x].length - 2 && isFloor(tiles[x + 1][y + 1])) { count++; }
		if(x < tiles.length - 2 && y > 0 && isFloor(tiles[x + 1][y - 1])) { count++; }
		return count == 1 && !isFloor(tiles[x][y]);
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

	private static boolean isFloor(int i) {
		return i == MapUtils.FLOOR || i == MapUtils.CORRIDOR || isDoor(i);
	}
	
	private static boolean isDoor(int i) {
		return i == MapUtils.DOOR || i == MapUtils.DOOR_LOCKED || i == MapUtils.DOOR_CLOSED;
	}
	
	protected static class Room {
		private Rectangle[] regions;
		
		public Room(Rectangle... regions) {
			this.regions = regions;
		}
		
		public Rectangle getBounds() {
			Rectangle bounds = new Rectangle(regions[0]);
			for(Rectangle r : regions) {
				bounds.add(r);
			}
			return bounds;
		}
	}
}
