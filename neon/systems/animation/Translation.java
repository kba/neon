/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012 - mdriesen
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

package neon.systems.animation;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import neon.entities.Entity;
import neon.entities.components.ShapeComponent;

public class Translation implements Runnable {
	private JComponent component;
	private Entity entity;
	private int x1, y1, x2, y2, interval;
	
	/**
	 * Translates an entity from one position to another.
	 * 
	 * @param entity
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param interval
	 */
	public Translation(Entity entity, int x1, int y1, int x2, int y2, int interval, JComponent component) {
		this.component = component;
		this.entity = entity;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.interval = interval;
	}
	
	public void run() {
		Rectangle bounds = entity.getComponent(ShapeComponent.class);
		bounds.setLocation(x1, y1);
		int distance = (int)Point.distance(x1, y1, x2, y2);
		for(int i = 0; i < distance + 1; i++) {
			int dx = (x2 - x1)*i/distance;
			int dy = (y2 - y1)*i/distance;
			bounds.setLocation(x1 + dx, y1 + dy);
//			System.out.println(entity.getBounds().getLocation());
			component.repaint();
			try {
				Thread.sleep(interval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
	}
}
