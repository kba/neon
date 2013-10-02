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

package neon.entities;

import neon.entities.components.PhysicsComponent;
import neon.entities.components.ShapeComponent;
import neon.ui.graphics.Renderable;

/**
 * This class represents a game entity that can be drawn on screen.
 * 
 * @author mdriesen
 */
public abstract class Entity {
	public final ShapeComponent bounds;
	public final PhysicsComponent physics;
	public Renderable renderer;	// waarom niet final?
	private final long uid;
	private final String id;
	
	public Entity(String id, long uid) {
		bounds = new ShapeComponent(this, 0, 0, 1, 1);
		physics = new PhysicsComponent(this);
		this.id = id;
		this.uid = uid;
	}

	public long getUID() {
		return uid;
	}
	
	public String getID() {
		return id;
	}

	public ShapeComponent getBounds() {
		return bounds;
	}
	
	public Renderable getRenderer() {
		return renderer;
	}
}
