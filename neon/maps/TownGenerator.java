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
import java.util.ArrayList;
import neon.core.Engine;
import neon.objects.EntityFactory;
import neon.objects.entities.Door;
import neon.objects.resources.RRegionTheme;
import neon.objects.resources.RTerrain;

/**
 * This class generates random towns.
 * 
 * @author mdriesen
 */
public class TownGenerator {
	private Zone zone;
	
	public TownGenerator(Zone zone) {
		this.zone = zone;
	}
	
	/**
	 * Generates a town cell with the given properties.
	 * 
	 * @param width		the width
	 * @param height	the height
	 * @param x			the x coordinate
	 * @param y			the y coordinate
	 */
	public void generate(int x, int y, int width, int height, RRegionTheme theme, int layer) {
		ArrayList<Rectangle> temp1;
		
		// region verdelen in willekeurig rechthoeken
		if(theme.id.equals("town_big")) {
			temp1 = BlocksGenerator.generateBSPRectangles(width/2, height/2, 4, 8);
		} else if(theme.id.equals("town_small")) {
			temp1 = BlocksGenerator.generatePackedRectangles(width/2, height/2, 4, 8, 2, 10);
		} else {
			temp1 = BlocksGenerator.generateSparseRectangles(width/2, height/2, 4, 8, 2, 20);
		}

		for(Rectangle r : temp1) {
			if(r != null) {
				RTerrain wall = (RTerrain)Engine.getResources().getResource(theme.wall, "terrain");
				Region house = new Region(wall.id, x + width/4 + r.x, y + height/4 + r.y, 
						r.width - 1, r.height - 1, null, (byte)(layer + 1), wall);
				makeDoor(house, theme);
				zone.addRegion(house);
			}
		}
	}

	private void makeDoor(Region r, RRegionTheme theme) {
		// ergens deur tussen moffelen
		int x = 0, y = 0;
		
		switch((int)(Math.random()*4)) {
		case 0: x = r.getX() + 1; y = r.getY(); break;
		case 1: x = r.getX() + 1; y = r.getHeight() + r.getY() - 1; break;
		case 2: x = r.getX(); y = r.getY() + 1; break;
		case 3: x = r.getWidth() + r.getX() - 1; y = r.getY() + 1; break;
		}
		
		long uid = Engine.getStore().createNewEntityUID();
		Door door = (Door)EntityFactory.getItem(theme.door, x, y, uid);
		Engine.getStore().addEntity(door);
		door.lock.close();
		zone.addItem(door);
		RTerrain rt = (RTerrain)Engine.getResources().getResource(theme.floor, "terrain");
		zone.addRegion(new Region(theme.floor, x , y, 1, 1, null, r.z + 1, rt));
	}
}
