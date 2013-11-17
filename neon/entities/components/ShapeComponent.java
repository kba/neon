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

package neon.entities.components;

import java.awt.Rectangle;
import neon.entities.Entity;

@SuppressWarnings("serial")
public class ShapeComponent extends Rectangle implements Component {
	private Entity entity;
	
	public ShapeComponent(Entity entity, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.entity = entity;
	}
	
	public void setWidth(int width) {
		this.width = width;
		entity.physics.move(x + width/2, y + height/2);	
	}

	public void setHeight(int height) {
		this.height = height;
		entity.physics.move(x + width/2, y + height/2);	
	}
	
	public void setX(int x) {
		this.x = x;
		entity.physics.move(x + width/2, y + height/2);	
	}

	public void setY(int y) {
		this.y = y;
		entity.physics.move(x + width/2, y + height/2);	
	}
	
	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		entity.physics.move(x + width/2, y + height/2);		
	}

	@Override
	public long getUID() {
		return entity.getUID();
	}
}
