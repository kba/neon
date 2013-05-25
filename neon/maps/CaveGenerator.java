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

import java.awt.geom.Area;

public class CaveGenerator {
	protected static int[][] generateOpenCave(int width, int height, int sparseness) {
		int[][] tiles = makeTiles(MazeGenerator.generateSquashedMaze(width, height, 3), width, height);

		// thinning out the numbers
		for(int i = 0; i < sparseness; i ++) {
			for(int x = 1; x < tiles.length - 1; x++) {	
				for(int y = 1; y < tiles[x].length - 1; y++) {
					int count = 0;
					if(tiles[x][y + 1] == MapUtils.FLOOR) { count++; }
					if(tiles[x][y - 1] == MapUtils.FLOOR) { count++; }
					if(tiles[x + 1][y] == MapUtils.FLOOR) { count++; }
					if(tiles[x - 1][y] == MapUtils.FLOOR) { count++; }
					if(tiles[x - 1][y + 1] == MapUtils.FLOOR) { count++; }
					if(tiles[x - 1][y - 1] == MapUtils.FLOOR) { count++; }
					if(tiles[x + 1][y + 1] == MapUtils.FLOOR) { count++; }
					if(tiles[x + 1][y - 1] == MapUtils.FLOOR) { count++; }
					if(count > 4 && tiles[x][y] == MapUtils.WALL) {
						tiles[x][y] = MapUtils.TEMP;
					}
				}
			}

			for(int x = 1; x < tiles.length - 1; x++) {	// TEMPs vervangen
				for(int y = 1; y < tiles[x].length - 1; y++) {		
					if(tiles[x][y] == MapUtils.TEMP) { 
						tiles[x][y] = MapUtils.FLOOR; 
					}
				}
			}
		}

		return tiles;
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
}
